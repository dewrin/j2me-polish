/*
 * Created on 15-Jan-2004 at 16:08:33.
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

import org.jdom.Element;


/**
 * <p>Respresents a group to which a device belongs to.</p>
 * <p>A group can be independent of a specific manufacturer, 
 * and a device can belong to several groups.
 * Also a group can belong do other groups.
 * </p>
 * 
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        15-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class DeviceGroup extends PolishComponent {


	private String parentIdentifier;

	/**
	 * Creates a new device group.
	 * 
	 * @param definition The xml definition of this group.
	 * @throws InvalidComponentException when the given definition contains errors.
	 */
	public DeviceGroup( Element definition ) 
	throws InvalidComponentException 
	{
		this.identifier = definition.getChildTextTrim( "name" );
		//System.out.println("\ninitialising group " + this.identifier );
		if (this.identifier == null) {
			throw new InvalidComponentException("A group needs to have the element <name> defined. Please check your [groups.xml] file.");
		}
		this.parentIdentifier  = definition.getChildTextTrim( "parent" );
		loadCapabilities(definition, this.identifier, "groups.xml");
	}
	
	/**
	 * Creates a new empty group.
	 * 
	 * @param name the name of the group
	 */
	public DeviceGroup(String name) {
		this.identifier = name;
	}

	/**
	 * @return The name of the parent of this group.
	 */
	public String getParentIdentifier() {
		return this.parentIdentifier;
	}

}
