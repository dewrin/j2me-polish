/*
 * Created on 28-Jan-2004 at 23:28:45.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish;

import de.enough.polish.exceptions.InvalidComponentException;
import de.enough.polish.util.TextUtil;

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

	/**
	 * Creates a new device manager which looks at the current directory for the devices.xml file.
	 * 
	 * @param vendorManager The manager of the device-manufacturers
	 * @param groupManager The manager for device-groups.
	 * @throws JDOMException when there are syntax errors in devices.xml
	 * @throws IOException when devices.xml could not be read
	 * @throws InvalidComponentException when a device definition has errors
	 */
	public DeviceManager( VendorManager vendorManager, DeviceGroupManager groupManager ) throws JDOMException, IOException, InvalidComponentException {
		this( vendorManager, groupManager, new File("./devices.xml") );
	}

	/**
	 * Creates a new device manager with the given devices.xml file.
	 * 
	 * @param vendorManager The manager of the device-manufacturers
	 * @param groupManager The manager for device-groups.
	 * @param devicesFile the file containing the device definitions.
	 * 			Usally this is the devices.xml file in the current directory.
	 * @throws JDOMException when there are syntax errors in devices.xml
	 * @throws IOException when devices.xml could not be read
	 * @throws InvalidComponentException when a device definition has errors
	 */
	public DeviceManager( VendorManager vendorManager, DeviceGroupManager groupManager, File devicesFile ) 
	throws JDOMException, IOException, InvalidComponentException 
	{
		loadDevices( vendorManager, groupManager, devicesFile );
	}
	
	/**
	 * Loads the device definitions.
	 * 
	 * @param vendorManager The manager of the device-manufacturers
	 * @param groupManager The manager for device-groups.
	 * @param devicesFile the file containing the device definitions.
	 * 			Usally this is the devices.xml file in the current directory.
	 * @throws JDOMException when there are syntax errors in devices.xml
	 * @throws IOException when devices.xml could not be read
	 * @throws InvalidComponentException when a device definition has errors
	 */
	private void loadDevices( VendorManager vendorManager, DeviceGroupManager groupManager, File devicesFile ) 
	throws JDOMException, IOException, InvalidComponentException 
	{
		SAXBuilder builder = new SAXBuilder( false );
		Document document = builder.build( devicesFile );
		ArrayList devicesList = new ArrayList();
		List xmlList = document.getRootElement().getChildren();
		for (Iterator iter = xmlList.iterator(); iter.hasNext();) {
			Element definition = (Element) iter.next();
			String identifier = definition.getChildTextTrim( "identifier");
			if (identifier == null) {
				throw new InvalidComponentException("Unable to initialise device. Every device needs to define either its [identifier] or its [name] and [vendor]. Check your [devices.xml].");
			}
			//System.out.println("\ninitialising device " + this.identifier);
			String[] chunks = TextUtil.split( identifier, '/');
			if (chunks.length != 2) {
				//TODO there could be several device definitions in one xml-block
				throw new InvalidComponentException("The device [" + identifier + "] has an invalid [identifier] - every identifier needs to consists of the vendor and the name, e.g. \"Nokia/6600\". Please check you [devices.xml].");
			}
			String vendorName = chunks[0];
			String deviceName = chunks[1];
			Vendor vendor = vendorManager.getVendor( vendorName );
			Device device = new Device( definition, identifier, deviceName, vendor, groupManager );
			devicesList.add( device );
		}
		this.devices = (Device[]) devicesList.toArray( new Device[ devicesList.size()]);
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
