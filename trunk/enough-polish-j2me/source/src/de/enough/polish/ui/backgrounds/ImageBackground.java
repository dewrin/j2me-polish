//#condition polish.usePolishGui
/*
 * Created on 14-Mar-2004 at 21:31:51.
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
package de.enough.polish.ui.backgrounds;

import de.enough.polish.ui.*;
import de.enough.polish.ui.Background;
import de.enough.polish.ui.StyleSheet;
import de.enough.polish.util.Debug;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import java.io.IOException;

/**
 * <p>Paints an image as a background.</p>
 * <p>Following CSS parameters are supported:
 * <ul>
 * 		<li><b>image</b>: the image url, e.g. url( bg.png ) or none</li>
 * 		<li><b>color</b>: the background color, should the image
 * 							be smaller than the actual background-area. Or "transparent".</li>
 * 		<li><b>repeat</b>: defines whether the image should be repeated. Either "no-repeat",
 * 				"repeat", "repeat-x" or "repeat-y".</li>
 * 		<li><b></b>: </li>
 * </ul>
 * </p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        14-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class ImageBackground 
extends Background
//#ifdef polish.images.backgroundLoad
implements ImageConsumer
//#endif
{
	public static final int NO_REPEAT = -1;
	public static final int REPEAT = 1;
	public static final int REPEAT_X = 2;
	public static final int REPEAT_Y = 3;
	
	private Image image;
	private int color;
	private int repeatMode;
	
	/**
	 * Creates a new image background.
	 * 
	 * @param color the background color or Item.TRANSPARENT
	 * @param imageUrl the url of the image, e.g. "/bg.png", must not be null!
	 * @param repeatMode indicates whether the background image should
	 *        be repeated, either ImageBackground.NO_REPEAT, REPEAT, REPEAT_X or REPEAT_Y
	 */
	public ImageBackground( int color, String imageUrl, int repeatMode ) {
		this.color = color;
		this.repeatMode = repeatMode;
		try {
			this.image = StyleSheet.getImage(imageUrl, this, false);
		} catch (IOException e) {
			//#debug error
			Debug.debug( "unable to load image [" + imageUrl + "]: " + e.getMessage(), e );
		}
	}

	//#ifdef polish.images.backgroundLoad
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ImageConsumer#setImage(java.lang.String, javax.microedition.lcdui.Image)
	 */
	public void setImage(String name, Image image) {
		this.image = image;
	}
	//#endif
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		if (this.color != Item.TRANSPARENT) {
			g.setColor( this.color );
			g.fillRect( x, y, width + 1, height + 1 );
		}
		if (this.image != null) {
			if (this.repeatMode == NO_REPEAT) {
				int centerX = x + (width / 2);
				int centerY = y + (height / 2);
				g.drawImage(this.image, centerX, centerY, Graphics.HCENTER | Graphics.VCENTER );
			} else {
				int imgWidth = this.image.getWidth();
				int imgHeight = this.image.getHeight();
				int imgX = x;
				int imgY = y;
				int xStop = x + width;
				int yStop = y + height;
				if (this.repeatMode == REPEAT ) {					
					while (imgY < yStop ) {
						while ( imgX < xStop ) {
							g.drawImage(this.image, imgX, imgY, Graphics.LEFT | Graphics.TOP );
							imgX += imgWidth;
						}
						imgY += imgHeight;
						imgX = x;
					}
				} else if (this.repeatMode == REPEAT_X) {
					while ( imgX < xStop ) {
						g.drawImage(this.image, imgX, imgY, Graphics.LEFT | Graphics.TOP );
						imgX += imgWidth;
					}
				} else {
					// repeatMode == REPEAT_Y
					while (imgY < yStop ) {
						g.drawImage(this.image, imgX, imgY, Graphics.LEFT | Graphics.TOP );
						imgY += imgHeight;
					}
				}
			}
		}
	}
	
}
