/*
 * Created on 22-Jan-2003 at 14:33:39.
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
package de.enough.polish.ant.build;

/**
 * <p>Represents a midlet.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        22-Jan-2003 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class Midlet {
	
	public int number;
	public String className;
	public String icon;
	public String name;

	/**
	 * Creates a new midlet definition.
	 */
	public Midlet() {
		// initialisation is done with the setter and getter methods
	}
	
	

	/**
	 * The number of this midlet. 
	 *  
	 * @return the number of this midlet.
	 */
	public int getNumber() {
		return this.number;
	}

	/**
	 * @param number the number of this midlet
	 */
	public void setNumber(int number) {
		this.number = number;
	}

	/**
	 * Returns the class-name.
	 * 
	 * @return the name of the class of this midlet.
	 */
	public String getClassName() {
		return this.className;
	}

	/**
	 * Sets the class name.
	 * 
	 * @param className the name of the class of this midlet.
	 */
	public void setClass(String className) {
		this.className = className;
	}

	/**
	 * @return Returns the icon.
	 */
	public String getIcon() {
		return this.icon;
	}

	/**
	 * @param icon The icon to set.
	 */
	public void setIcon(String icon) {
		if (!icon.startsWith("/")) {
			icon = "/" + icon;
		}
		this.icon = icon;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Retrieves the info for the manifest and JAD file of this midlet.
	 * 
	 * @param defaultIcon the name of the default icon.
	 * @return The info containing the name, icon and class of this midlet.
	 */
	public String getMidletInfo( String defaultIcon ) {
		if (this.name == null) {
			String altName = this.className;
			int dotPos = altName.lastIndexOf('.');
			if (dotPos != -1) {
				altName = altName.substring( dotPos + 1);
			}
			this.name = altName; 
		}
		if (this.icon == null) {
			if (defaultIcon != null) {
				this.icon = defaultIcon;
			} else {
				this.icon = "";
			}
		}
		return this.name + "," + this.icon + "," + this.className;
	}

}
