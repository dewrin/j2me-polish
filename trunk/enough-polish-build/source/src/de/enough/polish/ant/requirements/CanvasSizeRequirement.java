/*
 * Created on 24-Jan-2004 at 15:14:05.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.requirements;


/**
 * <p>Selects a device by its size of the canvas.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        24-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class CanvasSizeRequirement extends ScreenSizeRequirement {

	/**
	 * Creates a new requirement for the size of the canvas.
	 * 
	 * @param value the requirement value
	 */
	public CanvasSizeRequirement(String value) {
		super(value, "CanvasSize");
	}


}
