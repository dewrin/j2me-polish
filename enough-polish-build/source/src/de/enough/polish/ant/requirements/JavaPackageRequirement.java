/*
 * Created on 24-Jan-2004 at 18:05:57.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.requirements;


/**
 * <p>Selects a device by its support of specific software-APIs.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        24-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class JavaPackageRequirement extends StringRequirement {


	/**
	 * Creates a new java package requirement.
	 * 
	 * @param value the needed apis seperated by comma.
	 */
	public JavaPackageRequirement(String value) {
		super(value, "JavaPackage");
	}

}
