/*
 * Created on 15-Feb-2004 at 18:55:33.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.requirements;

import de.enough.polish.Device;

/**
 * <p>A filter for requirements and requirement-groups which all need to be met.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        15-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class AndRelation extends RequirementContainer {
	
	/**
	 * Creates a new empty and-relation.
	 */
	public AndRelation() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ant.requirements.DeviceFilter#isMet(de.enough.polish.Device)
	 */
	public boolean isMet(Device device) {
		DeviceFilter[] filters = getFilters();
		for (int i = 0; i < filters.length; i++) {
			DeviceFilter filter = filters[i];
			if (!filter.isMet(device)) {
				return false;
			}
		}
		return true;
	}
}
