//#condition polish.usePolishGui
/*
 * Created on 06-Jan-2004 at 22:36:37.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ui.borders;

import de.enough.polish.ui.Border;

import javax.microedition.lcdui.Graphics;

/**
 * <p>Paints a plain border in one color.</p>
 *
 * @author Robert Virkus, robert@enough.de
 * <pre>
 * history
 *        06-Jan-2004 - rob creation
 * </pre>
 */
public class SimpleBorder extends Border {

	private int color;

	/**
	 * Creates a new simple border.
	 * 
	 * @param color the color of this border in RGB, e.g. 0xFFDD12
	 * @param borderWidth the width of this border
	 */
	public SimpleBorder( int color, int borderWidth ) {
		super();
		this.color = color;
		this.borderWidth = borderWidth;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Border#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		g.setColor( this.color );
		g.drawRect( x, y, width, height );
		if (this.borderWidth > 1) {
			int border = this.borderWidth - 1;
			while ( border > 0) {
				g.drawRect( x+border, y+border, width - 2*border, height - 2*border );
				border--;
			}
		}
	}

}
