/*
 * Created on 21-Jan-2003 at 15:15:56.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant;

import de.enough.polish.*;
import de.enough.polish.ant.build.BuildSetting;
import de.enough.polish.ant.info.InfoSetting;
import de.enough.polish.ant.requirements.Requirements;
import de.enough.polish.exceptions.InvalidDeviceException;
import de.enough.polish.preprocess.Preprocessor;

import org.apache.tools.ant.BuildException;
import org.jdom.JDOMException;

import java.io.IOException;

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
		selectDevices();
		preprocess();
		compile();
		obfuscate();
		preverify();
		jar();
		jad();
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
			log("Nested element [deviceRequirements] is missing, now the project will be optimized for all devices.");
		}
	}
	
	/**
	 * Initialises this project and instanciates several helper classes.
	 */
	private void initProject() {
		//TODO enough implement initProject
		// create debug manager:
		boolean isDebugEnabled = this.buildSetting.isDebugEnabled(); 
		DebugManager debugManager = null;
		if (isDebugEnabled) {
			// init debug manager
		}
		// create project settings:
		this.polishProject = new Project( this.buildSetting.usesPolishGui(), isDebugEnabled, debugManager );
		try {
			// create new device manager:
			this.deviceManager = new DeviceManager( this.polishProject );
		} catch (JDOMException e) {
			throw new BuildException("unable to create device manager: " + e.getMessage(), e );
		} catch (IOException e) {
			throw new BuildException("unable to create device manager: " + e.getMessage(), e );
		} catch (InvalidDeviceException e) {
			throw new BuildException("unable to create device manager: " + e.getMessage(), e );
		}
		this.preprocessor = new Preprocessor( this.polishProject, null, null, null, false, false, true, null );
	}

	/**
	 * Selects the actual devices for which optimal applications should be generated.
	 */
	private void selectDevices() {
		if (this.deviceRequirements == null) {
			this.devices = this.deviceManager.getDevices();
		} else {
			this.devices = this.deviceRequirements.filterDevices( this.deviceManager.getDevices() );
		}
	}

	/**
	 * Preprocesses the source code for all devices.
	 */
	private void preprocess() {
		// TODO enough implement preprocess
		for ( int i=0; i<this.devices.length; i++) {
			Device device = this.devices[i];
			device.getCapability("Identifier");
		}
	}

	/**
	 * 
	 */
	private void compile() {
		// TODO enough implement compile
		
	}

	/**
	 * 
	 */
	private void obfuscate() {
		// TODO enough implement obfuscate
		
	}

	/**
	 * 
	 */
	private void preverify() {
		// TODO enough implement preverify
		
	}
	
	/**
	 * 
	 */
	private void jar() {
		// TODO enough implement jar
		
	}

	private void jad() {
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
