/*
 * Created on 15-Jan-2004 at 16:04:52.
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
 * <p>Represents a manufacturer of J2ME devices like Nokia, Sony-Ericsson, Motorola and so on.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        15-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class Vendor extends PolishComponent {

	
	/**
	 * Creates a new Vendor.
	 * 
	 * @param parent the project to which this vendor manufacturer belongs to.
	 * @param definition the XML definition of this vendor.
	 * @throws InvalidComponentException when the given definition contains errors
	 */
	public Vendor( PolishProject parent, Element definition )
	throws InvalidComponentException
	{
		super( parent );
		this.identifier = definition.getChildTextTrim( "name");
		//System.out.println("\ninitialising vendor " + this.identifier);
		if (this.identifier == null) {
			throw new InvalidComponentException("Every vendor needs to define the element <name> - please check your vendors.xml.");
		}
		// load all capabilities:
		loadCapabilities(definition, this.identifier, "vendors.xml");
	}


}
