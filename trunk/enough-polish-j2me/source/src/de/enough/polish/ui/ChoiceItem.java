//#condition polish.usePolishGui
/*
 * Created on 05-May-2004 at 15:11:20.
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

import javax.microedition.lcdui.*;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import java.io.IOException;

/**
 * <p>Paints a single item of a choice group.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        05-May-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class ChoiceItem extends IconItem 
//#ifdef polish.images.backgroundLoad
implements ImageConsumer
//#endif
{
	private static Image defaultRadioSelected;
	private static Image defaultRadioPlain;
	private static Image defaultCheckSelected;
	private static Image defaultCheckPlain;
	
	private boolean isMultiple;
	private Image selected;
	private Image plain;
	boolean isSelected;
	private Image boxImage;
	private int boxWidth;
	private int yAdjust;
	private boolean drawBox;
	private int choiceType;
	protected Font preferredFont;
	private int boxColor;
	//#ifdef polish.images.backgroundLoad
	private String selectedImgName;
	private String plainImgName;
	//#endif

	/**
	 * Creates a new ChoiceItem.
	 * 
	 * @param text the text of the item
	 * @param image the image of the item
	 * @param choiceType the type of this item, either 
	 * 		Choice.EXCLUSIVE, Choice.MULTIPLE, Choice.IMPLICIT or Choice.POPUP.
	 * 		When IMPLICIT or POPUP is used, no selection-box will be drawn. 
	 */
	public ChoiceItem(String text, Image image, int choiceType ) {
		this( text, image, choiceType, null );
	}
	
	/**
	 * Creates a new ChoiceItem.
	 * 
	 * @param text the text of the item
	 * @param image the image of the item
	 * @param choiceType the type of this item, either 
	 * 		Choice.EXCLUSIVE, Choice.MULTIPLE, Choice.IMPLICIT or Choice.POPUP.
	 * 		When IMPLICIT or POPUP is used, no selection-box will be drawn. 
	 * @param style the CSS style of this item
	 */
	public ChoiceItem(String text, Image image, int choiceType, Style style) {
		super(text, image, style);
		this.drawBox = (choiceType == Choice.EXCLUSIVE) || (choiceType == Choice.MULTIPLE);
		this.choiceType = choiceType;
		this.isMultiple = (choiceType == Choice.MULTIPLE);
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#createCssSelector()
	 */
	protected String createCssSelector() {
		if (this.choiceType == Choice.EXCLUSIVE) {
			return "radiobox";
		} else if (this.choiceType == Choice.MULTIPLE) {
			return "checkbox";
		} else if (this.choiceType == Choice.IMPLICIT){
			return "listitem";
		} else {
			return "popup";
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#initContent(int, int)
	 */
	protected void initContent(int firstLineWidth, int lineWidth) {
		if (!this.drawBox) {
			super.initContent(firstLineWidth, lineWidth);
			return;
		}
		if (this.plain == null) {
			this.plain = createPlain();
		}
		if (this.selected == null) {
			this.selected = createSelected();
		}
		int maxWidth = this.plain.getWidth();
		if (this.selected.getWidth() > maxWidth ) {
			maxWidth = this.selected.getWidth();
		}
		maxWidth += this.paddingHorizontal;
		this.boxWidth = maxWidth;
		int maxHeight = this.plain.getHeight();
		if (this.selected.getHeight() > maxHeight ) {
			maxHeight = this.selected.getHeight();
		}
		firstLineWidth -= maxWidth;
		lineWidth -=  maxWidth;
		
		super.initContent(firstLineWidth, lineWidth);
		
		this.contentWidth += maxWidth;
		if (this.contentHeight < maxHeight) {
			this.yAdjust = maxHeight - this.contentHeight;
			this.contentHeight = maxHeight;
		} else {
			this.yAdjust = 0;
		}
		if (this.isSelected) {
			this.boxImage = this.selected;
		} else {
			this.boxImage = this.plain;
		}
	}
	
	/**
	 * Creates a selected image.
	 * 
	 * @return the image for a selected item
	 */
	private Image createSelected() {
		if (this.isMultiple) {
			if (defaultCheckSelected == null ) {
				defaultCheckSelected = Image.createImage(12, 12);
				Graphics g = defaultCheckSelected.getGraphics();
				g.setColor( this.boxColor );
				g.drawRect(0, 0, 11, 11 );
				g.drawRect(1, 1, 9, 9 );
				g.drawLine(0, 0, 11, 11 );
				g.drawLine( 11, 0, 0, 11 );
			}
			return defaultCheckSelected;
		} else {
			if (defaultRadioSelected == null ) {
				defaultRadioSelected = Image.createImage(11, 11);
				Graphics g = defaultRadioSelected.getGraphics();
				g.setColor( this.boxColor );
				g.fillRect(0, 0, 12, 12 );
				g.setColor( 0xFFFFFF );
				g.fillArc(1, 1, 9, 9, 0, 360 );
				g.setColor( this.boxColor );
				g.fillArc(3, 3, 5, 5, 0, 360 );
			}
			return defaultRadioSelected;
		}
	}

	/**
	 * Creates a plain image
	 * @return the image for a plain item
	 */
	private Image createPlain() {
		if (this.isMultiple) {
			if (defaultCheckPlain == null ) {
				defaultCheckPlain = Image.createImage(12, 12);
				Graphics g = defaultCheckPlain.getGraphics();
				g.setColor( this.boxColor );
				g.drawRect(0, 0, 11, 11 );
				g.drawRect(1, 1, 9, 9 );
			}
			return defaultCheckPlain;
		} else {
			if (defaultRadioPlain == null ) {
				defaultRadioPlain = Image.createImage(11, 11);
				Graphics g = defaultRadioPlain.getGraphics();
				g.setColor( this.boxColor );
				g.fillRect(0, 0, 12, 12 );
				g.setColor( 0xFFFFFF );
				g.fillArc(1, 1, 9, 9, 0, 360 );
			}
			return defaultRadioPlain;
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paintContent(int x, int y, int leftBorder, int rightBorder,
			Graphics g) {
		if (this.drawBox) {
			g.drawImage( this.boxImage, x, y, Graphics.TOP | Graphics.LEFT );
			x += this.boxWidth;
			leftBorder += this.boxWidth;
			y += this.yAdjust;
		}
		super.paintContent(x, y, leftBorder, rightBorder, g);
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		super.setStyle(style);
		if (this.drawBox) {
			String type = createCssSelector();
			String selectedName = style.getProperty( type + "-selected");
			if (selectedName != null) {
				//#ifdef polish.images.backgroundLoad
					this.selectedImgName = selectedName;
				//#endif
				try {
					this.selected = StyleSheet.getImage( selectedName, this, true );
				} catch (IOException e) {
					//#debug error
					Debug.debug("Unable to load image [" + selectedName + "]: " + e.getMessage() , e );
				}
			}
			String plainName = style.getProperty( type + "-plain");
			if (plainName != null) {
				//#ifdef polish.images.backgroundLoad
					this.plainImgName = plainName;
				//#endif
				try {
					this.plain = StyleSheet.getImage( plainName, this, true );
				} catch (IOException e) {
					//#debug error
					Debug.debug("Unable to load image [" + plainName + "]: " + e.getMessage() , e );
				}
			}
			String colorStr = style.getProperty("choice-color");
			if (colorStr != null) {
				this.boxColor = Integer.parseInt( colorStr );
			}
		} // if draw box
	}
	
	//#ifdef polish.images.backgroundLoad
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ImageConsumer#setImage(java.lang.String, javax.microedition.lcdui.Image)
	 */
	public void setImage(String name, Image image) {
		if (!this.drawBox) {
			super.setImage( name, image );
		}
		if (!this.drawBox) {
			super.setImage( name, image );
		} else if (name.equals(this.selectedImgName)) {
			Debug.debug("setting selected image");
			this.selected = image;
		} else if (name.equals(this.plainImgName)) {
			Debug.debug("setting plain image");
			this.plain = image;
		} else {
			setImage( image );
		}
		this.isInitialised = false;
		/*
		this.isInitialised = false;
		repaint();
		*/
	}
	//#endif

	/**
	 * Selects or deselects this item.
	 * 
	 * @param select true when this item should be selected
	 */
	public void select(boolean select ) {
		this.isSelected = select;
		if (select) {
			this.boxImage = this.selected;
		} else {
			this.boxImage = this.plain;
		}
	}
	
	/**
	 * Changes the selected-state to the opposite state.
	 */
	public void toggleSelect() {
		select( !this.isSelected );
	}

	/**
	 * Sets the preferred font.
	 * This setting is ignored, since the J2ME Polish implementation
	 * uses the CSS settings.
	 *  
	 * @param font the preferred font
	 */
	public void setPreferredFont(Font font) {
		this.preferredFont = font;
	}
	
}
