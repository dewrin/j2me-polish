/*
 * Created on 15-Jan-2004 at 16:08:33.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish;


/**
 * <p>Respresents a group to which a device belongs to.</p>
 * <p>A group can be independent of a specific manufacturer, 
 * and a device can belong to several groups.
 * Also a group can belong do other groups.
 * </p>
 * 
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        15-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class DeviceGroup extends PolishComponent {

	/**
	 * Creates a new device group.
	 */
	public DeviceGroup() {
		super("group");
	}

	/**
	 * Creates a new device group.
	 * 
	 * @param parent the group to which this group belongs to.
	 */
	public DeviceGroup( DeviceGroup parent ) {
		super("group", parent );
	}
	
}
