/*
 * Created on 03-Oct-2003 at 17:14:23
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

/**
 * <p>Variable provides the definition of a name-value pair.</p>
 * <p></p>
 * <p>copyright Enough Software 2003, 2004</p>
 * <pre>
 *    history
 *       03-Oct-2003 (rob) creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class Variable {
	
	private String name;
	private String value;
	private String type;

	/**
	 * Creates new uninitialised Variable
	 */
	public Variable() {
		// no values are set here
	}

	/**
	 * Creates a new Varable
	 * @param name (String) the name of this variable
	 * @param value (String) the value of this variable
	 */
	public Variable(String name, String value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * @return the name of this variables
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return the value of this variable
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * @param name the name of this variable
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param value the value of this variable
	 */
	public void setValue(String value ) {
		this.value = value;
	}

	/**
	 * @return Returns the type of this variable.
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * @param type The type to set.
	 */
	public void setType(String type) {
		this.type = type;
	}

}
