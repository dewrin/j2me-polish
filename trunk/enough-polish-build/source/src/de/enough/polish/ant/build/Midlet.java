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

	/**
	 * Creates a new midlet definition.
	 */
	public Midlet() {
		// initialisation is done via the setter methods
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

}
