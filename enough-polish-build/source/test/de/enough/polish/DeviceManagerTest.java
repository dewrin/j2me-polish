/*
 * Created on 04-Feb-2004 at 20:04:50.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish;

import de.enough.polish.exceptions.InvalidComponentException;

import org.jdom.JDOMException;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

/**
 * <p>Tests the DeviceManager class</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        04-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class DeviceManagerTest extends TestCase {

	public DeviceManagerTest(String name) {
		super(name);
	}
	
	public void testInitialisation() throws JDOMException, IOException, InvalidComponentException 
	{	
		VendorManager vendorManager = new VendorManager( null, new File("./definitions/vendors.xml"));
		DeviceGroupManager groupManager = new DeviceGroupManager( new File("./definitions/groups.xml"));
		DeviceManager manager = new DeviceManager( vendorManager, groupManager, new File("./definitions/devices.xml"));
		System.out.println("initialisation done.");
		Device[] devices = manager.getDevices();
		for (int i = 0; i < devices.length; i++) {
			Device device = devices[i];
			System.out.println(device.getIdentifier());
		}
	}

}
