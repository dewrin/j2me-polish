//#condition polish.usePolishGui
/*
 * Created on 06-Jan-2004 at 22:36:37.
 *
 * Copyright (c) 2004 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
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
