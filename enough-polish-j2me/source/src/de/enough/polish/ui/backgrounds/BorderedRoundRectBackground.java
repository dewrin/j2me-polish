//#condition polish.usePolishGui
/*
 * Created on 06-Jan-2004 at 22:29:46.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ui.backgrounds;

import de.enough.polish.ui.Background;

import javax.microedition.lcdui.Graphics;

/**
 * <p>Paints a rectangle with round corners and a border.</p>
 * <p>This background-type can save some memory and processing time,
 *       when used instead of the RoundRectBackground and a RoundRectBorder together.
 * </p>
 *
 * @author Robert Virkus, robert@enough.de
 * <pre>
 * history
 *        06-Jan-2004 - rob creation
 * </pre>
 */
public class BorderedRoundRectBackground extends Background {

	private int color;
	private int arcWidth;
	private int arcHeight;
	private int borderColor; 

	/**
	 * Creates a new round rectangle background with a border.
	 * 
	 * @param color the color of the background
	 * @param arcWidth the horizontal diameter of the arc at the four corners
	 * @param arcHeight the vertical diameter of the arc at the four corners
	 * @param borderColor the color of the border
	 * @param borderWidth the width of the border
	 */
	public BorderedRoundRectBackground( int color,  int arcWidth, int arcHeight, int borderColor, int borderWidth) {
		this.color = color;
		this.arcWidth = arcWidth;
		this.arcHeight = arcHeight;
		this.borderColor = borderColor;
		this.borderWidth = borderWidth;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		g.setColor( this.color );
		g.fillRoundRect( x, y, width + 1, height + 1, this.arcWidth, this.arcHeight );
		g.setColor( this.borderColor );
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
