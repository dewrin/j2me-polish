//#condition polish.usePolishGui
/*
 * Created on 04-Jan-2004 at 18:48:09.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ui;

import javax.microedition.lcdui.Graphics;

/**
 * <p>Background is the base class for any backgrounds of widgets or forms.</p>
 *
 * @author Robert Virkus, robert@enough.de
 * <pre>
 * history
 *        04-Jan-2004 - rob creation
 * </pre>
 */
public abstract class Background {
	
	/**
	 * Defines the width of this Background.
	 * Usually this is 0, but some backgrounds might have a border included.
	 */
	protected int borderWidth;

	/**
	 * Creates a new Background.
	 * The width of this background is set to 0 here.
	 */
	public Background() {
		this.borderWidth = 0;
	}
	
	public boolean animate() {
		return false;
	}
	
	/**
	 * Paints this background.
	 * 
	 * @param x the horizontal start point
	 * @param y the vertical start point
	 * @param width the width of the background
	 * @param height the height of the background
	 * @param g the Graphics on which the background should be painted.
	 */
	public abstract void paint( int x, int y, int width, int height, Graphics g );

}
