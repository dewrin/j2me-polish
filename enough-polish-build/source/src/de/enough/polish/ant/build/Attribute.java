/*
 * Created on Jun 18, 2004
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
package de.enough.polish.ant.build;

import de.enough.polish.Variable;

/**
 * An attribute is a variable, which can also contain a condition.
 * 
 * @author robert virkus, j2mepolish@enough.de
 */
public class Attribute extends Variable {

	private String ifValue;

	/**
	 * Creates a new empty attribute
	 */
	public Attribute() {
		super();
	}
	
	/**
	 * Creates a new attribute 
	 * 
	 * @param name the name of the attribute
	 * @param value the value of the attribute
	 */
	public Attribute(String name, String value) {
		super(name, value);
	}
	
	public void setIf( String ifValue ) {
		this.ifValue = ifValue;
	}
	
	public String getIf() {
		return this.ifValue;
	}
}
