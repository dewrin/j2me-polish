/*
 * Created on 22-Jan-2003 at 14:10:02.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.build;

import de.enough.polish.*;

import org.apache.tools.ant.BuildException;

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
	
	public final static int IMG_LOAD_BACKGROUND = 1;
	public final static int IMG_LOAD_FOREGROUND = 2;
	
	private DebugSetting debugSetting;
	private MidletSetting midletSetting; 
	private String version;
	private File targetDir;
	private String symbols;
	private int imageLoadStrategy;
	private FullScreenSetting fullScreenSetting;
	private File devices;
	private File vendors;
	private File groups;
	private Capability[] variables;
	private Source source;
	private boolean usePolishGui;
	
	/**
	 * Creates a new build setting.
	 */
	public BuildSetting() {
		this.targetDir = new File("./build");
		this.devices = new File("./devices.xml");
		this.vendors = new File("./vendors.xml");
		this.groups = new File("./groups.xml");
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
		this.debugSetting = setting;
	}
	
	public void addConfiguredVariables( Variables vars ) {
		this.variables = vars.getVariables();
	}
	
	public Capability[] getVariables() {
		return this.variables;
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
	
	public void setVersion( String version) {
		this.version = version;
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
	 * @return Returns the devices.
	 */
	public File getDevices() {
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
	 * @return Returns the targetDir.
	 */
	public File getTargetDir() {
		return this.targetDir;
	}
	
	/**
	 * @param targetDir The targetDir to set.
	 */
	public void setTargetDir(File targetDir) {
		this.targetDir = targetDir;
	}

	/**
	 * @return Returns the groups.
	 */
	public File getGroups() {
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

}
