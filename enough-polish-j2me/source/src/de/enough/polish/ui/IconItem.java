//#condition polish.usePolishGui
/*
 * Created on 04-Apr-2004 at 21:30:32.
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

import de.enough.polish.util.Debug;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import java.io.IOException;

/**
 * <p>Shows a string with an optional image attached to it.</p>
 * <p>The dynamic CSS selector of the IconItem is "icon".</p>
 * <p>
 * Following CSS attributes can be set:
 * <ul>
 * 		<li><b>icon-image-align</b>: The position of the icon-image relative to the text,
 * 				either "right", "left", "top" or "bottom". Default is "left".</li>
 * 		<li><b>icon-image</b>: The name of the image for this icon. The name can
 * 			include the index of this item relative to the parent-container:
 * 		    The icon-image "%INDEX%icon.png" would be renamed to "0icon.png" when
 * 			this icon would be the first one in a list.</li>
 * 		<li><b></b>: </li>
 * </ul>
 * </p>
 * 
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        04-Apr-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class IconItem extends StringItem 
//#ifdef polish.images.backgroundLoad
implements ImageConsumer
//#endif
{
	
	private static final int DEFAULT_ALIGN = Graphics.LEFT;
	private Image image;
	private int imageAlign;
	private int imageHeight;
	private int imageWidth;
	private int yAdjust;

	/**
	 * Creates a new icon.
	 * 
	 * @param text the text of this item
	 * @param image the image of this item, null when no image should be displayed
	 */
	public IconItem( String text, Image image ) {
		this( text, image, null );
	}

	/**
	 * Creates a new icon.
	 * 
	 * @param text the text of this item
	 * @param image the image of this item, null when no image should be displayed
	 * @param style the style of this item
	 */
	public IconItem( String text, Image image, Style style) {
		super(null, text, Item.HYPERLINK, style);
		if (image != null && this.image == null) {
			setImage( image );
		}
	}
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#createCssSelector()
	 */
	protected String createCssSelector() {
		return "icon";
	}
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#initItem()
	 */
	protected void initContent(int firstLineWidth, int lineWidth) {
		if (this.image == null) {
			super.initContent(firstLineWidth, lineWidth);
			return;
		} 
		if (this.imageAlign == Graphics.LEFT || this.imageAlign == Graphics.RIGHT ) {
			firstLineWidth -= this.imageWidth;
			lineWidth -= this.imageWidth;
			super.initContent(firstLineWidth, lineWidth);
			if (this.imageHeight > this.contentHeight) {
				this.yAdjust = (this.imageHeight - this.contentHeight) / 2;
				this.contentHeight = this.imageHeight;
			} else {
				this.yAdjust = 0;
			}
			this.contentWidth += this.imageWidth;
		} else {
			super.initContent(firstLineWidth, lineWidth);
			this.contentHeight += this.imageHeight;   
			if (this.imageWidth > this.contentWidth) {
				this.contentWidth = this.imageWidth;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#paintContent(int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		if (this.image != null) {
			if (this.imageAlign == Graphics.LEFT ) {
				g.drawImage(this.image, x, y, Graphics.TOP | Graphics.LEFT );
				x += this.imageWidth;
				leftBorder += this.imageWidth;
				y += this.yAdjust;
			} else if (this.imageAlign == Graphics.RIGHT ) {
				g.drawImage(this.image, rightBorder, y, Graphics.TOP | Graphics.RIGHT );
				rightBorder -= this.imageWidth;
				y += this.yAdjust;
			} else if (this.imageAlign == Graphics.TOP ) {
				int centerX = leftBorder + ((rightBorder - leftBorder) / 2);
				//System.out.println("left: " + leftBorder + "  right: " + rightBorder + "  contentWidth: " + this.contentWidth);
				//System.out.println("x: " + x + "  centerX: " + centerX );
				g.drawImage(this.image, centerX, y, Graphics.TOP | Graphics.HCENTER );
				y += this.imageHeight;
			} else {
				// imageAlign == Graphics.BOTTOM
				int centerX = leftBorder + ((rightBorder - leftBorder) / 2);
				int bottomY = y + this.contentHeight;
				g.drawImage(this.image, centerX, bottomY, Graphics.BOTTOM | Graphics.HCENTER );
			}
		}
		super.paintContent(x, y, leftBorder, rightBorder, g);
	}
	
	/**
	 * Retrieves the image of this item.
	 * 
	 * @return the image of this icon.
	 */
	public Image getImage() {
		return this.image;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		super.setStyle(style);
		String align = (String) style.getProperty("icon-image-align");
		if (align == null) {
			// keep align setting
		} else if ("left".equals(align)) {
			this.imageAlign = Graphics.LEFT;
		} else if ("right".equals(align)) {
			this.imageAlign = Graphics.RIGHT;
		} else if ("top".equals(align)) {
			this.imageAlign = Graphics.TOP;
		} else if ("bottom".equals(align)) {
			this.imageAlign = Graphics.BOTTOM;
		} else {
			this.imageAlign = DEFAULT_ALIGN;
		}
		if (this.image == null) {
			String imageName = (String) style.getProperty("icon-image");
			if (imageName != null) {
				if (this.parent instanceof Container) {
					imageName = ((Container) this.parent).parseIndexUrl( imageName, this );
				}
				try {
					setImage( StyleSheet.getImage(imageName, this, false) );
				} catch (IOException e) {
					//#debug error
					Debug.debug("unable to load image [" + imageName + "]: " + e.getMessage(), e);
				}
			}
		}
	}

	//#ifdef polish.images.backgroundLoad
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ImageConsumer#setImage(java.lang.String, javax.microedition.lcdui.Image)
	 */
	public void setImage(String name, Image image) {
		setImage( image );
		repaint();
	}
	//#endif
	
	/**
	 * Sets the image for this icon.
	 * 
	 * @param image the image for this icon, when null is given, no image is painted.
	 */
	public void setImage( Image image ) {
		this.isInitialised = false;
		this.image = image;
		if (image != null) {
			if (this.imageAlign == Graphics.LEFT || this.imageAlign == Graphics.RIGHT ) {
				this.imageWidth = this.image.getWidth() + this.paddingHorizontal;
				this.imageHeight = this.image.getHeight();
			} else {
				this.imageWidth = this.image.getWidth();
				this.imageHeight = this.image.getHeight() + this.paddingVertical;
			}
		} else {
			this.imageWidth = 0;
			this.imageHeight = 0;
		}
		
	}
}
