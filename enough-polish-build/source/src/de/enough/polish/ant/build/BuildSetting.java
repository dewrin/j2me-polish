/*
 * Created on 22-Jan-2003 at 14:10:02.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.build;

import de.enough.polish.*;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import java.io.File;

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
	private String symbols;
	private String imageLoadStrategy;
	private FullScreenSetting fullScreenSetting;
	private File devices;
	private File vendors;
	private File groups;
	private Variable[] variables;
	private Source source;
	private boolean usePolishGui;
	private String midp1Path;
	private String midp2Path;
	private String preverify;
	private Project project;
	private boolean includeAntProperties;
	
	/**
	 * Creates a new build setting.
	 * 
	 * @param project The corresponding ant-project.
	 */
	public BuildSetting( Project project ) {
		this.project = project;
		this.workDir = new File("./build");
		this.destDir = new File("./dist");
		this.apiDir = new File("./import");
		this.resDir = new File ("./resources");
		this.devices = new File("./devices.xml");
		this.vendors = new File("./vendors.xml");
		this.groups = new File("./groups.xml");
		this.midp1Path = "./import/midp1.jar";
		this.midp2Path = "./import/midp2.jar";
		this.imageLoadStrategy = IMG_LOAD_FOREGROUND;
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

	public void addConfiguredSource( Source src ) {
		if (src.getUrl() == null) {
			throw new BuildException("The nested element <source> needs to define the attribute [url].");
		}
		this.source = src;
	}
	
	public Source getSource() {
		return this.source;
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
	 * @return Returns the xml file containing the devices-data.
	 */
	public File getDevices() {
		if (!this.devices.exists()) {
			throw new BuildException("Did not find [devices.xml] in the default path [" + this.devices.getAbsolutePath() + "]. Please specify the [devices]-attribute of the <build> element or copy [devices.xml] to this path.");
		}
		return this.devices;
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
		this.resDir = resDir;
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
	 * @return Returns the groups.
	 */
	public File getGroups() {
		if (!this.groups.exists()) {
			throw new BuildException("Did not find [groups.xml] in the default path [" + this.groups.getAbsolutePath() + "]. Please specify the [groups]-attribute of the <build> element or copy [groups.xml] to this path.");
		}
		return this.groups;
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
	public File getVendors() {
		if (!this.vendors.exists()) {
			throw new BuildException("Did not find [vendors.xml] in the default path [" + this.vendors.getAbsolutePath() + "]. Please specify the [vendors]-attribute of the <build> element or copy [vendors.xml] to this path.");
		}
		return this.vendors;
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
	public String getMidp1Path() {
		File file = new File( this.midp1Path );
		if (!file.exists()) {
			throw new BuildException("The default path to the MIDP/1.0-API [" + this.midp1Path + "] points to a non-existing file. Please specify it with the [midp1Path] attribute of the <build> element.");
		}
		return this.midp1Path;
	}
	
	/**
	 * Sets the path to the api-file of the MIDP/1.0 environment
	 *  
	 * @param midp1Path The path to the MIDP/1.0-api-file
	 */
	public void setMidp1Path( String midp1Path ) {
		File file = new File( midp1Path );
		if (!file.exists()) {
			throw new BuildException("Invalid path to the MIDP/1.0-API: [" + midp1Path + "] (File not found).");
		}
		this.midp1Path = midp1Path;
	}

	/**
	 * Gets the path to the MIDP/2.0-jar
	 * 
	 * @return The path to the api-file of the MIDP/2.0 environment 
	 */
	public String getMidp2Path() {
		File file = new File( this.midp2Path );
		if (!file.exists()) {
			throw new BuildException("The default path to the MIDP/2.0-API [" + this.midp2Path + "] points to a non-existing file. Please specify it with the [midp2Path] attribute of the <build> element.");
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
	public void setMidp2Path( String midp2Path ) {
		File file = new File( midp2Path );
		if (!file.exists()) {
			throw new BuildException("Invalid path to the MIDP/2.0-API: [" + midp2Path + "] (File not found).");
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
	
	public void setPreverify( String preverify ) {
		this.preverify = preverify;
	}
	
	public String getPreverify() {
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

}
