/*
 * Created on 21-Jan-2003 at 15:15:56.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant;

import de.enough.polish.*;
import de.enough.polish.ant.build.BuildSetting;
import de.enough.polish.ant.build.Source;
import de.enough.polish.ant.info.InfoSetting;
import de.enough.polish.ant.requirements.Requirements;
import de.enough.polish.exceptions.InvalidComponentException;
import de.enough.polish.preprocess.PreprocessException;
import de.enough.polish.preprocess.Preprocessor;
import de.enough.polish.util.TextUtil;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.types.Path;
import org.jdom.JDOMException;

import java.io.*;
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

	private BuildSetting buildSetting;
	private InfoSetting infoSetting;
	private Requirements deviceRequirements;
	
	/** the project settings */ 
	private Project polishProject;
	/** the manager of all devices */
	private DeviceManager deviceManager;
	/** the actual devices which are supported by this project */
	private Device[] devices;
	private Preprocessor preprocessor;
	private File[] sourceDirs;
	private String[][] sourceFiles;
	private Path midp1BootClassPath;
	private Path midp2BootClassPath;
	private HashMap apiPaths;
	private String apiDir;
	
	/**
	 * Creates a new empty task 
	 */
	public PolishTask() {
		// initialisation is done with the setter-methods.
	}
	
	public void addConfiguredInfo( InfoSetting setting ) {
		if (setting.getName() == null ) {
			throw new BuildException("The nested element [info] needs the attribute [name] which defines the name of this project.");
		}
		this.infoSetting = setting;
	}
	
	public void addConfiguredDeviceRequirements( Requirements requirements ) {
		this.deviceRequirements = requirements;
	}
	
	/**
	 * Adds the build settings for this project.
	 * 
	 * @param setting the build settings.
	 */
	public void addConfiguredBuild( BuildSetting setting ) {
		if (setting.getMidlets() == null || setting.getMidlets().length == 0) {
			throw new BuildException("Midlets need to be defined in the build section with either <midlets> or <midlet>.");
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
		for ( int i=0; i<devCount; i++) {
			Device device = this.devices[i];
			preprocess( device );
			compile( device );
			obfuscate( device );
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
				// init debug manager
				debugManager = new DebugManager( this.buildSetting.getDebugSetting() );
			} catch (PreprocessException e) {
				throw new BuildException( e.getMessage() );
			}
		}
		// create project settings:
		this.polishProject = new Project( this.buildSetting.usesPolishGui(), isDebugEnabled, debugManager );
		this.polishProject.addFeature(this.buildSetting.getImageLoadStrategy());
		if (debugManager != null && this.buildSetting.getDebugSetting().isVisual()) {
			this.polishProject.addFeature("debug.visual");
		}
		Capability[] variables = this.buildSetting.getVariables();
		if (variables != null) {
			for (int i = 0; i < variables.length; i++) {
				Capability var = variables[i];
				this.polishProject.addDirectCapability( var );
			}
		}
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
		//TODO allow a different seperation of source-URLs as well
		String[] urls = TextUtil.split( source.getUrl(), ':' );
		if (source.getPolish() != null) {
			this.sourceDirs = new File[ urls.length + 1];
			this.sourceFiles = new String[ urls.length + 1][]; 
			File polishDir = new File( source.getPolish() );
			if (!polishDir.exists()) {
				throw new BuildException("The polish source-directory [" + source.getPolish() + "] does not exist. Please check your settings of the [polish] attribute in the <source> element of the <build> section.");
			}
			this.sourceDirs[ urls.length ] = polishDir;
			dirScanner.setBasedir(polishDir);
			dirScanner.scan();
			this.sourceFiles[ urls.length ] = dirScanner.getIncludedFiles();
		} else {
			this.sourceDirs = new File[ urls.length ];
			this.sourceFiles = new String[ urls.length][]; 
		}
		for (int i = 0; i < urls.length; i++) {
			File dir = new File( urls[i] );
			if (!dir.exists()) {
				throw new BuildException("The source-directory [" + urls[i] + "] does not exist. Please check your settings in the <source> element of the <build> section.");
			}
			this.sourceDirs[i] = dir; 
			dirScanner.setBasedir(dir);
			dirScanner.scan();
			this.sourceFiles[i] = dirScanner.getIncludedFiles();
			//TODO read source files content
		}
		
		// init boot class path:
		this.midp1BootClassPath = new Path( this.project, this.buildSetting.getMidp1Path());
		this.midp2BootClassPath = new Path( this.project, this.buildSetting.getMidp2Path());
		
		// init path for device APIs:
		this.apiPaths = new HashMap();
		this.apiDir = "./import";
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
		try {
			String targetDir = this.buildSetting.getTargetDir().getAbsolutePath() 
				+ "/" + device.getIdentifier()
				+ "/source";
			this.preprocessor.setTargetDir( targetDir );
			this.preprocessor.setSymbols( device.getFeatures() );
			this.preprocessor.setVariables( device.getCapabilities() );
			System.out.println("now preprocessing for device [" +  device.getIdentifier() + "]." );
			for (int i = 0; i < this.sourceDirs.length; i++) {
				File sourceDir = this.sourceDirs[i];
				String[] files = this.sourceFiles[i];
				//System.out.println("current source dir: " + sourceDir );				
				for (int j = 0; j < files.length; j++) {
					String file = files[j];
					//System.out.println("preprocesssing " + file);
					this.preprocessor.preprocess( sourceDir, file);
				}
			}
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
		javac.setTaskName(getTaskName() + "-" + device.getIdentifier() );
		//TODO check if javac.target=1.1 is really needed
		javac.setTarget("1.1");
		
		System.out.println("now compiling for device [" +  device.getIdentifier() + "]." );
		String targetBase = this.buildSetting.getTargetDir().getAbsolutePath() 
			+ "/" + device.getIdentifier();
		File targetDir = new File( targetBase + "/classes" );
		if (!targetDir.exists()) {
			targetDir.mkdirs();
		}
		javac.setDestdir( targetDir );
		javac.setSrcdir(new Path( this.project,  targetBase + "/source") );
		if (device.isMidp1()) {
			javac.setBootclasspath(this.midp1BootClassPath);
		} else {
			javac.setBootclasspath(this.midp2BootClassPath);
		}
		
		// check for each supported api, if the appropriate path-property
		// has been set: mmapi = ${polish.api.mmapi}
		// when this has not been defined, just look in the import-dir
		String[] apis = device.getSupportedApis();
		StringBuffer classPath = new StringBuffer();
		Hashtable properties = this.project.getProperties(); 
		for (int i = 0; i < apis.length; i++) {
			String api = apis[i];
			String path = (String) this.apiPaths.get( api );
			if (path != null) {
				classPath.append( ':' )
						 .append( path );
			} else {
				path = (String) properties.get( "polish.api." + api );
				if (path == null) {
					//TODO use system specific path-seperator(if needed by Path):
					path = this.apiDir + "/" + api + ".jar:" + this.apiDir + "/" + api + ".zip";
				}  
				//TODO use system specific path-seperator (if needed by Path):
				classPath.append(':')
						 .append( path );
				this.apiPaths.put( api, path );
			}
		}
		if (apis.length > 0) {
			javac.setClasspath( new Path(this.project, classPath.toString().substring(1) ) );
			System.out.println( "using classpath [" + classPath.toString().substring(1) + "]." );
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
	 * Obfuscates the compiled source code.
	 *  
	 * @param device The device for which the obfuscation should be done.
	 */
	private void obfuscate( Device device ) {
		// TODO enough implement obfuscate
		
	}

	/**
	 * Preverifies the compiled and a\obfuscated code.
	 *  
	 * @param device The device for which the preverification should be done.
	 */
	private void preverify( Device device ) {
		// TODO enough implement preverify
		
	}
	
	/**
	 * Jars the code.
	 * 
	 * @param device The device for which the code should be jared.
	 */
	private void jar( Device device ) {
		// TODO enough implement jar
		
	}

	/**
	 * Creates the JAD file.
	 * 
	 * @param device The device for which a JAD file should be created. 
	 */
	private void jad( Device device ) {
		// TODO implement jad()
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
	

}
