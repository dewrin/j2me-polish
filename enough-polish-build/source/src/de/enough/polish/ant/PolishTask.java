/*
 * Created on 21-Jan-2003 at 15:15:56.
 *
 * Copyright (c) 2004 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.ant;

import de.enough.polish.*;
import de.enough.polish.ant.build.*;
import de.enough.polish.ant.info.InfoSetting;
import de.enough.polish.ant.requirements.Requirements;
import de.enough.polish.exceptions.InvalidComponentException;
import de.enough.polish.obfuscate.Obfuscator;
import de.enough.polish.preprocess.*;
import de.enough.polish.util.*;

import org.apache.tools.ant.*;
import org.apache.tools.ant.taskdefs.*;
import org.apache.tools.ant.types.Path;
import org.jdom.JDOMException;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Manages a J2ME project from the preprocessing to the packaging and obfuscation.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        21-Jan-2003 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class PolishTask extends ConditionalTask {

	private static final String VERSION = "1.0.0-RC1";

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
	private Obfuscator obfuscator;
	private String[] preserveClasses;
	private StyleSheet styleSheet;
	private ImportConverter importConverter;
	private TextFile styleSheetFile;
	private ResourceUtil resourceUtil;
	private String wtkHome;
	private HashMap midletClassesByName;
	private static final Pattern START_APP_PATTERN = 
		Pattern.compile("\\s*void\\s+startApp\\s*\\(\\s*\\)");

	private LibraryManager libraryManager;
	
	/**
	 * Creates a new empty task 
	 */
	public PolishTask() {
		// initialisation is done with the setter-methods.
		// if you should use the PolishTask not within an ant-build.xml
		// then make sure to set the project with .setProject(...)
		this.resourceUtil = new ResourceUtil( getClass().getClassLoader() );
	}
	
	public void addConfiguredInfo( InfoSetting setting ) {
		if (setting.getName() == null ) {
			throw new BuildException("The nested element <info> requires the attribute [name] which defines the name of this project.");
		}
		if (setting.getlicense() == null) {
			throw new BuildException("The nested element <info> requires the attribute [license] with either \"GPL\" for open source software or the commercial license, which can be obtained at http://www.j2mepolish.org.");
		}
		this.infoSetting = setting;
	}
	
	public void addConfiguredDeviceRequirements( Requirements requirements ) {
		if (requirements.isActive(this.project)) {
			this.deviceRequirements = requirements;
		}
	}
	
	/**
	 * Creates and adds the build settings for this project.
	 * 
	 * @return the new build setting.
	 */
	public BuildSetting createBuild() {
		this.buildSetting = new BuildSetting( this.project );
		return this.buildSetting;
	}
	
	/**
	 * Executes this task. 
	 * For all selected devices the source code will be preprocessed,
	 * compiled, obfuscated and jared.
	 * 
	 * @throws BuildException when the build failed.
	 */
	public void execute() throws BuildException {
		if (!isActive()) {
			return;
		}
		checkSettings();
		initProject();
		selectDevices();
		int devCount = this.devices.length;
		if (devCount > 1) {
			System.out.println("Processing [" + devCount + "] devices...");
		}
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
		// check the nested element of <build>:
		Midlet[] midlets = this.buildSetting.getMidlets(); 
		if (midlets == null || midlets.length == 0) {
			throw new BuildException("Midlets need to be defined in the build section with either <midlets> or <midlet>.");
		}
		// check if the ant-property WTK_HOME has been set:
		//e.g. with: <property name="wtk.home" value="c:\Java\wtk-1.0.4"/>
		this.wtkHome = this.project.getProperty("wtk.home");
		if (this.buildSetting.getPreverify() == null) {
			// no preverify has been set, that's okay when the wtk.home ant-property has been set:
			if (this.wtkHome == null) { 
				throw new BuildException("Nested element [build] needs to define the attribute [preverify] which points to the preverify-executable of the wireless toolkit. Alternatively you can set the home directory of the Wireless Toolkit by defining the Ant-property [wtk.home]: <property name=\"wtk.home\" location=\"/home/user/WTK2.1\"/>");
			}
			if (!this.wtkHome.endsWith( File.separator )) {
				this.wtkHome += File.separator;
			}
			String preverifyPath = this.wtkHome + "bin" + File.separator + "preverify";
			if ( File.separatorChar == '\\') {
				preverifyPath += ".exe";
			}
			File preverifyFile = new File( preverifyPath );
			if (preverifyFile.exists()) {
				this.buildSetting.setPreverify( preverifyFile );
			} else {
				// probably the wtk.home path is wrong:
				File file = new File( this.wtkHome );
				if (!file.exists()) {
					throw new BuildException("The Ant-property [wtk.home] points to a non-existing directory. Please adjust his setting in the build.xml file.");
				} else {
					throw new BuildException("Unable to find the preverify tool at the default location [" + preverifyPath + "]. Please specify where to find it with the \"preverify\"-attribute of the <build> element (in the build.xml file).");
				}
			}
		}
	}
	
	/**
	 * Initialises this project and instantiates several helper classes.
	 */
	private void initProject() {
		// create debug manager:
		boolean isDebugEnabled = this.buildSetting.isDebugEnabled(); 
		DebugManager debugManager = null;
		if (isDebugEnabled) {
			try {
				debugManager = new DebugManager( this.buildSetting.getDebugSetting() );
			} catch (BuildException e) {
				throw new BuildException( e.getMessage(), e );
			}
		}
		// create project settings:
		this.polishProject = new PolishProject( this.buildSetting.usesPolishGui(), isDebugEnabled, debugManager );
		if (debugManager != null && debugManager.isVerbose()) {
			this.polishProject.addFeature("debugVerbose");
		}
		this.polishProject.addCapability("license", this.infoSetting.getlicense() );
		// add some specified features:
		this.polishProject.addFeature(this.buildSetting.getImageLoadStrategy());
		if (debugManager != null && this.buildSetting.getDebugSetting().useGui()) {
			this.polishProject.addFeature("useDebugGui");
		}
		FullScreenSetting fullScreenSetting = this.buildSetting.getFullScreenSetting();
		if (fullScreenSetting.isMenu()) {
			this.polishProject.addFeature("useMenuFullScreen");
			this.polishProject.addFeature("useFullScreen");
		} else if (fullScreenSetting.isEnabled()) {
			this.polishProject.addFeature("useFullScreen");
		}
		// add all ant properties if desired: 
		if (this.buildSetting.includeAntProperties()) {
			Hashtable antProperties = this.project.getProperties();
			Set keySet = antProperties.keySet();
			for (Iterator iter = keySet.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				this.polishProject.addDirectCapability( key, (String) antProperties.get(key) );
			}
		}
		// add all variables from the build.xml:
		Variable[] variables = this.buildSetting.getVariables();
		if (variables != null) {
			for (int i = 0; i < variables.length; i++) {
				Variable var = variables[i];
				//System.out.println("adding variable [" + var.getName() + "]." );
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
		
		// create LibraryManager:
		try {
			this.libraryManager = new LibraryManager( this.project.getProperties(), this.buildSetting.getApiDir().getAbsolutePath(), this.wtkHome, this.buildSetting.getPreverify().getAbsolutePath(), this.buildSetting.openApis() );
		} catch (JDOMException e) {
			throw new BuildException("unable to create api manager: " + e.getMessage(), e );
		} catch (IOException e) {
			throw new BuildException("unable to create api manager: " + e.getMessage(), e );
		} catch (InvalidComponentException e) {
			throw new BuildException("unable to create api manager: " + e.getMessage(), e );
		}

		// create vendor/group/device manager:
		try {
			VendorManager vendorManager = new VendorManager( this.polishProject, this.buildSetting.getVendors());
			DeviceGroupManager groupManager = new DeviceGroupManager( this.buildSetting.getGroups() ); 
			this.deviceManager = new DeviceManager( vendorManager, groupManager, this.libraryManager, this.buildSetting.getDevices() );
		} catch (JDOMException e) {
			throw new BuildException("unable to create device manager: " + e.getMessage(), e );
		} catch (IOException e) {
			throw new BuildException("unable to create device manager: " + e.getMessage(), e );
		} catch (InvalidComponentException e) {
			throw new BuildException("unable to create device manager: " + e.getMessage(), e );
		}
		this.preprocessor = new Preprocessor( this.polishProject, null, null, null, false, true, null );
		
		
		//	initialise the preprocessing-source-directories:
		DirectoryScanner dirScanner = new DirectoryScanner();
		dirScanner.setIncludes( new String[]{"**/*.java"} );
		File[] dirs = this.buildSetting.getSourceDirs();
		this.sourceDirs = new File[ dirs.length + 1];
		this.sourceFiles = new TextFile[ dirs.length + 1][];
		if (this.buildSetting.getPolishDir() != null) {
			// there is an explicit J2ME Polish directory:
			File polishDir = this.buildSetting.getPolishDir();
			this.sourceDirs[ 0 ] = polishDir;
			dirScanner.setBasedir(polishDir);
			dirScanner.scan();
			this.sourceFiles[ 0 ] = getTextFiles( polishDir,  dirScanner.getIncludedFiles() );
		} else {
			// the J2ME Polish sources need to be loaded from the jar-file:
			long lastModificationTime = 0;
			File jarFile = new File("import/enough-j2mepolish-build.jar");
			if (jarFile.exists()) {
				lastModificationTime = jarFile.lastModified();
			} else {
				jarFile = new File("lib/enough-j2mepolish-build.jar");
				if (jarFile.exists()) {
					lastModificationTime = jarFile.lastModified();
				}
			}
			this.sourceDirs[ 0 ] = new File("src");
			try {
				String[] fileNames = this.resourceUtil.readTextFile("build/j2mepolish.index.txt");
				this.sourceFiles[ 0 ] = getTextFiles( "src", fileNames, lastModificationTime );
			} catch (IOException e) {
				throw new BuildException("Unable to load the J2ME source files from enough-j2mepolish-build.jar: " + e.getMessage(), e );
			}
		}
		// load the normal source files:
		for (int i = 0; i < dirs.length; i++) {
			File dir = dirs[i];
			if (!dir.exists()) {
				throw new BuildException("The source-directory [" + dir.getAbsolutePath() + "] does not exist. Please check your settings in the [sourceDir] attribute of the <build> element.");
			}
			this.sourceDirs[i+1] = dir; 
			dirScanner.setBasedir(dir);
			dirScanner.scan();
			this.sourceFiles[i+1] = getTextFiles( dir,  dirScanner.getIncludedFiles() );
		}
		if (this.buildSetting.usesPolishGui() && this.styleSheetFile == null) {
			throw new BuildException("Did not find the file [StyleSheet.java] of the J2ME Polish GUI framework. Please adjust the [polishDir] attribute of the <build> element in the [build.xml] file. The [polishDir]-attribute should point to the directory which contains the J2ME Polish-Java-sources.");
		}
		
		// init boot class path:
		this.midp1BootClassPath = new Path( this.project, this.buildSetting.getMidp1Path().getAbsolutePath());
		this.midp2BootClassPath = new Path( this.project, this.buildSetting.getMidp2Path().getAbsolutePath());
				
		// init obfuscator:
		ObfuscatorSetting obfuscatorSetting = this.buildSetting.getObfuscatorSetting();
		if ((obfuscatorSetting != null) && (obfuscatorSetting.isEnabled())) {
			String[] keepClasses = obfuscatorSetting.getPreserveClassNames();
			String[] midletClasses = this.buildSetting.getMidletClassNames();
			this.preserveClasses = new String[ keepClasses.length + midletClasses.length ];
			System.arraycopy( keepClasses, 0, this.preserveClasses, 0,  keepClasses.length );
			System.arraycopy( midletClasses, 0, this.preserveClasses, keepClasses.length, midletClasses.length  );
			this.obfuscator = Obfuscator.getInstance( obfuscatorSetting.getName(), obfuscatorSetting.getClassName() );
		}
		
		// init import manager:
		this.importConverter = new ImportConverter();
		
		// init base style sheet:
		if (this.buildSetting.usesPolishGui()) {
			File cssFile = new File( this.buildSetting.getResDir().getAbsolutePath() + File.separatorChar + "polish.css");
			if (!cssFile.exists()) {
				log("Unable to find polish.css at [" + cssFile.getAbsolutePath() + "] - you should create this file when you want to make most of the J2ME Polish GUI.", Project.MSG_WARN );
				this.styleSheet = new StyleSheet();
			} else {
				CssReader cssReader = new CssReader();
				try {
					cssReader.add(cssFile);
				} catch (IOException e) {
					throw new BuildException("Unable to load polish.css: " + e.getMessage(), e );
				}
				this.styleSheet = cssReader.getStyleSheet();
			}
		}
		
		// set the names of the midlets:
		this.midletClassesByName = new HashMap();
		String[] midletClasses = this.buildSetting.getMidletClassNames();
		for (int i = 0; i < midletClasses.length; i++) {
			this.midletClassesByName.put( midletClasses[i], Boolean.TRUE );			
		}
	}

	/**
	 * Creates an array of text files.
	 * 
	 * @param baseDir The base directory.
	 * @param fileNames The full names of the files.
	 * @return an array of text-files
	 */
	private TextFile[] getTextFiles(File baseDir, String[] fileNames) 
	{
		TextFile[] files = new TextFile[ fileNames.length ];
		for (int i = 0; i < fileNames.length; i++) {
			String fileName = fileNames[i];
			try {
				TextFile file = new TextFile( baseDir.getAbsolutePath(), fileName );
				if (fileName.endsWith("StyleSheet.java") && fileName.startsWith("de")) {
					this.styleSheetFile = file;
				}
				files[i] = file;
			} catch (FileNotFoundException e) {
				throw new BuildException("Unable to load java source [" + fileName + "]: " + e.getMessage(), e );
			}
		}
		return files;
	}

	/**
	 * Creates an array of text files and loads them from the jar-file.
	 * 
	 * @param baseDir The base directory.
	 * @param fileNames The full names of the files.
	 * @param lastModificationTime the time of the last modification of the files
	 * @return an array of text-files
	 */
	private TextFile[] getTextFiles(String baseDir, String[] fileNames, long lastModificationTime) 
	{
		TextFile[] files = new TextFile[ fileNames.length ];
		for (int i = 0; i < fileNames.length; i++) {
			String fileName = fileNames[i];
			TextFile file = new TextFile( baseDir, fileName, lastModificationTime, this.resourceUtil );
			if ("de/enough/polish/ui/StyleSheet.java".equals(fileName)) {
				this.styleSheetFile = file;
			}
			files[i] = file;
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
		System.out.println("preprocessing for device [" +  device.getIdentifier() + "]." );
		try {
			int numberOfChangedFiles = 0;
			String targetDir = this.buildSetting.getWorkDir().getAbsolutePath() 
				+ File.separatorChar + device.getVendorName() 
				+ File.separatorChar + device.getName();
			device.setBaseDir( targetDir );
			targetDir += File.separatorChar + "source";
			device.setSourceDir(targetDir);
			// initialise the preprocessor:
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
			this.preprocessor.setUsePolishGui(usePolishGui);
			long lastCssModification = 0;
			StyleSheet cssStyleSheet = null;
			if (usePolishGui) {
				cssStyleSheet = loadStyleSheet( device );
				lastCssModification = cssStyleSheet.lastModified();
				this.preprocessor.setSyleSheet( cssStyleSheet );
				if (cssStyleSheet.containsDynamicStyles()) {
					this.preprocessor.addSymbol("polish.useDynamicStyles");
				} else {
					this.preprocessor.removeSymbol("polish.useDynamicStyles");
				}
				if (cssStyleSheet.containsBeforeStyle()) {
					this.preprocessor.addSymbol("polish.useBeforeStyle");
				} else {
					this.preprocessor.removeSymbol("polish.useBeforeStyle");
				}
				if (cssStyleSheet.containsAfterStyle()) {
					this.preprocessor.addSymbol("polish.useAfterStyle");
				} else {
					this.preprocessor.removeSymbol("polish.useAfterStyle");
				}
			} else {
				this.preprocessor.removeSymbol("polish.useDynamicStyles");
				this.preprocessor.removeSymbol("polish.useBeforeStyle");
				this.preprocessor.removeSymbol("polish.useAfterStyle");
			}
			StringList styleSheetCode = null;
			boolean usesTicker = false;
			// preprocess each source file:
			for (int i = 0; i < this.sourceDirs.length; i++) {
				File sourceDir = this.sourceDirs[i];
				this.preprocessor.addVariable( "polish.source", sourceDir.getAbsolutePath() );
				TextFile[] files = this.sourceFiles[i];
				//System.out.println("current source dir: " + sourceDir );
				// preprocess each file in that source-dir:
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
					// 3. The build.xml has been modified since the last run
					// 4. One of the polish.css files has been modified since the last run 
					// when only the CSS files have changed
					boolean saveInAnyCase =  ( !targetFile.exists() )
							 || ( sourceLastModified > targetLastModified )
							 || ( buildXmlLastModified > targetLastModified ); 
					boolean preprocess = ( saveInAnyCase )
							 || ( lastCssModification > targetLastModified);
					if (   preprocess ) {
						// preprocess this file:
						StringList sourceCode = new StringList( file.getContent() );
						// generate the class-name from the file-name:
						String className = file.getFileName();
						if (className.endsWith(".java")) {
							className = className.substring(0, className.length() - 5 );
						}
						className = TextUtil.replace(className, File.separatorChar, '.' );
						// set the StyleSheet.display variable in all MIDlets
						if ( (this.midletClassesByName.get( className ) != null) 
								&& usePolishGui) {
							insertDisplaySetting( className, sourceCode );
							sourceCode.reset();
						}
						int result = this.preprocessor.preprocess( className, sourceCode );
						// only think about saving when the file should not be skipped 
						// and when it is not the StyleSheet.java file:
						if (file == this.styleSheetFile ) {
							styleSheetCode = sourceCode;
						} else  if (result != Preprocessor.SKIP_FILE) {
							sourceCode.reset();
							// now replace the import statements:
							boolean changed = this.importConverter.processImports(usePolishGui, device.isMidp1(), sourceCode);
							if (changed) {
								result = Preprocessor.CHANGED;
							}
							// save modified file:
							if ( ( saveInAnyCase ) 
							  || (result == Preprocessor.CHANGED) ) 
							{
								// now process the getTicker() and setTicker() calls:
								usesTicker = TickerConverter.convertTickerCalls(sourceCode);
								//System.out.println( "preprocessed [" + className + "]." );
								file.saveToDir(targetDir, sourceCode.getArray(), false );
								numberOfChangedFiles++;
							//} else {
							//	System.out.println("not saving " + file.getFileName() );
							}
						//} else {
						//	System.out.println("Skipping file " + file.getFileName() );
						}
					} // when preprocessing should be done.
				} // for each file
			} // for each source folder
			
			if (usesTicker) {
				//TODO rob add preprocessing symbol for J2ME Polish library,
				// so that the ticker class can be removed completely
			}
			// now all files have been preprocessed.
			// Now the StyleSheet.java file needs to be written,
			// but only when the polish GUI should be used:
			if (usePolishGui) {
				// check if the CSS declarations have changed since the last run:
				File targetFile = new File( targetDir + File.separatorChar + this.styleSheetFile.getFileName() );				
				boolean cssIsNew = (!targetFile.exists())
					|| ( lastCssModification > targetFile.lastModified() )
					|| ( buildXmlLastModified > targetFile.lastModified() );
				if (cssIsNew) {
					//System.out.println("CSS is new and the style sheet will be generated.");
					if (styleSheetCode == null) {
						// the style sheet has not been preprocessed:
						styleSheetCode = new StringList( this.styleSheetFile.getContent() );
						String className = "de.enough.polish.ui.StyleSheet";
						this.preprocessor.preprocess( className, styleSheetCode );
					}
					// now insert the CSS information for this device
					// into the StyleSheet.java source-code:
					CssConverter cssConverter = new CssConverter();
					styleSheetCode.reset();
					cssConverter.convertStyleSheet(styleSheetCode, 
							this.preprocessor.getStyleSheet(),
							device,
							this.preprocessor ); 				
					this.styleSheetFile.saveToDir(targetDir, styleSheetCode.getArray(), false );
					numberOfChangedFiles++;
				//} else {
				//	System.out.println("CSSS is not new - last CSS modification == " + lastCssModification + " <= StyleSheet.java.lastModified() == " + targetFile.lastModified() );
				}
				
			}
			device.setNumberOfChangedFiles( numberOfChangedFiles );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new BuildException( e.getMessage() );
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException( e.getMessage() );
		} catch (BuildException e) {
			e.printStackTrace();
			throw new BuildException( e.getMessage() );
		}
	}
	
	/**
	 * Sets the StyleSheet.display variable in a MIDlet class.
	 * 
	 * @param className the name of the class
	 * @param sourceCode the source code
	 * @throws BuildException when the startApp()-method could not be found
	 */
	private void insertDisplaySetting( String className, StringList sourceCode ) {
		// at first try to find the startApp method:
		while (sourceCode.next()) {
			String line = sourceCode.getCurrent();
			Matcher matcher = START_APP_PATTERN.matcher(line);
			if (matcher.find()) {
				int lineIndex = sourceCode.getCurrentIndex();
				while ((line.indexOf('{') == -1) && (sourceCode.next()) ) {
					line = sourceCode.getCurrent();
				}
				if (!sourceCode.hasNext()) {
					throw new BuildException("Unable to process MIDlet [" + className + "]: startApp method is not opened with '{': line [" + (++lineIndex) + "].");
				}
				sourceCode.insert("StyleSheet.display = Display.getDisplay( this );");
				return;
			}
		}
		System.out.println(START_APP_PATTERN.pattern());
		throw new BuildException("Unable to find startApp method in MIDlet [" + className + "].");

	}

	/**
	 * Reads the style sheet for the given device.
	 * 
	 * @param device the device
	 * @return the style sheet for that device
	 * @throws IOException when a sub-style sheet could not be loaded.
	 */
	private StyleSheet loadStyleSheet(Device device) throws IOException {
		String resDir = this.buildSetting.getResDir().getAbsolutePath()
						+ File.separator;
		// initialise the CSS-style sheet:
		long lastCssModification = this.styleSheet.lastModified();
		
		CssReader cssReader = new CssReader( this.styleSheet );
		
		// add the vendor style sheet:
		Vendor vendor = device.getVendor();
		StyleSheet sheet = vendor.getStyleSheet();
		if ( sheet == null ) {
			// CSS file has not been read yet:
			File cssFile = new File( resDir + vendor.getIdentifier() 
					+ File.separator + "polish.css");
			if (cssFile.exists()) {
				CssReader reader = new CssReader( this.styleSheet );
				reader.add( cssFile );
				sheet = reader.getStyleSheet();
				vendor.setStyleSheet( sheet );
			} else {
				sheet = new StyleSheet();
				vendor.setStyleSheet( sheet );
			}
		}
		cssReader.add( sheet );
		if (sheet.lastModified() > lastCssModification) {
			lastCssModification = sheet.lastModified();
		}
		
		// now add the CSS files of the groups:
		DeviceGroup[] groups = device.getGroups();
		for (int i = 0; i < groups.length; i++) {
			DeviceGroup group = groups[i];
			sheet = group.getStyleSheet();
			if (sheet == null) {
				File cssFile = new File( resDir + group.getIdentifier() 
						+ File.separator + "polish.css");
				if (cssFile.exists()) {
					CssReader reader = new CssReader( this.styleSheet );
					reader.add( cssFile );
					sheet = reader.getStyleSheet();
					group.setStyleSheet( sheet );
				} else {
					sheet = new StyleSheet();
					group.setStyleSheet( sheet );
				}
			}
			cssReader.add(sheet);
			if (sheet.lastModified() > lastCssModification) {
				lastCssModification = sheet.lastModified();
			}
		}
		
		// now add the style sheet of the device:
		// CSS file has not been read yet:
		File cssFile = new File( resDir + vendor.getIdentifier() 
				+ File.separator + device.getName() + File.separator + "polish.css");
		if (cssFile.exists()) {
			CssReader reader = new CssReader( this.styleSheet );
			reader.add( cssFile );
			sheet = reader.getStyleSheet();
			cssReader.add( sheet );
		} else {
			sheet = new StyleSheet();
		}
		if (sheet.lastModified() > lastCssModification) {
			lastCssModification = sheet.lastModified();
		}
		sheet = cssReader.getStyleSheet();
		sheet.setLastModified(lastCssModification);
		return sheet;
	}

	/**
	 * Compiles the source code.
	 *  
	 * @param device The device for which the source code should be compiled.
	 */
	private void compile( Device device ) {
		// SETTING OF CLASSPATH:
		// check for each supported api, if the appropriate path-property
		// has been set: mmapi = ${polish.api.mmapi}
		// when this has not been defined, just look in the import-dir
		String[] classPaths = this.libraryManager.getClassPaths(device);
		device.setClassPaths( classPaths );
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < classPaths.length; i++) {
			String path = classPaths[i];
			buffer.append( path )
			      .append( File.pathSeparatorChar );
		}
		device.setClassPath( buffer.toString() );
		
		/*
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
			device.setClassPath(classPath);
			//System.out.println( "using classpath [" + classPath.toString().substring(1) + "]." );
		}
		*/
		// setting target directory:
		String targetDirName = device.getBaseDir() + File.separatorChar + "classes";
		device.setClassesDir( targetDirName );
		
		if (device.getNumberOfChangedFiles() == 0) {
			System.out.println("nothing to compile for device [" +  device.getIdentifier() + "]." );
			return;			
		}
		System.out.println("compiling for device [" +  device.getIdentifier() + "]." );
		
		// init javac task:
		Javac javac = new Javac();
		javac.setProject( this.project );
		javac.setTaskName(getTaskName() + "-javac-" + device.getIdentifier() );
		//javac.target=1.1 is needed for the preverification:
		javac.setTarget("1.1");
		File targetDir = new File( targetDirName );
		if (!targetDir.exists()) {
			targetDir.mkdirs();
		}
		javac.setDestdir( targetDir );
		javac.setSrcdir(new Path( this.project,  device.getSourceDir() ) );
		javac.setSourcepath(new Path( this.project,  "" ));
		if (device.isMidp1()) {
			javac.setBootclasspath(this.midp1BootClassPath);
		} else {
			javac.setBootclasspath(this.midp2BootClassPath);
		}
		if (device.getClassPath() != null) {
			javac.setClasspath( new Path(this.project, device.getClassPath() ) );
		}
		// start compile:
		try {
			javac.execute();
		} catch (BuildException e) {
			System.out.println("If an error occured in the J2ME Polish packages, please try a clean rebuild - e.g. [ant clean] and then [ant].");
			System.out.println("Alternatively you might need to define where to find the device-APIs. Following classpath has been used: [" + device.getClassPath() + "].");
			throw new BuildException( "Unable to compile source code for device [" + device.getIdentifier() + "]: " + e.getMessage(), e );
		}
		
	}


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
		File preverify = this.buildSetting.getPreverify();
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
			preverify.getAbsolutePath(), 
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
		System.out.println("creating JAR for device [" + device.getIdentifier() + "]." );
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
			resourceDir = new File( resourcePath + device.getVendorName() );
			if (resourceDir.exists()) {
				files = resourceDir.listFiles( cssFilter );
				FileUtil.copy( files, classesDir );
			}
			// 3. copy group resources:
			String[] groups = device.getGroupNames();
			for (int i = 0; i < groups.length; i++) {
				String group = groups[i];
				resourceDir = new File( resourcePath + group );
				if (resourceDir.exists()) {
					files = resourceDir.listFiles( cssFilter );
					FileUtil.copy( files, classesDir );
				}
			}
			// 4.: copy device resources:
			resourceDir = new File( resourcePath + device.getVendorName() 
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
		infoProperties.put( "polish.vendor", device.getVendorName() );
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
		String test = this.polishProject.getCapability("polish.license");
		if ( !this.infoSetting.getlicense().equals(test)) {
			throw new BuildException("Encountered invalid license.");
		}
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
			String[] midletInfos = this.buildSetting.getMidletInfos( this.infoSetting.getIcon() );
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
		infoProperties.put( "polish.vendor", device.getVendorName() );
		infoProperties.put( "polish.version", this.infoSetting.getVersion() );
		String jarName = this.infoSetting.getJarName();
		jarName = PropertyUtil.writeProperties(jarName, infoProperties);
		infoProperties.put( "polish.jarName", jarName );
		
		// now create the JAD file:
		System.out.println("creating JAD for device [" + device.getIdentifier() + "].");
		Jad jad = new Jad();
		Variable[] jadAttributes = this.infoSetting.getJadAttributes();
		for (int i = 0; i < jadAttributes.length; i++) {
			Variable var =jadAttributes[i];
			jad.addAttribute( var.getName() , PropertyUtil.writeProperties( var.getValue(), infoProperties) );
		}
		// add build properties - midlet infos:
		String[] midletInfos = this.buildSetting.getMidletInfos( this.infoSetting.getIcon() );
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
	 * <p>copyright Enough Software 2004</p>
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
			//TODO enough also filter settings.xml
			if ( ("css".equals( extension )) || ("CSS".equals(extension)) ) {
				return false;
			} else {
				return true;
			}
		}
	}
	

}
