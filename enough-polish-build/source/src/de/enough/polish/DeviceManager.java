/*
 * Created on 28-Jan-2004 at 23:28:45.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish;

import java.io.File;

/**
 * <p>Manages all knwown devices.</p>
 * <p>The devices are defined in the devices.xml file</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        28-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class DeviceManager {

	private Device[] devices;
	private Project project;
	private File devicesFile;

	/**
	 * Creates a new device manager which looks at the current directory for the devices.xml file.
	 * 
	 * @param project the polish project settings.
	 */
	public DeviceManager( Project project ) {
		this( project, new File("./devices.xml") );
	}

	/**
	 * Creates a new device manager with the given devices.xml file.
	 * 
	 * @param project the polish project settings.
	 * @param devicesFile the file containing the device definitions.
	 * 			Usally this is the devices.xml file in the current directory.
	 */
	public DeviceManager( Project project, File devicesFile ) {
		this.project = project;
		this.devicesFile = devicesFile;
		loadDevices();
	}
	
	/**
	 * Loads the device definitions.
	 */
	private void loadDevices() {
		// TODO enough implement loadDevices
		
	}

	/**
	 * Retrieves all found device definitions.
	 * 
	 * @return the device definitions found in the devices.xml file.
	 */
	public Device[] getDevices() {
		return this.devices;
	}


}
