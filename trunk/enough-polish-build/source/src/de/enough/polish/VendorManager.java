/*
 * Created on 16-Feb-2004 at 19:09:20.
 *
 * Copyright (c) 2004 Robert Virkus / enough software
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
 * www.enough.de/j2mepolish for details.
 */
package de.enough.polish;

import de.enough.polish.exceptions.InvalidComponentException;

import org.apache.tools.ant.BuildException;
import org.jdom.*;
import org.jdom.input.SAXBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * <p>Manages all known vendors.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        16-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class VendorManager {
	
	private HashMap vendors;
	
	/**
	 * Creates a new vendor manager.
	 * 
	 * @param project The j2me project settings.
	 * @param vendorsIS The input stream containing the vendor-definitions. This is usually "./vendors.xml".
	 * @throws JDOMException when there are syntax errors in devices.xml
	 * @throws IOException when devices.xml could not be read
	 * @throws InvalidComponentException when a vendor definition has errors
	 */
	public VendorManager( PolishProject project, InputStream vendorsIS ) 
	throws JDOMException, IOException, InvalidComponentException 
	{
		this.vendors = new HashMap();
		loadVendors( project, vendorsIS );
		vendorsIS.close();
	}
	
	/**
	 * Loads all known vendors from the given file.
	 * 
	 * @param project The j2me project settings.
	 * @param vendorsIS The input stream containing the vendor-definitions. This is usually "./vendors.xml".
	 * @throws JDOMException when there are syntax errors in devices.xml
	 * @throws IOException when devices.xml could not be read
	 * @throws InvalidComponentException when a vendor definition has errors
	 */
	private void loadVendors(PolishProject project, InputStream vendorsIS) 
	throws JDOMException, IOException, InvalidComponentException 
	{
		if (vendorsIS == null) {
			throw new BuildException("Unable to load vendors.xml, no file found.");
		}
		SAXBuilder builder = new SAXBuilder( false );
		Document document = builder.build( vendorsIS );
		List xmlList = document.getRootElement().getChildren();
		for (Iterator iter = xmlList.iterator(); iter.hasNext();) {
			Element deviceElement = (Element) iter.next();
			Vendor vendor = new Vendor( project, deviceElement );
			this.vendors.put( vendor.getIdentifier(), vendor );
		}
	}

	/**
	 * Retrieves the specified vendor.
	 * 
	 * @param name The name of the vendor, e.g. Nokia, Siemens, Motorola, etc.
	 * @return The vendor or null of that vendor has not been defined.
	 */
	public Vendor getVendor( String name ) {
		return (Vendor) this.vendors.get( name );
	}
}
