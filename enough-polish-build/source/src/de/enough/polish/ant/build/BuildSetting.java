/*
 * Created on 22-Jan-2003 at 14:10:02.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.build;

import de.enough.polish.*;
import de.enough.polish.util.ResourceUtil;
import de.enough.polish.util.TextUtil;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import java.io.*;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * <p>Represents the build settings of a polish J2ME project.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        22-Jan-2003 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class BuildSetting {
	
	public final static String IMG_LOAD_BACKGROUND = "images.backgroundLoad";
	public final static String IMG_LOAD_FOREGROUND = "images.directLoad";
	
	private DebugSetting debugSetting;
	private MidletSetting midletSetting; 
	private ObfuscatorSetting obfuscatorSetting;
	private File workDir;
	private File apiDir;
	private File destDir;
	private File resDir;
	private File sourceDir;
	private String symbols;
	private String imageLoadStrategy;
	private FullScreenSetting fullScreenSetting;
	private File devices;
	private File vendors;
	private File groups;
	private Variable[] variables;
	private boolean usePolishGui;
	private File midp1Path;
	private File midp2Path;
	private File preverify;
	private Project project;
	private boolean includeAntProperties;
	private ResourceUtil resourceUtil;
	private ArrayList sourceDirs;
	private File polishDir;
	
	/**
	 * Creates a new build setting.
	 * 
	 * @param project The corresponding ant-project.
	 */
	public BuildSetting( Project project ) {
		this.project = project;
		this.workDir = new File("build");
		this.destDir = new File("dist");
		this.apiDir = new File("import");
		this.resDir = new File ("resources");
		this.sourceDirs = new ArrayList();
		this.midp1Path = new File( "import/midp1.jar" );
		this.midp2Path = new File( "import/midp2.jar" );
		this.imageLoadStrategy = IMG_LOAD_FOREGROUND;
		this.resourceUtil = new ResourceUtil( this.getClass().getClassLoader() );
	}
	
	public void addConfiguredObfuscator( ObfuscatorSetting setting ) {
		if (setting.isActive(this.project)) {
			this.obfuscatorSetting = setting;
		}
	}
	
	public void addConfiguredMidlets( MidletSetting setting ) {
		if (this.midletSetting != null) {
			throw new BuildException("Please use either <midlets> or <midlet> to define your midlets!");
		}
		this.midletSetting = setting;
	}
	
	public void addConfiguredMidlet( Midlet midlet ) {
		if (this.midletSetting == null ) {
			this.midletSetting = new MidletSetting();
		}
		this.midletSetting.addConfiguredMidlet( midlet );
	}
	
	public void addConfiguredFullscreen( FullScreenSetting setting ) {
		if (this.fullScreenSetting != null) {
			throw new BuildException("Please use either the attribute [fullscreen] or the nested element [fullscreen], but not both!");
		}
		this.fullScreenSetting = setting;
	}
	
	public void addConfiguredDebug( DebugSetting setting ) {
		if (setting.isActive(this.project)) {
			this.debugSetting = setting;
		}
	}
	
	public void addConfiguredVariables( Variables vars ) {
		this.includeAntProperties = vars.includeAntProperties();
		this.variables = vars.getVariables();
	}
	
	public Variable[] getVariables() {
		return this.variables;
	}
	
	/**
	 * @return Returns the includeAntProperties.
	 */
	public boolean includeAntProperties() {
		return this.includeAntProperties;
	}
	
	public void setSymbols( String symbols ) {
		this.symbols = symbols;
	}
	
	public void setUsePolishGui( boolean usePolishGui ) {
		this.usePolishGui = usePolishGui; 
	}
		
	/**
	 * Determines whether this project should use the polish GUI at all.
	 * The GUI is only used when the current device allows the use of the GUI.
	 * The GUI makes no sense for devices with black and white screens,
	 * for example.
	 * 
	 * @return true when this projects wants to use the polish GUI
	 */
	public boolean usesPolishGui() {
		return this.usePolishGui;
	}
	
	public void setImageLoadStrategy( String strategy ) {
		if ("background".equalsIgnoreCase(strategy) ) {
			this.imageLoadStrategy = IMG_LOAD_BACKGROUND;
		} else if ("foreground".equalsIgnoreCase(strategy)) {
			this.imageLoadStrategy = IMG_LOAD_FOREGROUND;
		} else {
			throw new BuildException("The build-attribute [imageLoadStrategy] needs to be either [background] or [foreground]. "
					+ "The strategy [" + strategy + "] is not supported.");
		}
	}
	
	/**
	 * Retrieves the strategy by which images should be loaded.
	 * 
	 * @return either IMG_LOAD_BACKGROUND or IMG_LOAD_FOREGROUND
	 * @see #IMG_LOAD_BACKGROUND
	 * @see #IMG_LOAD_FOREGROUND
	 */
	public String getImageLoadStrategy() {
		return this.imageLoadStrategy;
	}
	
	public void setFullscreen( String setting ) {
		if (this.fullScreenSetting != null) {
			throw new BuildException("Please use either the attribute [fullscreen] or the nested element [fullscreen], but not both!");
		}
		this.fullScreenSetting = new FullScreenSetting();
		if ("menu".equalsIgnoreCase(setting)) {
			this.fullScreenSetting.setEnable( true );
			this.fullScreenSetting.setMenu( true );
		} else if ("yes".equalsIgnoreCase(setting) || "true".equalsIgnoreCase(setting)) {
			this.fullScreenSetting.setEnable( true );
		} else if ("no".equalsIgnoreCase(setting) || "false".equalsIgnoreCase(setting)) {
			// keep the default setting
		} else {
			throw new BuildException("The build-attribute [fullscreen] needs to be either [yes], [no] or [menu]. "
					+ "The setting [" + setting + "] is not supported.");
		}
	}


	/**
	 * Retrieves the full screen setting.
	 * 
	 * @return the full screen setting
	 */
	public FullScreenSetting getFullScreenSetting() {
		return this.fullScreenSetting;
	}
	
	public DebugSetting getDebugSetting() {
		return this.debugSetting;
	}
	
	public Midlet[] getMidlets() {
		if (this.midletSetting == null) {
			return null;
		}
		return this.midletSetting.getMidlets();
	}

	/**
	 * Determines whether debugging is enabled.
	 * 
	 * @return true when debugging is enabled for this project.
	 */
	public boolean isDebugEnabled() {
		if (this.debugSetting == null) {
			return false;
		} else {
			return this.debugSetting.isEnabled();
		}
	}
	
	/**
	 * Retrieves the working directory.
	 * The default working directory is "./build".
	 * If the working directory does not exist, it will be created now.
	 * 
	 * @return Returns the working directory.
	 */
	public File getWorkDir() {
		if (!this.workDir.exists()) {
			this.workDir.mkdirs();
		}
		return this.workDir;
	}
	
	/**
	 * Sets the working directory. Defaults to "./build".
	 * 
	 * @param workDir The working directory to set.
	 */
	public void setWorkDir(File workDir) {
		this.workDir = workDir;
	}
	
	/**
	 * Retrieves the directory to which the ready-to-distribute jars should be copied to.
	 * Defaults to "./dist".
	 * If the distribution directory does not exist, it will be created now.
	 * 
	 * @return The destination directory.
	 */
	public File getDestDir() {
		if (! this.destDir.exists()) {
			this.destDir.mkdirs();
		}
		return this.destDir;
	}
	
	
	/**
	 * Sets the destination directory. Defaults to "./dist".
	 * 
	 * @param destDir The destination directory.
	 */
	public void setDestDir( File destDir ) {
		this.destDir = destDir;
	}
	
	/**
	 * Retrieves the directory which contains the resources. Defaults to "./resources".
	 * Resources include pictures, texts, etc. as well as the CSS-files 
	 * containing the design information. 
	 * If the resource directory does not exist, it will be created now.
	 * 
	 * @return The directory which contains the resources
	 */
	public File getResDir() {
		if (!this.resDir.exists()) {
			this.resDir.mkdirs();
		}
		return this.resDir;
	}
	
	/**
	 * Sets the directory containing the resources of this project.
	 * Default resource directory is "./resources".
	 * Resources include pictures, texts, etc. as well as the CSS-files 
	 * containing the design information. 
	 * 
	 * @param resDir The directory containing the resources.
	 */
	public void setResDir( File resDir ) {
		if (!resDir.exists()) {
			throw new BuildException("The resource directory [" + resDir.getAbsolutePath() + 
					"] does not exist. Please correct the attribute [resDir] " +
					"of the <build> element.");
		}
		this.resDir = resDir;
	}
	
	/**
	 * Sets the directory containing the J2ME source code of polish.
	 * 
	 * @param polishDir the directory containing the J2ME source code of polish.
	 */
	public void setPolishDir( File polishDir ) {
		if (!polishDir.exists()) {
			throw new BuildException("The polish directory [" + polishDir.getAbsolutePath() + 
					"] does not exist. " +
					"Please correct the [polishDir] attribute of the <build> element.");
		}
		this.polishDir = polishDir;
	}
	
	/**
	 * Retrieves the directory containing the J2ME source code of polish.
	 * 
	 * @return the directory containing the J2ME source code of polish
	 */
	public File getPolishDir() {
		return this.polishDir;
	}

	public void setSrcdir( String srcDir ) {
		setSourceDir( srcDir );
	}
	public void setSrcDir( String srcDir ) {
		setSourceDir( srcDir );
	}
	/**
	 * Sets the source directory in which the source files for the application reside.
	 * 
	 * @param srcDir the source directory
	 */
	public void setSourceDir( String srcDir ) {
		String[] paths = TextUtil.split( srcDir, ':');
		if (paths.length == 1) {
			paths = TextUtil.split( srcDir, ';' );
		}
		for (int i = 0; i < paths.length; i++) {
			String path = paths[i];
			File dir = new File( path );
			if (!dir.exists()) {
				throw new BuildException("The source directory [" + path + "] does not exist. " +
						"Please correct the attribute [sourceDir] of the <build> element.");
			}
			this.sourceDirs.add( dir );
		}
	}
	
	/**
	 * Retrieves all external source directories.
	 * 
	 * @return an arrray with at least one source directory.
	 */
	public File[] getSourceDirs() {
		if (this.sourceDirs.size() == 0) {
			// add default directory: either source/src, scr or source:
			File src = new File("source/src");
			if (src.exists()) {
				this.sourceDirs.add( src );
			} else {
				src = new File("src");
				if (src.exists()) {
					this.sourceDirs.add( src );
				} else {
					src = new File("source");
					if (src.exists()) {
						this.sourceDirs.add( src );
					} else {
						throw new BuildException("Did not find any of the default " +
								"source directories [source/src], [src] or [source]. " +
								"Please specify the [sourceDir]-attribute of the " +
								"<build> element.");
					}
				}
			}
		}
		File[] dirs = (File[]) this.sourceDirs.toArray( new File[ this.sourceDirs.size()]);
		return dirs;
	}
	

	/**
	 * @return Returns the the directory which contains device specific libraries.
	 */
	public File getApiDir() {
		if (!this.apiDir.exists()) {
			throw new BuildException("Did not find the api directory in the default path [" + this.apiDir.getAbsolutePath() + "]. Please specify either the [apiDir]-attribute of the <build> element or copy all device-specific jars to this path.");
		}
		return this.apiDir;
	}
	
	/**
	 * Sets the directory which contains device specific libraries
	 * 
	 * @param apiDir The directory which contains device specific libraries. Defaults to "./import"
	 */
	public void setApiDir(File apiDir) {
		if (!apiDir.exists()) {
			throw new BuildException("The [apiDir]-attribute of the <build> element points to a non existing directory: [" + apiDir.getAbsolutePath() + "].");
		}
		this.apiDir = apiDir;
	}
	
	/**
	 * @return Returns the xml file containing the devices-data.
	 */
	public InputStream getDevices() {
		try {
			return getResource( this.devices, "devices.xml" );
		} catch (FileNotFoundException e) {
			throw new BuildException("Unable to open devices.xml: " + e.getMessage(), e );
		}
	}
	
	/**
	 * @param devices The path to the devices.xml
	 */
	public void setDevices(File devices) {
		if (!devices.exists()) {
			throw new BuildException("The [devices]-attribute of the <build> element points to a non existing file: [" + devices.getAbsolutePath() + "].");
		}
		this.devices = devices;
	}
	
	/**
	 * @return Returns the groups.
	 */
	public InputStream getGroups() {
		try {
			return getResource( this.groups, "groups.xml" );
		} catch (FileNotFoundException e) {
			throw new BuildException("Unable to open devices.xml: " + e.getMessage(), e );
		}
	}
	/**
	 * @param groups The groups to set.
	 */
	public void setGroups(File groups) {
		if (!groups.exists()) {
			throw new BuildException("The [groups]-attribute of the <build> element points to a non existing file: [" + groups.getAbsolutePath() + "].");
		}
		this.groups = groups;
	}

	/**
	 * @return Returns the vendors.
	 */
	public InputStream getVendors() {
		try {
			return getResource( this.vendors, "vendors.xml" );
		} catch (FileNotFoundException e) {
			throw new BuildException("Unable to open devices.xml: " + e.getMessage(), e );
		}
	}
	
	/**
	 * @param vendors The vendors to set.
	 */
	public void setVendors(File vendors) {
		if (!vendors.exists()) {
			throw new BuildException("The [vendors]-attribute of the <build> element points to a non existing file: [" + vendors.getAbsolutePath() + "].");
		}
		this.vendors = vendors;
	}

	/**
	 * Gets the path to the MIDP/1.0-api-file
	 * 
	 * @return The path to the api-file of the MIDP/1.0 environment 
	 */
	public File getMidp1Path() {
		if (!this.midp1Path.exists()) {
			throw new BuildException("The default path to the MIDP/1.0-API [" + this.midp1Path.getAbsolutePath() + "] points to a non-existing file. Please specify it with the [midp1Path] attribute of the <build> element.");
		}
		return this.midp1Path;
	}
	
	/**
	 * Sets the path to the api-file of the MIDP/1.0 environment
	 *  
	 * @param midp1Path The path to the MIDP/1.0-api-file
	 */
	public void setMidp1Path( File midp1Path ) {
		if (!midp1Path.exists()) {
			throw new BuildException("Invalid path to the MIDP/1.0-API: [" + midp1Path.getAbsolutePath() + "] (File not found).");
		}
		this.midp1Path = midp1Path;
	}

	/**
	 * Gets the path to the MIDP/2.0-jar
	 * 
	 * @return The path to the api-file of the MIDP/2.0 environment 
	 */
	public File getMidp2Path() {
		if (!this.midp2Path.exists()) {
			throw new BuildException("The default path to the MIDP/2.0-API [" + this.midp2Path.getAbsolutePath() + "] points to a non-existing file. Please specify it with the [midp2Path] attribute of the <build> element.");
		}
		return this.midp2Path;
	}
	
	/**
	 * Sets the path to the api-file of the MIDP/2.0 environment.
	 * When the midp1Path is not defined, it will use the same
	 * api-path as the given MIDP/2.0 environment.
	 *  
	 * @param midp2Path The path to the MIDP/2.0-api-file
	 */
	public void setMidp2Path( File midp2Path ) {
		if (!midp2Path.exists()) {
			throw new BuildException("Invalid path to the MIDP/2.0-API: [" + midp2Path.getAbsolutePath() + "] (File not found).");
		}
		this.midp2Path = midp2Path;
		if (this.midp1Path == null) {
			this.midp1Path = midp2Path;
		}
	}

	/**
	 * @return The user-defined symbols
	 */
	public String getSymbols() {
		return this.symbols;
	}
	
	public void setPreverify( File preverify ) {
		if (!preverify.exists()) {
			throw new BuildException("The path to the preverify-tool is invalid: [" + preverify.getAbsolutePath() + "] points to a non-existing file. Please correct the [preverify] attribute of the <build> element.");
		}
		this.preverify = preverify;
	}
	
	public File getPreverify() {
		return this.preverify;
	}

	/**
	 * Retrieves all the defined MIDlet-class-names.
	 * 
	 * @return The names of all midlet-classes in a String array. 
	 * 		The first midlet is also the first element in the returned array.
	 */
	public String[] getMidletClassNames() {
		Midlet[] midlets = this.midletSetting.getMidlets();
		String[] midletClassNames = new String[ midlets.length ];
		for (int i = 0; i < midlets.length; i++) {
			midletClassNames[i] = midlets[i].getClassName();
		}
		return midletClassNames;
	}
	
	/**
	 * Retrieves the infos for all midlets.
	 * The infos contain the name, the icon and the class of the midlet
	 * and are used for the JAD and the manifest.
	 * 
	 * @param defaultIcon the url of the default icon.
	 * @return The infos of all midlets in a String array.
	 * 		The first midlet is also the first element in the returned array.
	 */
	public String[] getMidletInfos( String defaultIcon ) {
		Midlet[] midlets = this.midletSetting.getMidlets();
		String[] midletInfos = new String[ midlets.length ];
		for (int i = 0; i < midlets.length; i++) {
			midletInfos[i] = midlets[i].getMidletInfo( defaultIcon );
		}
		return midletInfos;
	}

	/**
	 * @return The obfuscator which should be used
	 */
	public ObfuscatorSetting getObfuscatorSetting() {
		return this.obfuscatorSetting;
	}
	
	/**
	 * Sets the name of the obfuscator.
	 * 
	 * @param obfuscator The name of the obfuscator, e.g. "ProGuard" or "RetroGuard"
	 */
	public void setObfuscator( String obfuscator ) {
		if (this.obfuscatorSetting == null) {
			this.obfuscatorSetting = new ObfuscatorSetting();
		}
		this.obfuscatorSetting.setName( obfuscator );
	}
	
	/**
	 * Determines whether the resulting jars should be obfuscated at all.
	 * 
	 * @return True when the jars should be obfuscated.
	 */
	public boolean doObfuscate() {
		if (this.obfuscatorSetting == null) {
			return false;
		}
		return this.obfuscatorSetting.isEnabled();
	}
	
	/**
	 * Determines whether the resulting jars should be obfuscated at all.
	 * 
	 * @param obfuscate True when the jars should be obfuscated.
	 */
	public void setObfuscate( boolean obfuscate ) {
		if (this.obfuscatorSetting == null) {
			this.obfuscatorSetting = new ObfuscatorSetting();
		}
		this.obfuscatorSetting.setEnable( obfuscate );
	}

	/**
	 * Retrieves the specified resource as an input stream.
	 * The caller is responsible for closing the returned input stream.
	 * 
	 * @param file the file which has been set. When the file is not null,
	 * 		  it needs to exists as well.
	 * @param name the name of the resource
	 * @return the input stream for the specified resource.
	 * @throws FileNotFoundException when the resource could not be found.
	 */
	private InputStream getResource(File file, String name) 
	throws FileNotFoundException 
	{
		if (file != null ) {
			try {
				return new FileInputStream( file );
			} catch (FileNotFoundException e) {
				throw new BuildException("Unable to open [" + file.getAbsolutePath() + "]: " + e.getMessage(), e );
			}
		}
		return this.resourceUtil.open( name );
	}

	
}
