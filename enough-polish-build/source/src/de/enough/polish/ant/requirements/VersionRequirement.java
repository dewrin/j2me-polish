/*
 * Created on 10-Feb-2004 at 22:49:38.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.requirements;

import de.enough.polish.Device;

/**
 * <p></p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        10-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class VersionRequirement extends Requirement {

	/**
	 * @param value
	 * @param propertyName
	 */
	public VersionRequirement(String value, String propertyName) {
		super(value, propertyName);
		// TODO enough implement VersionRequirement
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ant.requirements.Requirement#isMet(de.enough.polish.Device, java.lang.String)
	 */
	protected boolean isMet(Device device, String property) {
		// TODO enough implement isMet
		return false;
	}

}
