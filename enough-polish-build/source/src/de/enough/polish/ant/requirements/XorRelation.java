/*
 * Created on 15-Feb-2004 at 20:08:16.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.requirements;

import de.enough.polish.Device;

/**
 * <p>Contains a list of requirements of which only one must be met by the device.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        15-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class XorRelation extends RequirementContainer {
	
	/**
	 * Creates a new xor relation.
	 */
	public XorRelation() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ant.requirements.DeviceFilter#isMet(de.enough.polish.Device)
	 */
	public boolean isMet(Device device) {
		int numberOfMetRequirements = 0;
		DeviceFilter[] filters = getFilters();
		for (int i = 0; i < filters.length; i++) {
			DeviceFilter filter = filters[i];
			if (filter.isMet(device)) {
				numberOfMetRequirements++;
				if (numberOfMetRequirements > 1) {
					return false;
				}
			}
		}
		return (numberOfMetRequirements == 1);
	}
}
