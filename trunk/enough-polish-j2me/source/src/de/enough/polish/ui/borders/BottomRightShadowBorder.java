//#condition polish.usePolishGui
/*
 * Created on 06-Jan-2004 at 22:55:32.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ui.borders;

import de.enough.polish.ui.Border;

import javax.microedition.lcdui.Graphics;

/**
 * <p>Paints a border which is like shadow, which is seen on the bottom and on the right of the bordered Item.</p>
 *
 * @author Robert Virkus, robert@enough.de
 * <pre>
 * history
 *        06-Jan-2004 - rob creation
 * </pre>
 */
public class BottomRightShadowBorder extends Border {
	private int color;
	private int offset;

	/**
	 * Creates a new border which is like a shadow which is seen on the bottom and on the right of the Item.
	 * 
	 * @param color the color of this border in RGB, e.g. 0xFFDD12
	 * @param borderWidth the width of this border
	 * @param offset the offset of the shadow
	 */
	public BottomRightShadowBorder( int color, int borderWidth, int offset ) {
		super();
		this.color = color;
		this.borderWidth = borderWidth;
		this.offset = offset;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Border#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		g.setColor( this.color );
		int bottom = y + height;
		int right = x + width;
		int xOffset = x + this.offset;
		int yOffset = y + this.offset;
		// draw buttom line:
		g.drawLine( xOffset, bottom, right, bottom );
		// draw right line:
		g.drawLine( right, yOffset, right, bottom );
		
		if (this.borderWidth > 1) {
			int border = this.borderWidth - 1;
			while ( border > 0) {
				g.drawLine( xOffset, bottom - border, right, bottom - border );
				g.drawLine( right - border, yOffset, right - border, bottom );
				border--;
			}
		}
	}

}
