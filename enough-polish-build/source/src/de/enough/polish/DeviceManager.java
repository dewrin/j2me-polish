/*
 * Created on 28-Jan-2004 at 23:28:45.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish;

import de.enough.polish.exceptions.InvalidDeviceException;

import org.jdom.*;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * <p>Manages all known J2ME devices.</p>
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
	 * 
	 * @throws JDOMException when there are syntax errors in devices.xml
	 * @throws IOException when devices.xml could not be read
	 * @throws InvalidDeviceException when a device definition has errors
	 */
	public DeviceManager( Project project ) throws JDOMException, IOException, InvalidDeviceException {
		this( project, new File("./devices.xml") );
	}

	/**
	 * Creates a new device manager with the given devices.xml file.
	 * 
	 * @param project the polish project settings.
	 * @param devicesFile the file containing the device definitions.
	 * 			Usally this is the devices.xml file in the current directory.
	 * 
	 * @throws JDOMException when there are syntax errors in devices.xml
	 * @throws IOException when devices.xml could not be read
	 * @throws InvalidDeviceException when a device definition has errors
	 */
	public DeviceManager( Project project, File devicesFile ) throws JDOMException, IOException, InvalidDeviceException {
		this.project = project;
		this.devicesFile = devicesFile;
		loadDevices();
	}
	
	/**
	 * Loads the device definitions.
	 * 
	 * @throws JDOMException when there are syntax errors in devices.xml
	 * @throws IOException when devices.xml could not be read
	 * @throws InvalidDeviceException when a device definition has errors
	 */
	private void loadDevices() throws JDOMException, IOException, InvalidDeviceException {
		SAXBuilder builder = new SAXBuilder( false );
		Document document = builder.build( this.devicesFile );
		ArrayList devicesList = new ArrayList();
		List xmlList = document.getRootElement().getChildren();
		for (Iterator iter = xmlList.iterator(); iter.hasNext();) {
			Element deviceElement = (Element) iter.next();
			Device device = new Device( deviceElement );
			devicesList.add( device );
		}
		this.devices = (Device[]) devicesList.toArray( new Device[ devicesList.size()]);
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
