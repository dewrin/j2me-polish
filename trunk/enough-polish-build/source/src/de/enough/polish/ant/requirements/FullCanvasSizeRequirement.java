/*
 * Created on 24-Jan-2004 at 15:57:09.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.requirements;


/**
 * <p>Selects a device by its canvas-size in the full screen mode.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        24-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class FullCanvasSizeRequirement 
extends ScreenSizeRequirement 
{

	/**
	 * Creates a new requirement for the size of the fullscreen-canvas.
	 * 
	 * @param value the needed size, e.g. "40+ x 100+"
	 */
	public FullCanvasSizeRequirement( String value ) {
		super( value, "FullCanvasSize");
	}

}
