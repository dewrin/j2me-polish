//#condition polish.usePolishGui
/*
 * Created on 04-Jan-2004 at 19:37:35.
 *
 * Copyright (c) 2004 Robert Virkus / enough software
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
 * www.enough.de/j2mepolish for details.
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
