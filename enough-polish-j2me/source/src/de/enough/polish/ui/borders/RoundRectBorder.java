//#condition polish.usePolishGui
/*
 * Created on 06-Jan-2004 at 22:43:23.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ui.borders;

import de.enough.polish.ui.Border;

import javax.microedition.lcdui.Graphics;

/**
 * <p>Paints a border with round corners.</p>
 *
 * @author Robert Virkus, robert@enough.de
 * <pre>
 * history
 *        06-Jan-2004 - rob creation
 * </pre>
 */
public class RoundRectBorder extends Border {

	private int color;
	private int arcWidth;
	private int arcHeight;
	private int borderColor; 

	/**
	 * Creates a new round rectangle border.
	 * 
	 * @param color the color of the background
	 * @param borderWidth the width of the border
	 * @param arcWidth the horizontal diameter of the arc at the four corners
	 * @param arcHeight the vertical diameter of the arc at the four corners
	 */
	public RoundRectBorder( int color, int borderWidth, int arcWidth, int arcHeight ) {
		this.color = color;
		this.borderWidth = borderWidth;
		this.arcWidth = arcWidth;
		this.arcHeight = arcHeight;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Border#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		g.setColor( this.color );
		g.drawRoundRect( x, y, width, height, this.arcWidth, this.arcHeight );
		if (this.borderWidth > 1) {
			int border = this.borderWidth - 1;
			while ( border > 0) {
				g.drawRoundRect( x+border, y+border, width - 2*border, height - 2*border, this.arcWidth, this.arcHeight );
				border--;
			}
		}
	}


}
