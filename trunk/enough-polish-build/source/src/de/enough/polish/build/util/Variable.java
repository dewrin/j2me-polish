/*
 * Created on 03-Oct-2003 at 17:14:23
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.build.util;

/**
 * <p>Variable provides the definition of a name-value pair.</p>
 * <p></p>
 * <p>copyright enough software 2003, 2004</p>
 * <pre>
 *    history
 *       03-Oct-2003 (rob) creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class Variable {
	
	private String name;
	private String value;

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

}
