/*
 * Created on 24-Jan-2004 at 14:56:31.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.requirements;

/**
 * <p>A class used for comparing a requirement-value with a device-property.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        24-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public interface Matcher {

	/**
	 * Checks the given device-value with the requirement-value.
	 * 
	 * @param deviceValue the value of the device.
	 * @return true when the value of the device matches the value of this matcher.
	 */
	public boolean matches( String deviceValue );

}
