//#condition polish.usePolishGui
/*
 * Created on 04-Jan-2004 at 19:37:35.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ui;

import javax.microedition.lcdui.Graphics;

/**
 * <p>Provides an abstract  border.</p>
 *
 * <p>copyright enough software 2003, 2004</p>
 * <pre>
 * history
 *        04-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public abstract class Border {
	
	protected int borderWidth;

	/**
	 * Creates a new border.
	 * The width of this border is set to the default value 1 here. 
	 */
	public Border() {
		this.borderWidth = 1;
	}
	
	/**
	 * Paints this border.
	 * 
	 * @param x  the horizontal start point
	 * @param y  the vertical start point
	 * @param width  the width of the border
	 * @param height  the height of the border
	 * @param g  the Graphics on which the border should be painted.
	 */
	public abstract void paint( int x, int y, int width, int height, Graphics g );
	
}
