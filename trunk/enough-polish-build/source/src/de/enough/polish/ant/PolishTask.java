/*
 * Created on 21-Jan-2003 at 15:15:56.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant;

import de.enough.polish.*;
import de.enough.polish.ant.build.*;
import de.enough.polish.ant.info.InfoSetting;
import de.enough.polish.ant.requirements.Requirements;
import de.enough.polish.exceptions.InvalidComponentException;
import de.enough.polish.obfuscate.Obfuscator;
import de.enough.polish.preprocess.*;
import de.enough.polish.preprocess.PreprocessException;
import de.enough.polish.preprocess.Preprocessor;
import de.enough.polish.util.*;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.*;
import org.apache.tools.ant.types.Path;
import org.jdom.JDOMException;

import java.io.*;
import java.util.*;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>Manages a J2ME project from the preprocessing to the packaging and obfuscation.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        21-Jan-2003 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class PolishTask extends ConditionalTask {

	private static final String VERSION = "0.3.1";

	private BuildSetting buildSetting;
	private InfoSetting infoSetting;
	private Requirements deviceRequirements;
	
	/** the project settings */ 
	private PolishProject polishProject;
	/** the manager of all devices */
	private DeviceManager deviceManager;
	/** the actual devices which are supported by this project */
	private Device[] devices;
	private Preprocessor preprocessor;
	private File[] sourceDirs;
	private TextFile[][] sourceFiles;
	private Path midp1BootClassPath;
	private Path midp2BootClassPath;
	private HashMap apiPaths;
	private File apiDir;
	private Obfuscator obfuscator;
	private String[] preserveClasses;
	
	/**
	 * Creates a new empty task 
	 */
	public PolishTask() {
		// initialisation is done with the setter-methods.
		//this.quote = File.pathSeparatorChar == '/' ? '\'' : '"';
	}
	
	public void addConfiguredInfo( InfoSetting setting ) {
		if (setting.getName() == null ) {
			throw new BuildException("The nested element [info] needs the attribute [name] which defines the name of this project.");
		}
		this.infoSetting = setting;
	}
	
	public void addConfiguredDeviceRequirements( Requirements requirements ) {
		if (requirements.isActive(this.project)) {
			this.deviceRequirements = requirements;
		}
	}
	
	/**
	 * Adds the build settings for this project.
	 * 
	 * @param setting the build settings.
	 */
	public void addConfiguredBuild( BuildSetting setting ) {
		Midlet[] midlets = setting.getMidlets(); 
		if (midlets == null || midlets.length == 0) {
			throw new BuildException("Midlets need to be defined in the build section with either <midlets> or <midlet>.");
		}
		if (setting.getPreverify() == null) {
			throw new BuildException("Nested element [build] needs to define the attribute [preverify] which points to the preverify-executable of the wireless toolkit.");
		}
		this.buildSetting = setting;
	}
	
	public void execute() throws BuildException {
		if (!isActive()) {
			return;
		}
		checkSettings();
		initProject();
		selectDevices();
		int devCount = this.devices.length;
		System.out.println("Processing [" + devCount + "] devices...");
		boolean obfuscate = this.buildSetting.doObfuscate();
		for ( int i=0; i<devCount; i++) {
			Device device = this.devices[i];
			preprocess( device );
			compile( device );
			if (obfuscate) {
				obfuscate( device );
			}
			preverify( device );
			jar( device );
			jad( device );
		}
		test();
		deploy();
	}

	/**
	 * Checks the settings of this task.
	 */
	private void checkSettings() {
		if (this.infoSetting == null) {
			throw new BuildException("Nested element [info] is required.");
		}
		if (this.buildSetting == null) {
			throw new BuildException("Nested element [build] is required.");
		}
		if (this.deviceRequirements == null) {
			log("Nested element [deviceRequirements] is missing, now the project will be optimized for all known devices.");
		}
	}
	
	/**
	 * Initialises this project and instanciates several helper classes.
	 */
	private void initProject() {
		// create debug manager:
		boolean isDebugEnabled = this.buildSetting.isDebugEnabled(); 
		DebugManager debugManager = null;
		if (isDebugEnabled) {
			try {
				debugManager = new DebugManager( this.buildSetting.getDebugSetting() );
			} catch (PreprocessException e) {
				throw new BuildException( e.getMessage() );
			}
		}
		// create project settings:
		this.polishProject = new PolishProject( this.buildSetting.usesPolishGui(), isDebugEnabled, debugManager );
		this.polishProject.addFeature(this.buildSetting.getImageLoadStrategy());
		if (debugManager != null && this.buildSetting.getDebugSetting().isVisual()) {
			this.polishProject.addFeature("debug.visual");
		}
		// add all ant properties: 
		Hashtable antProperties = this.project.getProperties();
		Set keySet = antProperties.keySet();
		for (Iterator iter = keySet.iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			this.polishProject.addDirectCapability( key, (String) antProperties.get(key) );
		}
		// add all variables from the build.xml:
		Variable[] variables = this.buildSetting.getVariables();
		if (variables != null) {
			for (int i = 0; i < variables.length; i++) {
				Variable var = variables[i];
				System.out.println("adding variable [" + var.getName() + "]." );
				this.polishProject.addDirectCapability( var );
			}
		}
		// add all symbols from the build.xml:
		String symbolDefinition = this.buildSetting.getSymbols();
		if (symbolDefinition != null) {
			String[] symbols = TextUtil.splitAndTrim( symbolDefinition, ',' );
			for (int i = 0; i < symbols.length; i++) {
				this.polishProject.addDirectFeature( symbols[i] );
			}
		}
		
		// create vendor/group/device manager:
		try {
			VendorManager vendorManager = new VendorManager( this.polishProject, this.buildSetting.getVendors());
			DeviceGroupManager groupManager = new DeviceGroupManager( this.buildSetting.getGroups() ); 
			this.deviceManager = new DeviceManager( vendorManager, groupManager, this.buildSetting.getDevices() );
		} catch (JDOMException e) {
			throw new BuildException("unable to create device manager: " + e.getMessage(), e );
		} catch (IOException e) {
			throw new BuildException("unable to create device manager: " + e.getMessage(), e );
		} catch (InvalidComponentException e) {
			throw new BuildException("unable to create device manager: " + e.getMessage(), e );
		}
		this.preprocessor = new Preprocessor( this.polishProject, null, null, null, false, false, true, null );
		
		//	initialise the preprocessing-source-directories:
		DirectoryScanner dirScanner = new DirectoryScanner();
		dirScanner.setIncludes( new String[]{"**/*.java"} );
		Source source = this.buildSetting.getSource();
		String url = source.getUrl();
		char splitChar = url.indexOf(':') != -1 ? ':' : ';';
		String[] urls = TextUtil.splitAndTrim( url, splitChar );
		if (source.getPolish() != null) {
			this.sourceDirs = new File[ urls.length + 1];
			this.sourceFiles = new TextFile[ urls.length + 1][]; 
			File polishDir = new File( source.getPolish() );
			if (!polishDir.exists()) {
				throw new BuildException("The polish source-directory [" + source.getPolish() + "] does not exist. Please check your settings of the [polish] attribute in the <source> element of the <build> section.");
			}
			this.sourceDirs[ urls.length ] = polishDir;
			dirScanner.setBasedir(polishDir);
			dirScanner.scan();
			this.sourceFiles[ urls.length ] = getTextFiles( source.getPolish(),  dirScanner.getIncludedFiles() );
		} else {
			this.sourceDirs = new File[ urls.length ];
			this.sourceFiles = new TextFile[ urls.length][]; 
		}
		for (int i = 0; i < urls.length; i++) {
			String dirName = urls[i];
			File dir = new File( dirName );
			if (!dir.exists()) {
				throw new BuildException("The source-directory [" + urls[i] + "] does not exist. Please check your settings in the <source> element of the <build> section.");
			}
			this.sourceDirs[i] = dir; 
			dirScanner.setBasedir(dir);
			dirScanner.scan();
			this.sourceFiles[i] = getTextFiles( dirName,  dirScanner.getIncludedFiles() );
		}
		
		// init boot class path:
		this.midp1BootClassPath = new Path( this.project, this.buildSetting.getMidp1Path());
		this.midp2BootClassPath = new Path( this.project, this.buildSetting.getMidp2Path());
		
		// init path for device APIs:
		this.apiPaths = new HashMap();
		this.apiDir = this.buildSetting.getApiDir();
		
		// init obfuscator:
		ObfuscatorSetting obfuscatorSetting = this.buildSetting.getObfuscatorSetting();
		if (obfuscatorSetting.isEnabled()) {
			String[] keepClasses = obfuscatorSetting.getPreserveClassNames();
			String[] midletClasses = this.buildSetting.getMidletClassNames();
			this.preserveClasses = new String[ keepClasses.length + midletClasses.length ];
			System.arraycopy( keepClasses, 0, this.preserveClasses, 0,  keepClasses.length );
			System.arraycopy( midletClasses, 0, this.preserveClasses, keepClasses.length, midletClasses.length  );
			this.obfuscator = Obfuscator.getInstance( obfuscatorSetting.getName(), obfuscatorSetting.getClassName() );
		}
	}

	/**
	 * Creates an array of text files.
	 * 
	 * @param baseDir The base directory.
	 * @param fileNames The full names of the files.
	 * @return an array of text-files
	 */
	private TextFile[] getTextFiles(String baseDir, String[] fileNames) 
	{
		TextFile[] files = new TextFile[ fileNames.length ];
		for (int i = 0; i < fileNames.length; i++) {
			String fileName = fileNames[i];
			try {
				files[i] = new TextFile( baseDir, fileName );
			} catch (FileNotFoundException e) {
				throw new BuildException("Unable to load java source [" + fileName + "]: " + e.getMessage(), e );
			}
		}
		return files;
	}

	/**
	 * Selects the actual devices for which optimal applications should be generated.
	 */
	private void selectDevices() {
		if (this.deviceRequirements == null) {
			this.devices = this.deviceManager.getDevices();
			if (this.devices == null || this.devices.length == 0) {
				throw new BuildException("The [devices.xml] file does not define any devices at all - please specify a correct devices-file." );
			}
		} else {
			this.devices = this.deviceRequirements.filterDevices( this.deviceManager.getDevices() );
			if (this.devices == null || this.devices.length == 0) {
				throw new BuildException("Your device-requirements are too strict - no device fulfills them." );
			}
		}
	}
	
	/**
	 * Preprocesses the source code for all devices.
	 * 
	 * @param device The device for which the preprocessing should be done.
	 */
	private void preprocess( Device device ) {
		System.out.println("now preprocessing for device [" +  device.getIdentifier() + "]." );
		try {
			String targetDir = this.buildSetting.getWorkDir().getAbsolutePath() 
				+ File.separatorChar + device.getVendor() 
				+ File.separatorChar + device.getName();
			device.setBaseDir( targetDir );
			targetDir += File.separatorChar + "source";
			device.setSourceDir(targetDir);
			this.preprocessor.setTargetDir( targetDir );
			this.preprocessor.setSymbols( device.getFeatures() );
			this.preprocessor.setVariables( device.getCapabilities() );
			// get the last modfication time of the build.xml file
			// so that it can be checked whether there are any changes at all:
			File buildXml = new File( this.project.getBaseDir().getAbsolutePath() 
						+ File.separatorChar + "build.xml" );
			long buildXmlLastModified = buildXml.lastModified();
			// check if the polish gui is used at all:
			boolean usePolishGui = this.buildSetting.usesPolishGui()
								  && device.supportsPolishGui();
			for (int i = 0; i < this.sourceDirs.length; i++) {
				File sourceDir = this.sourceDirs[i];
				this.preprocessor.addVariable( "polish.source", sourceDir.getAbsolutePath() );
				TextFile[] files = this.sourceFiles[i];
				//System.out.println("current source dir: " + sourceDir );				
				for (int j = 0; j < files.length; j++) {
					TextFile file = files[j];
					// check if file needs to be preprocessed at all:
					long sourceLastModified = file.lastModified();
					File targetFile = new File( targetDir
							+ File.separatorChar + file.getFileName() );
					long targetLastModified = targetFile.lastModified();
					// preprocess this file, but only when there can
					// be changes at all - this could be when
					// 1. The preprocessed file does not yet exists
					// 2. The source file has been modified since the last run
					// 3, The build.xml has been modified since the last run
					boolean saveInAnyCase =  ( !targetFile.exists() )
							 || ( sourceLastModified > targetLastModified );
					/*
					if (( sourceLastModified > targetLastModified )) {
						System.out.println("File has been modified: " + file.getFileName() 
								+ "\n last mod of source: " + sourceLastModified + " last mod of target: " + targetLastModified );
					}
					if (( buildXmlLastModified > targetLastModified ) ) {
						System.out.println("build.xml has been modified: " + file.getFileName() 
								+ "\n last mod of target: " + targetLastModified + " last mod of build.xml: " + buildXmlLastModified );
					}
					*/
					if (   ( saveInAnyCase )
						|| ( buildXmlLastModified > targetLastModified ) )
					{
						// preprocess this file:
						StringList sourceCode = new StringList( file.getContent() );
						int result = this.preprocessor.preprocess( file.getFileName(), sourceCode );
						if (result != Preprocessor.SKIP_FILE) {
							if (usePolishGui) {
								// now replace the import statements:
							}
							// save modified file:
							if ( ( saveInAnyCase ) 
							  || (result == Preprocessor.CHANGED) ) 
							{
								file.saveToDir(targetDir, sourceCode.getArray(), false );
							//} else {
							//	System.out.println("not saving " + file.getFileName() );
							}
						} else {
							System.out.println("Skipping file " + file.getFileName() );
						}
					} // when preprocessing should be done.
				} // for each file
			} // for each source folder
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new BuildException( e.getMessage() );
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException( e.getMessage() );
		} catch (PreprocessException e) {
			e.printStackTrace();
			throw new BuildException( e.getMessage() );
		}
	}

	/**
	 * Compiles the source code.
	 *  
	 * @param device The device for which the source code should be compiled.
	 */
	private void compile( Device device ) {
		Javac javac = new Javac();
		javac.setProject( this.project );
		javac.setTaskName(getTaskName() + "-javac-" + device.getIdentifier() );
		//javac.target=1.1 is needed for the preverification:
		javac.setTarget("1.1");
		
		System.out.println("now compiling for device [" +  device.getIdentifier() + "]." );
		String targetDirName = device.getBaseDir() + File.separatorChar + "classes";
		device.setClassesDir( targetDirName );
		File targetDir = new File( targetDirName );
		if (!targetDir.exists()) {
			targetDir.mkdirs();
		}
		javac.setDestdir( targetDir );
		javac.setSrcdir(new Path( this.project,  device.getSourceDir() ) );
		if (device.isMidp1()) {
			javac.setBootclasspath(this.midp1BootClassPath);
		} else {
			javac.setBootclasspath(this.midp2BootClassPath);
		}
		
		// check for each supported api, if the appropriate path-property
		// has been set: mmapi = ${polish.api.mmapi}
		// when this has not been defined, just look in the import-dir
		String apisStr = device.getSupportedApisAsString();
		if(apisStr == null) {
			apisStr = "";
		}  
		// check if the class path has been resolved before:
		String classPath = (String) this.apiPaths.get( apisStr );
		if (classPath == null) {
			String[] apis = device.getSupportedApis();
			if (apis == null) {
				apis = new String[0];
			}
			StringBuffer classPathBuffer = new StringBuffer();
			Hashtable properties = this.project.getProperties(); 
			for (int i = 0; i < apis.length; i++) {
				String api = apis[i];
				String path = (String) this.apiPaths.get( api );
				if (path != null) {
					classPathBuffer.append( ':' )
							 .append( path );
				} else {
					path = (String) properties.get( "polish.api." + api );
					if (path == null) {
						path = this.apiDir.getAbsolutePath() + File.separatorChar + api + ".jar" 
							 + File.pathSeparatorChar 
							 + this.apiDir.getAbsolutePath() + File.separatorChar + api + ".zip";
					}  
					classPathBuffer.append( File.pathSeparatorChar )
							 .append( path );
					this.apiPaths.put( api, path );
				}
			} // for each api
			if (apis.length > 0) {
				classPath = classPathBuffer.toString().substring(1);
				this.apiPaths.put(apisStr, classPath );
			}
		}
		if (classPath != null) {
			javac.setClasspath( new Path(this.project, classPath ) );
			device.setClassPath(classPath);
			//System.out.println( "using classpath [" + classPath.toString().substring(1) + "]." );
		}
		
		// start compile:
		try {
			javac.execute();
		} catch (BuildException e) {
			e.printStackTrace();
			throw new BuildException( "Unable to compile source code for device [" + device.getIdentifier() + "]: " + e.getMessage() );
		}
		
	}



	/**
	 * Adds quotes around a path when it contains whitespace.
	 *
	 * @param path The path
	 * @return The path with quotes when it contains whitespace or without quotes
     * 		   when it does not contain whitespace.
	private String addQuotes(String path) {
		if (true) {
			return path;
		}
		if (path.indexOf(' ') != -1) {
			return this.quote + path + this.quote;
		} else {
			return path;
		}
	}
	 */


	/**
	 * Obfuscates the compiled source code.
	 *  
	 * @param device The device for which the obfuscation should be done.
	 */
	private void obfuscate( Device device ) {
		System.out.println("obfuscating for device [" + device.getIdentifier() + "].");
		Path bootPath;
		if (device.isMidp1()) {
			bootPath = this.midp1BootClassPath;
		} else {
			bootPath = this.midp2BootClassPath;
		}
		File sourceFile = new File( this.buildSetting.getWorkDir().getAbsolutePath()
				+ File.separatorChar + "source.jar");
		//jar classes dir:
		long time = System.currentTimeMillis();
		try {
			JarUtil.jar( new File( device.getClassesDir()), sourceFile, false );
		} catch (IOException e) {
			throw new BuildException("Unable to prepare the obfuscation-jar: " + e.getMessage(), e );
		}
		System.out.println("Jaring took " + ( System.currentTimeMillis() - time) + " ms.");
		File destFile = new File( this.buildSetting.getWorkDir().getAbsolutePath()
				+ File.separatorChar + "dest.jar");
		this.obfuscator.obfuscate(device, sourceFile, destFile, this.preserveClasses, bootPath );
		//unjar destFile to build/obfuscated:
		time = System.currentTimeMillis();
		try {
			String targetDir = device.getBaseDir() + File.separatorChar + "obfuscated";
			device.setClassesDir(targetDir);
			JarUtil.unjar( destFile, new File( targetDir )   );
		} catch (IOException e) {
			throw new BuildException("Unable to prepare the obfuscation-jar: " + e.getMessage(), e );
		}
	}

	/**
	 * Preverifies the compiled and a\obfuscated code.
	 *  
	 * @param device The device for which the preverification should be done.
	 */
	private void preverify( Device device ) {
		System.out.println("preverifying for device [" + device.getIdentifier() + "].");
		String preverify = this.buildSetting.getPreverify();
		String classPath;
		if (device.isMidp1()) {
			classPath = this.midp1BootClassPath.toString();
		} else {
			classPath = this.midp2BootClassPath.toString();
		}
		classPath += File.pathSeparatorChar + device.getClassPath();
		/* File preverfyDir = new File( this.buildSetting.getWorkDir().getAbsolutePath()
				+ File.separatorChar + "preverfied" );
		device.setPreverifyDir( preverifyDir ); 
		*/
		String[] commands = new String[] {
			preverify, 
			"-classpath", classPath,
			"-d", device.getClassesDir(), // destination-dir - default is ./output
			"-cldc",
			device.getClassesDir()
		};
		StringBuffer commandBuffer = new StringBuffer();
		for (int i = 0; i < commands.length; i++) {
			commandBuffer.append( commands[i] ).append(' ');
		}
		
		try {
			Process preverifyProc = Runtime.getRuntime().exec( commands, null );
			InputStream in = preverifyProc.getErrorStream();
			int ch;
			StringBuffer message = new StringBuffer();
			while ( (ch = in.read()) != -1) {
				message.append((char) ch );
			}
			int exitValue = preverifyProc.waitFor();
			if (exitValue != 0) {
				throw new BuildException("Unable to preverify: " + message.toString()  
					+ " - The exit-status is [" + exitValue + "]\n"
					+ " The call was: [" + commandBuffer.toString() + "]."
				);
			}
		} catch (IOException e ) {
			throw new BuildException("Unable to preverify: " + e.getMessage()
					+ "\n The call was: [" + commandBuffer.toString() + "].", e);
		} catch (InterruptedException e) {
			throw new BuildException("Unable to preverify: " + e.getMessage()
					+ "\n The call was: [" + commandBuffer.toString() + "].", e);
		}
		
	}

	/**
	 * Jars the code.
	 * 
	 * @param device The device for which the code should be jared.
	 */
	private void jar( Device device ) {
		System.out.println("creating jar for device [" + device.getIdentifier() + "]." );
		File classesDir = new File( device.getClassesDir() );
		//copy resources:
		File resourceDir = this.buildSetting.getResDir();
		String resourcePath = resourceDir.getAbsolutePath() + File.separatorChar; 
		
		FileFilter cssFilter =  new CssFileFilter();
		try {
			// 1. copy general resources:
			File[] files = resourceDir.listFiles( cssFilter );
			FileUtil.copy( files, classesDir );
			// 2. copy vendor resources:
			resourceDir = new File( resourcePath + device.getVendor() );
			if (resourceDir.exists()) {
				files = resourceDir.listFiles( cssFilter );
				FileUtil.copy( files, classesDir );
			}
			// 3.: copy group resources:
			String[] groups = device.getGroups();
			for (int i = 0; i < groups.length; i++) {
				String group = groups[i];
				resourceDir = new File( resourcePath + group );
				if (resourceDir.exists()) {
					files = resourceDir.listFiles( cssFilter );
					FileUtil.copy( files, classesDir );
				}
			}
			// 4.: copy device resources:
			resourceDir = new File( resourcePath + device.getVendor() 
								+ File.separatorChar + device.getName() );
			if (resourceDir.exists()) {
				files = resourceDir.listFiles( cssFilter );
				FileUtil.copy( files, classesDir );
			}
		} catch (IOException e) {
			throw new BuildException("Unable to copy resources from [" + resourceDir + "]: " + e.getMessage(), e );
		}
		
		Jar jarTask = new Jar();
		jarTask.setProject( this.project );
		jarTask.setTaskName( getTaskName() + "-jar-" + device.getIdentifier() );
		jarTask.setBasedir( classesDir );
		// retrieve the name of the jar-file:
		HashMap infoProperties = new HashMap();
		infoProperties.put( "polish.identifier", device.getIdentifier() );
		infoProperties.put( "polish.name", device.getName() );
		infoProperties.put( "polish.vendor", device.getVendor() );
		infoProperties.put( "polish.version", this.infoSetting.getVersion() );
		String jarName = this.infoSetting.getJarName();
		jarName = PropertyUtil.writeProperties(jarName, infoProperties);
		infoProperties.put( "polish.jarName", jarName );
		File jarFile = new File( this.buildSetting.getDestDir().getAbsolutePath() 
						+ File.separatorChar + jarName );
		if (!jarFile.getParentFile().exists()) {
			jarFile.getParentFile().mkdirs();
		}
		device.setJarFile( jarFile );
		jarTask.setDestFile( jarFile );
		//create manifest:
		try {
			Manifest manifest = new Manifest();
			Manifest.Attribute polishVersion = new Manifest.Attribute("Polish-Version", VERSION );
			manifest.addConfiguredAttribute( polishVersion  );
			String midp = InfoSetting.MIDP1;
			if (device.isMidp2()) {
				midp = InfoSetting.MIDP2;
			}
			Manifest.Attribute meProfile = new Manifest.Attribute( InfoSetting.MICRO_EDITION_PROFILE, midp );
			manifest.addConfiguredAttribute(meProfile);
			
			// add info attributes:
			Variable[] attributes = this.infoSetting.getManifestAttributes();
			for (int i = 0; i < attributes.length; i++) {
				Variable var = attributes[i];
				String value = PropertyUtil.writeProperties(var.getValue(), infoProperties);
				Manifest.Attribute attribute = new Manifest.Attribute(var.getName(), value );
				manifest.addConfiguredAttribute( attribute  );
			}
			// add build properties - midlet infos:
			String[] midletInfos = this.buildSetting.getMidletInfos();
			for (int i = 0; i < midletInfos.length; i++) {
				String info = midletInfos[i];
				Manifest.Attribute attribute = new Manifest.Attribute(InfoSetting.NMIDLET + (i+1), info );
				manifest.addConfiguredAttribute( attribute  );
			}
			jarTask.addConfiguredManifest( manifest );
		} catch (ManifestException e) {
			e.printStackTrace();
			throw new BuildException("Unable to create manifest: " + e.getMessage(), e );
		}
		jarTask.execute();
		
	}
	
	/**
	 * Creates the JAD file for the given device.
	 * 
	 * @param device The device for which the JAD file should be created.
	 */
	private void jad(Device device) {
		HashMap infoProperties = new HashMap();
		infoProperties.put( "polish.identifier", device.getIdentifier() );
		infoProperties.put( "polish.name", device.getName() );
		infoProperties.put( "polish.vendor", device.getVendor() );
		infoProperties.put( "polish.version", this.infoSetting.getVersion() );
		String jarName = this.infoSetting.getJarName();
		jarName = PropertyUtil.writeProperties(jarName, infoProperties);
		infoProperties.put( "polish.jarName", jarName );
		
		// now create the JAD file:
		System.out.println("Now creating JAD file for device [" + device.getIdentifier() + "].");
		Jad jad = new Jad();
		Variable[] jadAttributes = this.infoSetting.getJadAttributes();
		for (int i = 0; i < jadAttributes.length; i++) {
			Variable var =jadAttributes[i];
			jad.addAttribute( var.getName() , PropertyUtil.writeProperties( var.getValue(), infoProperties) );
		}
		// add build properties - midlet infos:
		String[] midletInfos = this.buildSetting.getMidletInfos();
		for (int i = 0; i < midletInfos.length; i++) {
			String info = midletInfos[i];
			jad.addAttribute( InfoSetting.NMIDLET + (i+1), info );
		}
		// add size of jar:
		//TODO check if File.length changes after the file has been changed.
		long size = device.getJarFile().length();
		jad.addAttribute(  InfoSetting.MIDLET_JAR_SIZE, "" + size );
		
		String jadName = PropertyUtil.writeProperties( jarName.substring(0, jarName.lastIndexOf('.') ) + ".jad", infoProperties );
		File jadFile = new File( this.buildSetting.getDestDir().getAbsolutePath() + File.separatorChar + jadName );
		try {
			FileUtil.writeTextFile(jadFile, jad.getContent() );
		} catch (IOException e) {
			throw new BuildException("Unable to create JAD file [" + jadFile.getAbsolutePath() +"] for device [" + device.getIdentifier() + "]: " + e.getMessage() );
		}
	}
	
	/**
	 * 
	 */
	private void test() {
		// TODO enough implement test
		
	}

	/**
	 * 
	 */
	private void deploy() {
		// TODO enough implement deploy
		
	}
	
	/**
	 * <p>Accepts only non CSS-files.</p>
	 *
	 * <p>copyright enough software 2004</p>
	 * <pre>
	 * history
	 *        19-Feb-2004 - rob creation
	 * </pre>
	 * @author Robert Virkus, robert@enough.de
	 */
	class CssFileFilter implements FileFilter {

		/* (non-Javadoc)
		 * @see java.io.FileFilter#accept(java.io.File)
		 */
		public boolean accept(File file) {
			if (file.isDirectory()) {
				return false;
			}
			String extension = file.getName();
			int extPos = extension.lastIndexOf('.'); 
			if ( extPos != -1) {
				extension = extension.substring( extPos + 1 ); 
			}
			if ( ("css".equals( extension )) || ("CSS".equals(extension)) ) {
				return false;
			} else {
				return true;
			}
		}
	}
	

}
