/*
 * Created on 23-Feb-2004 at 14:30:33.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.build;

/**
 * <p>Represents a class which should be obfuscated.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        23-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class Keep {
	
	private String className;
	
	/**
	 * Creates a new empty keep.
	 */
	public Keep() {
		// initialisation is done via the setter method
	}
	
	public void setClass( String className ) {
		this.className = className;
	}
	
	public String getClassName() {
		return this.className;
	}
}
