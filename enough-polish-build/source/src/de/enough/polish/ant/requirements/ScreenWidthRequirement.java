/*
 * Created on 16-Feb-2004 at 14:44:23.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.requirements;

import de.enough.polish.Device;

/**
 * <p>Selects a device by the width of its screen.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        16-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class ScreenWidthRequirement extends IntRequirement {
	
	/**
	 * Creates a new requirement for the width of a device screen
	 * 
	 * @param value The needed width of the screen in pixel, e.g. "80+"
	 */
	public ScreenWidthRequirement(String value) {
		super(value, Device.SCREEN_WIDTH);
	}
}
