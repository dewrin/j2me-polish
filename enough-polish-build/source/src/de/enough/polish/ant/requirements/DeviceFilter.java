/*
 * Created on 15-Feb-2004 at 18:44:12.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.requirements;

import de.enough.polish.Device;

/**
 * <p>Allows an object to filter specific devices.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        15-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public interface DeviceFilter {
	
	
	/**
	 * Checks if this filter allows the given device.
	 * 
	 * @param device the device which should be tested against this filter.
	 * @return true when this filter is satisfied by the given device.
	 */
	public boolean isMet( Device device );
}
