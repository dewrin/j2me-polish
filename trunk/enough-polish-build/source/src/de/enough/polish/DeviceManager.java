/*
 * Created on 28-Jan-2004 at 23:28:45.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish;

import de.enough.polish.exceptions.InvalidComponentException;
import de.enough.polish.util.TextUtil;

import org.apache.tools.ant.BuildException;
import org.jdom.*;
import org.jdom.input.SAXBuilder;

import java.io.*;
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
	 * Creates a new device manager with the given devices.xml file.
	 * 
	 * @param vendorManager The manager of the device-manufacturers
	 * @param groupManager The manager for device-groups.
	 * @param devicesIS the InputStream containing the device definitions.
	 * 			Usally this is the devices.xml file in the current directory.
	 * @throws JDOMException when there are syntax errors in devices.xml
	 * @throws IOException when devices.xml could not be read
	 * @throws InvalidComponentException when a device definition has errors
	 */
	public DeviceManager( VendorManager vendorManager, DeviceGroupManager groupManager, InputStream devicesIS ) 
	throws JDOMException, IOException, InvalidComponentException 
	{
		loadDevices( vendorManager, groupManager, devicesIS );
		devicesIS.close();
	}
	
	/**
	 * Loads the device definitions.
	 * 
	 * @param vendorManager The manager of the device-manufacturers
	 * @param groupManager The manager for device-groups.
	 * @param devicesIS the InputStream containing the device definitions.
	 * 			Usally this is the devices.xml file in the current directory.
	 * @throws JDOMException when there are syntax errors in devices.xml
	 * @throws IOException when devices.xml could not be read
	 * @throws InvalidComponentException when a device definition has errors
	 */
	private void loadDevices( VendorManager vendorManager, DeviceGroupManager groupManager, InputStream devicesIS ) 
	throws JDOMException, IOException, InvalidComponentException 
	{
		if (devicesIS == null) {
			throw new BuildException("Unable to load devices.xml, no file found.");
		}
		SAXBuilder builder = new SAXBuilder( false );
		Document document = builder.build( devicesIS );
		ArrayList devicesList = new ArrayList();
		List xmlList = document.getRootElement().getChildren();
		for (Iterator iter = xmlList.iterator(); iter.hasNext();) {
			Element definition = (Element) iter.next();
			String identifierStr = definition.getChildTextTrim( "identifier");
			if (identifierStr == null) {
				throw new InvalidComponentException("Unable to initialise device. Every device needs to define either its [identifier] or its [name] and [vendor]. Check your [devices.xml].");
			}
			// one xml definition can contain several device-definitions,
			// e.g. <identifier>Nokia/3650, Nokia/5550</identifier>
			String[] identifiers = TextUtil.splitAndTrim(identifierStr,',');
			for (int i = 0; i < identifiers.length; i++) {
				String identifier = identifiers[i];
				String[] chunks = TextUtil.split( identifier, '/');
				if (chunks.length != 2) {
					throw new InvalidComponentException("The device [" + identifier + "] has an invalid [identifier] - every identifier needs to consists of the vendor and the name, e.g. \"Nokia/6600\". Please check you [devices.xml].");
				}
				String vendorName = chunks[0];
				String deviceName = chunks[1];
				Vendor vendor = vendorManager.getVendor( vendorName );
				Device device = new Device( definition, identifier, deviceName, vendor, groupManager );
				devicesList.add( device );
			}
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
