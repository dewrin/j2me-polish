//#condition polish.usePolishGui
/*
 * Created on 06-Jan-2004 at 22:01:54.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ui.backgrounds;

import de.enough.polish.ui.Background;

import javax.microedition.lcdui.Graphics;

/**
 * <p>Paints a filled rectangle as a background in a specific color.</p>
 *
 * @author Robert Virkus, robert@enough.de
 * <pre>
 * history
 *        06-Jan-2004 - rob creation
 * </pre>
 */
public class SimpleBackground 
extends Background 
{
	
	private int color;

	/**
	 * Creates a new simple background.
	 * 
	 * @param color the color of the background in RGB, e.g. 0xFFDD11
	 */
	public SimpleBackground( int color ) {
		super();
		this.color = color;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		g.setColor( this.color );
		g.fillRect( x, y, width + 1, height + 1 );
	}

}
