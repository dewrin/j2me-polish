//#condition polish.usePolishGui
/*
 * Created on 01-Mar-2004 at 09:45:32.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ui;

import de.enough.polish.util.ArrayList;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

/**
 * <p>Contains a number of items.</p>
 * <p>Main purpose is to manage all items of a Form or similiar canvasses.</p>
 * <p>Containers support following CSS attributes:
 * <ul>
 * 		<li><b>focused</b>: The name of the focused style, e.g. "style( funnyFocussed );"
 * 				</li>
 * 		<li><b></b>: </li>
 * </ul>
 * </p>
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        01-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class Container extends Item {
	
	protected ArrayList itemsList;
	protected Item[] items;
	protected boolean focusFirstElement;
	protected Style focusedStyle;
	private Style itemStyle;
	private Item focusedItem;
	private int focusedIndex = -1;
	
	/**
	 * Creates a new empty container.
	 * 
	 * @param focusFirstElement true when the first focussable element should be focused automatically.
	 */
	public Container( boolean focusFirstElement ) {
		this( focusFirstElement, null );
	}
	
	/**
	 * Creates a new empty container.
	 * 
	 * @param focusFirstElement true when the first focussable element should be focused automatically.
	 * @param style the style for this container
	 */
	public Container(boolean focusFirstElement, Style style) {
		super( style );
		this.itemsList = new ArrayList();
		this.focusFirstElement = focusFirstElement;
		if (this.focusedStyle == null) {
			this.focusedStyle = StyleSheet.focusedStyle;
		}
	}

	public void add( Item item ) {
		this.isInitialised = false;
		//#ifdef polish.useDynamicStyles
		item.parent = this;
		//#endif
		this.itemsList.add( item );
	}

	public void add( int index, Item item ) {
		this.isInitialised = false;
		//#ifdef polish.useDynamicStyles
		item.parent = this;
		//#endif
		this.itemsList.add( index, item );
	}
	
	public void set( int index, Item item ) {
		this.isInitialised = false;
		//#ifdef polish.useDynamicStyles
		item.parent = this;
		//#endif
		this.itemsList.set( index, item );
	}
	
	public Item get( int index ) {
		return (Item) this.itemsList.get( index );
	}
	
	public Item remove( int index ) {
		this.isInitialised = false;
		return (Item) this.itemsList.remove(index);
	}
	
	public boolean remove( Item item ) {
		this.isInitialised = false;
		return this.itemsList.remove( item ); 
	}
	
	/**
	 * Removes all items from this container.
	 */
	public void clear() {
		this.isInitialised = false;
		this.itemsList.clear();
	}
	
	/**
	 * Retrieves the number of items stored in this container.
	 * 
	 * @return The number of items stored in this container.
	 */
	public int size() {
		return this.itemsList.size();
	}
	
	/**
	 * Retrieves all items which this container holds.
	 * The items might not have been intialised.
	 * 
	 * @return an array of all items.
	 */
	public Item[] getItems() {
		/* if (!this.isInitialised) {
			init();
		} */
		return this.items;
	}
	
	/**
	 * Focusses the specified item.
	 * 
	 * @param index the index of the item. The first item has the index 0. 
	 * @return true when the specified item could be focused.
	 * 		   It needs to have an appearanceMode which is not Item.PLAIN to
	 *         be focussable.
	 */
	public boolean focus(int index) {
		Item item = this.items[ index ];
		if (item.appearanceMode != Item.PLAIN) {
			focus( index, item );
			return true;
		}
		return false;
	}
	
	/**
	 * Sets the focus to the given item.
	 * 
	 * @param index the position
	 * @param item the item which should be focused
	 */
	private void focus( int index, Item item ) {
		// first defocus the last focused item:
		if (this.focusedItem != null) {
			this.focusedItem.setStyle(this.itemStyle);
		}
		// save style of the to be focused item:
		this.itemStyle = item.getStyle();
		this.focusedIndex = index;
		this.focusedItem = item;
		item.setStyle( this.focusedStyle );
		this.isInitialised = false;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#initItem( int, int )
	 */
	protected void initContent(int firstLineWidth, int lineWidth) {
		Item[] myItems = (Item[]) this.itemsList.toArray( new Item[ this.itemsList.size() ]);
		int myContentWidth = 0;
		int myContentHeight = 0;
		//TODO rob: firstLineWidth ist nicht korrekt fuer die items!
		// (border, margin und padding einrechnen)
		for (int i = 0; i < myItems.length; i++) {
			Item item = myItems[i];
			int width = item.getItemWidth( firstLineWidth, lineWidth );
			int height = item.getItemHeight( firstLineWidth, lineWidth );
			// now the item should have a style:
			if (this.focusFirstElement && (item.appearanceMode != Item.PLAIN)) {
				focus( i, item );
				height = item.getItemHeight( firstLineWidth, lineWidth );
				width = item.getItemWidth( firstLineWidth, lineWidth );
				this.focusFirstElement = false;
			}
			if (width > myContentWidth) {
				myContentWidth = width; 
			}
			myContentHeight += height;
		}
		this.contentHeight = myContentHeight;
		this.contentWidth = myContentWidth;
		this.items = myItems;
	}

	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#paintItem(int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		// paints all items,
		// the layout will be done according to this containers'
		// layout or according to the items layout, when specified.
		for (int i = 0; i < this.items.length; i++) {
			Item item = this.items[i];
			//TODO layout items
			item.paint(x, y, leftBorder, rightBorder, g);
			y += item.itemHeight;
		}
	}

	//#ifdef polish.useDynamicStyles
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#getCssSelector()
	 */
	protected String createCssSelector() {
		return "container";
	}
	//#endif

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handleKeyPressed(int, int)
	 */
	protected boolean handleKeyPressed(int keyCode, int gameAction) {
		if (this.focusedItem != null) {
			if ( this.focusedItem.handleKeyPressed(keyCode, gameAction) ) {
				return true;
			}
			
		}
		if ( gameAction == Canvas.RIGHT || gameAction == Canvas.DOWN ) {
			return shiftFocus( true );
		} else if ( gameAction == Canvas.LEFT || gameAction == Canvas.UP ) {
			return shiftFocus( false );
		} else {
			return false;
		}
	}

	/**
	 * Shifts the focus to the next or the previous item.
	 * 
	 * @param focusNext true when the next item should be focused, false when
	 * 		  the previous item should be focused.
	 * @return true when the focus could be moved to either the next or the previous item.
	 */
	private boolean shiftFocus(boolean focusNext ) {
		int i = this.focusedIndex;
		Item item = null;
		while (true) {
			if (focusNext) {
				i++;
				if (i >= this.items.length) {
					break;
				}
			} else {
				i--;
				if (i < 0) {
					break;
				}
			}
			item = this.items[i];
			if (item.appearanceMode != Item.PLAIN) {
				break;
			}
		}
		if (item == null || item.appearanceMode == Item.PLAIN) {
			return false;
		}
		focus(i, item);
		return true;
	}

	/**
	 * Retrieves the index of the item which is currently focused.
	 * 
	 * @return the index of the focused item, -1 when none is focused.
	 */
	public int getFocussedIndex() {
		return this.focusedIndex;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		setStyle(style, false);
	}
	
	/**
	 * Sets the style of this container.
	 * 
	 * @param style the style
	 * @param ignoreBackground when true is given, the background and border-settings
	 * 		  will be ignored.
	 */
	public void setStyle( Style style, boolean ignoreBackground) {
		super.setStyle(style);
		if (ignoreBackground) {
			this.background = null;
			this.border = null;
			this.borderWidth = 0;
		}
		String focused = style.getProperty("focused-style");
		if (focused != null) {
			Style focStyle = StyleSheet.getStyle( focused );
			if (focStyle != null) {
				this.focusedStyle = focStyle;
			}
		}
	}

	/**
	 * Parses the given URL and includes the index of the item, when there is an "%INDEX%" within the given url.
	 * @param url the resource URL which might include the substring "%INDEX%"
	 * @param item the item to which the URL belongs to. The item must be 
	 * 		  included in this container.
	 * @return the URL in which the %INDEX% is substituted by the index of the
	 * 		   item in this container. The url "icon%INDEX%.png" is resolved
	 * 		   to "icon1.png" when the item is the second item in this container.
	 * @throws NullPointerException when the given url or item is null
	 */
	public String parseIndexUrl(String url, Item item) {
		int pos = url.indexOf("%INDEX%");
		if (pos != -1) {
			int index = this.itemsList.indexOf( item );
			//TODO rob check if valid, when url ends with %INDEX%
			url = url.substring(0, pos) + index + url.substring( pos + 7 );
		}
		return url;
	}

}
