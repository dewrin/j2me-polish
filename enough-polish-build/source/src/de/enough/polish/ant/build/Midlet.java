/*
 * Created on 22-Jan-2003 at 14:33:39.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.build;

/**
 * <p>Represents a midlet.</p>
 *
 * <p>copyright enough software 2004</p>
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
		this.icon = "";
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
	 * @return The info containing the name, icon and class of this midlet.
	 */
	public String getMidletInfo() {
		if (this.name == null) {
			String altName = this.className;
			int dotPos = altName.lastIndexOf('.');
			if (dotPos != -1) {
				altName = altName.substring( dotPos + 1);
			}
			this.name = altName; 
		}
		return this.name + "," + this.icon + "," + this.className;
	}

}
