/*
 * Created on 16-Feb-2004 at 14:48:05.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.requirements;

import de.enough.polish.Device;

/**
 * <p>Selects a device by its screen height.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        16-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class ScreenHeightRequirement extends IntRequirement {
	
	/**
	 * Creates a new requirement for the screen height of a device.
	 * 
	 * @param value The needed height of the screen, e.g. "100+"
	 */
	public ScreenHeightRequirement(String value) {
		super(value, Device.SCREEN_HEIGHT);
	}
}
