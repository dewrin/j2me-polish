/*
 * Created on 04-Feb-2004 at 20:04:50.
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
package de.enough.polish;

import de.enough.polish.exceptions.InvalidComponentException;

import org.jdom.JDOMException;

import java.io.*;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import junit.framework.TestCase;

/**
 * <p>Tests the DeviceManager class</p>
 *
 * <p>copyright Enough Software 2004</p>
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
		VendorManager vendorManager = new VendorManager( null, new FileInputStream( new File("vendors.xml") ));
		DeviceGroupManager groupManager = new DeviceGroupManager( new FileInputStream( new File("groups.xml")));
		LibraryManager apiManager = new LibraryManager( new Hashtable(),"import", "/home/enough/dev/WTK21/", null,  new FileInputStream( new File("apis.xml")) );
		DeviceManager manager = new DeviceManager( vendorManager, groupManager, apiManager, new FileInputStream( new File("devices.xml")));
		System.out.println("initialisation done.");
		Device[] devices = manager.getDevices();
		for (int i = 0; i < devices.length; i++) {
			Device device = devices[i];
			System.out.println(device.getIdentifier());
		}
	}

}
