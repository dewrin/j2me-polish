//#condition polish.usePolishGui
/*
 * Created on 06-Jan-2004 at 22:17:53.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ui.backgrounds;

import de.enough.polish.ui.Background;

import javax.microedition.lcdui.Graphics;

/**
 * <p>Paints a filled rectangle with a border around it as a background.</p>
 * <p>This background-type can save some memory and processing time,
 *       when used instead of the SimpleBackground and a SimpleBorder together.
 * </p>
 *
 * @author Robert Virkus, robert@enough.de
 * <pre>
 * history
 *        06-Jan-2004 - rob creation
 * </pre>
 */
public class BorderedSimpleBackground extends Background {
	
	private int color;
	private int borderColor; 

	/**
	 * Creates a new simple background with a border.
	 * 
	 * @param color the color of the background
	 * @param borderColor the color of the border
	 * @param borderWidth the width of the border
	 */
	public BorderedSimpleBackground( int color, int borderColor, int borderWidth) {
		this.color = color;
		this.borderColor = borderColor;
		this.borderWidth = borderWidth;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		g.setColor( this.color );
		g.fillRect( x, y, width + 1, height + 1 );
		g.setColor( this.borderColor );
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
