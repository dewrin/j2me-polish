/*
 * Created on 16-Feb-2004 at 19:48:06.
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

import org.apache.tools.ant.BuildException;
import org.jdom.*;
import org.jdom.input.SAXBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * <p>Manages all known groups of devices.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        16-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class DeviceGroupManager {
	
	private HashMap groups;
	
	/**
	 * Creates a new group manager.
	 * 
	 * @param groupsIS the InputStream containing the groups definitions.
	 * 			Usally this is the groups.xml file in the current directory.
	 * @throws JDOMException when there are syntax errors in groups.xml
	 * @throws IOException when groups.xml could not be read
	 * @throws InvalidComponentException when a group definition has errors
	 */
	public DeviceGroupManager( InputStream groupsIS ) 
	throws InvalidComponentException, JDOMException, IOException 
	{
		this.groups = new HashMap();
		loadGroups( groupsIS );
		groupsIS.close();
	}
	
	/**
	 * Loads all group definitions.
	 * 
	 * @param groupsIS the InputStream containing the groups definitions.
	 * 			Usally this is the groups.xml file in the current directory.
	 * @throws JDOMException when there are syntax errors in groups.xml
	 * @throws IOException when groups.xml could not be read
	 * @throws InvalidComponentException when a group definition has errors
	 */
	private void loadGroups(InputStream groupsIS) 
	throws InvalidComponentException, JDOMException, IOException 
	{
		if (groupsIS == null) {
			throw new BuildException("Unable to load groups.xml, no file found.");
		}
		SAXBuilder builder = new SAXBuilder( false );
		Document document = builder.build( groupsIS );
		List xmlList = document.getRootElement().getChildren();
		for (Iterator iter = xmlList.iterator(); iter.hasNext();) {
			Element deviceElement = (Element) iter.next();
			DeviceGroup group = new DeviceGroup( deviceElement );
			this.groups.put( group.getIdentifier(), group );
		}
		DeviceGroup[] groupArray = (DeviceGroup[]) this.groups.values().toArray( new DeviceGroup[ this.groups.size() ] );
		for (int i = 0; i < groupArray.length; i++) {
			DeviceGroup group = groupArray[i];
			String parentName = group.getParentIdentifier();
			if (parentName != null) {
				//System.out.println("\nsetting " + parentName + " as parent for group " + group.getIdentifier());
				DeviceGroup parent = getGroup( parentName );
				if (parent == null) {
					throw new InvalidComponentException("The group [" + group.getIdentifier() + "] has the non-existing parent [" + parentName + "]. Check your [groups.xml]");
				}
				group.addComponent( parent );
			}
		}
	}

	/**
	 * Retrieves the group for the given name.
	 * 
	 * @param name The name of the group, e.g. "Series60"
	 * @return The found group or null when it has not been defined.
	 */
	public DeviceGroup getGroup( String name ) {
		return (DeviceGroup) this.groups.get( name );
	}

	/**
	 * Gets or creates the group with the specified name.
	 * 
	 * @param name the name of the group
	 * @param create when true is given, the group will be created when it does not exist
	 * @return the group with the specified name.
	 */
	public Object getGroup(String name, boolean create) {
		DeviceGroup group = (DeviceGroup) this.groups.get( name );
		if (group == null && create) {
			group = new DeviceGroup( name );
			this.groups.put( name, group );
		}
		return group;
	}
}
