//#condition polish.usePolishGui
/*
 * Created on 12-Mar-2004 at 21:46:17.
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

//#if polish.useFullScreen && polish.api.nokia-ui 
	import com.nokia.mid.ui.FullCanvas;
//#endif
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.Debug;

import javax.microedition.lcdui.*;


/**
 * The common superclass of all high-level user interface classes.
 * 
 * The common superclass of all high-level user interface classes. The
 * contents displayed and their interaction with the user are defined by
 * subclasses.
 * 
 * <P>Using subclass-defined methods, the application may change the contents
 * of a <code>Screen</code> object while it is shown to the user.  If
 * this occurs, and the
 * <code>Screen</code> object is visible, the display will be updated
 * automatically.  That
 * is, the implementation will refresh the display in a timely fashion without
 * waiting for any further action by the application.  For example, suppose a
 * <code>List</code> object is currently displayed, and every element
 * of the <code>List</code> is
 * visible.  If the application inserts a new element at the beginning of the
 * <code>List</code>, it is displayed immediately, and the other
 * elements will be
 * rearranged appropriately.  There is no need for the application to call
 * another method to refresh the display.</P>
 * 
 * <P>It is recommended that applications change the contents of a
 * <code>Screen</code> only
 * while it is not visible (that is, while another
 * <code>Displayable</code> is current).
 * Changing the contents of a <code>Screen</code> while it is visible
 * may result in
 * performance problems on some devices, and it may also be confusing if the
 * <code>Screen's</code> contents changes while the user is
 * interacting with it.</P>
 * 
 * <P>In MIDP 2.0 the four <code>Screen</code> methods that defined
 * read/write ticker and
 * title properties were moved to <code>Displayable</code>,
 * <code>Screen's</code> superclass.  The
 * semantics of these methods have not changed.</P>
 * <HR>
 * 
 * @since MIDP 1.0
 * 
 */
public abstract class Screen 
//#if polish.useFullScreen && polish.classes.fullscreen:defined
//#define tmp.fullScreen
//#= extends ${polish.classes.fullscreen}
//#else
extends Canvas
//#endif
//#if polish.debugVerbose && polish.useDebugGui
implements CommandListener
//#endif
{
	
	protected StringItem title;
	protected String titleText;
	protected int titleHeight;
	protected Background background;
	protected Border border;
	protected Style style;
	protected int screenHeight;
	protected int screenWidth;
	protected Ticker ticker;
	protected String cssSelector;
	protected CommandListener cmdListener;
	protected Container container;
	protected boolean isLayoutVCenter;
	protected boolean isInitialised;
	//#if polish.useFullScreen && polish.api.nokia-ui 
		private boolean isInPaintMethod;
		private long lastPaintTime;
		private boolean repaintRequested;
	//#endif
	//#if polish.useMenuFullScreen && polish.classes.fullscreen:defined
		//#define tmp.menuFullScreen
		private Command menuSingleCommand;
		private Container menuContainer;
		private ArrayList menuCommands;
		private boolean menuOpened;
		private Font menuFont;
		private int menuFontColor = 0;
		private int menuBarHeight;
		/*
		//#ifdef polish.ScreenWidth:defined
			//#= private int menuMaxWidth = (  ${ polish.ScreenWidth } * 2 ) / 3;
		//#else
			private int menuMaxWidth = ( getWidth() * 2 ) / 3;
		//#endif
		 */
		private int menuMaxWidth = 120;
		private int menuBarColor = 0xFFFFFF;
		private int fullScreenHeight;
	//#endif
	/** The Gauge Item which should be animated by this screen */
	protected Item gauge;

	/**
	 * Creates a new screen
	 * 
	 * @param title the Screen's title, or null for no title
	 */
	public Screen( String title)
	{
		this( title, null );
	}
	
	/**
	 * Creates a new screen
	 * 
	 * @param title the title, or null for no title
	 * @param style the style of this screen
	 */
	public Screen( String title, Style style ) {
		super();
		try {
		// get the screen dimensions:
		// this is a bit complicated, since Nokia's FullCanvas fucks
		// up when calling super.getHeight(), so we need to use hardcoded values...
		
		//#ifdef tmp.menuFullScreen
			//#ifdef polish.ScreenHeight:defined
				//#= this.fullScreenHeight = ${ polish.ScreenHeight };
			//#else
				this.fullScreenHeight = super.getHeight();
			//#endif
			this.screenHeight = this.fullScreenHeight - this.menuBarHeight;
		//#else
			//#ifdef polish.CanvasHeight:defined
				//#= this.screenHeight = ${ polish.CanvasHeight };
			//#else
				this.screenHeight = getHeight();
			//#endif
		//#endif
		
		//#ifdef polish.ScreenWidth:defined
			//#= this.screenWidth = ${ polish.ScreenWidth };
		//#else
			this.screenWidth = getWidth();
		//#endif
						
		// creating standard container:
		this.container = new Container( true );
		this.container.screen = this;
		this.titleText = title;
		this.style = style;
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw new RuntimeException("Error in Screen Constructor: " + e.toString() );
		}
	}
		
	/**
	 * Initialises this screen before it is painted for the first time.
	 */
	public void init() {
		if (this.style != null) {
			setStyle( this.style );
		}
		// inform all root items that they belong to this screen:
		if (this.container != null) {
			this.container.screen = this;
			//TODO rob check scrolling dimensions
			// this.container.setVerticalDimensions( this.titleHeight, this.screenHeight );			
			this.container.setVerticalDimensions( 0, this.screenHeight - this.titleHeight );
			Debug.debug("using vertical dimensions: 0, " + (this.screenHeight - this.titleHeight));
		}
		Item[] items = getRootItems();
		for (int i = 0; i < items.length; i++) {
			Item item = items[i];
			item.screen = this;
		}
		//#ifdef polish.useDynamicStyles
			// check if this screen has got a style:
			if (this.style == null) {
				this.cssSelector = createCssSelector();
				setStyle( StyleSheet.getStyle( this ) );
			} else {
				this.cssSelector = this.style.name;
			}
		//#endif
		//#ifdef tmp.menuFullScreen
			 Style menuStyle = StyleSheet.getStyle("menu");
			if (menuStyle != null) {
				String colorStr = menuStyle.getProperty("menubar-color");
				if (colorStr != null) {
					this.menuBarColor = Integer.parseInt(colorStr);
				}
				this.menuFontColor = menuStyle.labelFontColor;
				if (menuStyle.labelFont != null) {
					this.menuFont = menuStyle.labelFont;
				} else {
					this.menuFont = Font.getFont( Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM );				
				}			
			} else {
				this.menuFont = Font.getFont( Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM );
			}
			this.menuBarHeight = this.menuFont.getHeight() + 2;
			this.screenHeight = this.fullScreenHeight - this.menuBarHeight;
		//#endif
			
		// set the title:
		setTitle( this.titleText );
		// start the animmation thread if necessary: 
		if (StyleSheet.animationThread == null) {
			StyleSheet.animationThread = new AnimationThread();
			StyleSheet.animationThread.start();
		}
		this.isInitialised = true;
		// register this screen:
		StyleSheet.currentScreen = this;
	}

	/**
	 * Sets the style of this screen.
	 * 
	 * @param style the style
	 */
	public void setStyle(Style style) {
		this.style = style;
		this.background = style.background;
		this.border = style.border;
		this.container.setStyle(style, true);
		this.isLayoutVCenter = (( style.layout & Item.LAYOUT_VCENTER ) == Item.LAYOUT_VCENTER);
	}
	
	public boolean animate() {
		//#ifdef polish.debugVerbose
			try {
		//#endif
				
		//TODO rob animate ticker, container, border
		boolean animated = false;
		if (this.background != null) {
			animated = this.background.animate();
		}
		if (this.container != null) {
			animated = animated | this.container.animate();
		}
		if (this.gauge != null) {
			animated = animated | this.gauge.animate();
		}
		//#if polish.useFullScreen && polish.api.nokia-ui 
		if (animated || this.repaintRequested) {
			this.repaintRequested = false;
		//#else
			//# if (animated) {
		//#endif
			repaint();
			return true;
		} else {
			return false;
		}
		//#ifdef polish.debugVerbose
			} catch (Exception e) {
				Debug.debug("animate() threw an exception", e );
				//#ifdef polish.useDebugGui
					// set the current screen to the debug-screen:
					StyleSheet.display.setCurrent( Debug.getLogForm(true, this) );
				//#endif
				return false;
			}
		//#endif
	}
	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#paint(javax.microedition.lcdui.Graphics)
	 */
	public final void paint(Graphics g) {
		//#if polish.useFullScreen && polish.api.nokia-ui 
			this.isInPaintMethod = true;
		//#endif
		try {
			if (!this.isInitialised) {
				init();
			}
		// paint background:
		if (this.background != null) {
			//#ifdef tmp.menuFullScreen
				this.background.paint(0, 0, this.screenWidth, this.fullScreenHeight, g);
			//#else
				this.background.paint(0, 0, this.screenWidth, this.screenHeight, g);
			//#endif
		} else {
			g.setColor( 0xFFFFFF );
			//#ifdef tmp.menuFullScreen
				g.fillRect( 0, 0, this.screenWidth, this.fullScreenHeight );
			//#else
				g.fillRect( 0, 0, this.screenWidth, this.screenHeight );
			//#endif
		}
		// paint title:
		if (this.title != null) {
			this.title.paint(0, 0, 0, this.screenWidth, g);
		}
		int y = this.titleHeight;
		// protect the title and the full-screen-menu area:
		g.setClip(0, y, this.screenWidth, this.screenHeight - y );
		g.translate( 0, y );
		// paint content:
		paintScreen( g);
		
		g.translate( 0, -y );
		// allow painting outside of the screen again:
		//#ifdef tmp.menuFullScreen
		 	g.setClip(0, 0, this.screenWidth, this.fullScreenHeight );
		//#else
		 	g.setClip(0, 0, this.screenWidth, this.screenHeight );
		//#endif
		
		// paint border:
		if (this.border != null) {
			this.border.paint(0, 0, this.screenWidth, this.screenHeight, g);
		}
		
		// paint menu in full-screen mode:
		//#ifdef tmp.menuFullScreen
			if (this.menuOpened) {
				int menuHeight = this.menuContainer.getItemHeight(this.menuMaxWidth, this.menuMaxWidth);
				y = this.screenHeight - menuHeight;
				if (y < this.titleHeight) {
					y = this.titleHeight; 
					this.menuContainer.setVerticalDimensions(y, this.screenHeight);
				}
				this.menuContainer.paint(0, y, 0, this.menuMaxWidth, g);
			} 
			// clear menu-bar:
			if (this.menuBarColor != Item.TRANSPARENT) {
				g.setColor( this.menuBarColor );
				g.fillRect(0, this.screenHeight, this.screenWidth,  this.menuBarHeight );
			}
			if (this.menuContainer.size() > 0) {
				String menuText = null;
				if (this.menuOpened) {
					//TODO rob internationalise cmd.selectMenu
					menuText = "Select";
				} else {
					if (this.menuSingleCommand != null) {
						menuText = this.menuSingleCommand.getLabel();
					} else {
						//TODO rob internationalise cmd.openMenu
						menuText = "Options";				
					}
				}
				g.setColor( this.menuFontColor );
				g.setFont( this.menuFont );
				g.drawString(menuText, 2, this.screenHeight + 1, Graphics.TOP | Graphics.LEFT );
				if ( this.menuOpened ) {
					// draw select string:
					//TODO rob internationalise cmd.cancelMenu
					menuText = "Cancel";
					g.drawString(menuText, this.screenWidth - 2, this.screenHeight + 1, Graphics.TOP | Graphics.RIGHT );
				}
			}
		//#endif
		} catch (RuntimeException e) {
			//#mdebug error
			g.setColor( 0xFF0000 );
			g.fillRect( 0, 0, this.screenWidth, this.screenHeight );
			g.setColor( 0 );
			String msg = e.toString();
			g.drawString( msg, 10, 10, Graphics.TOP | Graphics.LEFT );
			Debug.debug( "unable to paint screen", e );
			//#enddebug
			throw e;
		}
		//#if polish.useFullScreen && polish.api.nokia-ui 
			this.lastPaintTime = System.currentTimeMillis();
			this.isInPaintMethod = false;
		//#endif
		//TODO rob paint the ticker
	}
	
	/**
	 * Paints the screen.
	 * 
	 * @param g the graphics on which the screen should be painted
	 */
	protected void paintScreen( Graphics g ) {
		int y = 0;
		if (this.isLayoutVCenter) {
			int containerHeight = this.container.getItemHeight( this.screenWidth, this.screenWidth);
			int availableHeight = this.screenHeight - this.titleHeight - containerHeight;
			if (availableHeight > 0) {
				y = (availableHeight / 2);
			}
		}
		this.container.paint( 0, y, 0, this.screenWidth, g );
	}
	
	/**
	 * Gets the title of the Screen. 
	 * Returns null if there is no title.
	 * 
	 * @return the title of this screen
	 */
	public String getTitle()
	{
		if (this.title == null) {
			return null;
		} else {
			return this.title.getText();
		}
	}

	/**
	 * Sets the title of the Screen. If null is given, removes the title. <p>
	 * 
	 * If the Screen is physically visible, the visible effect
	 * should take place no later than immediately
	 * after the callback or
	 * <A HREF="../../../javax/microedition/midlet/MIDlet.html#startApp()"><CODE>startApp</CODE></A>
	 * returns back to the implementation.
	 * 
	 * @param s - the new title, or null for no title
	 */
	public void setTitle( String s)
	{
		if (s != null) {
			//#style title, default
			this.title = new StringItem( null, s );
			// the Nokia 6600 has an amazing bug - when trying to refer the
			// field screenWidth, it returns 0 in setTitle(). Obviously this works
			// in other phones and in the simulator, but not on the Nokia 6600.
			// That's why hardcoded values are used here. 
			// The name of the field does not matter by the way. This is 
			// a very interesting behaviour and should be analysed
			// at some point...
			//#= this.titleHeight = this.title.getItemHeight(${polish.ScreenWidth}, ${polish.ScreenWidth});
		} else {
			this.title = null;
			this.titleHeight = 0;
		}
		if (this.container != null) {
			this.container.setVerticalDimensions( 0,  this.screenHeight - this.titleHeight );
		}
		if (isShown()) {
			repaint();
		}
	}

	/**
	 * Set a ticker for use with this Screen, replacing any previous ticker.
	 * If null, removes the ticker object
	 * from this screen. The same ticker is may be shared by several Screen
	 * objects within an application. This is done by calling setTicker() on
	 * different screens with the same Ticker object.
	 * If the Screen is physically visible, the visible effect
	 * should take place no later than immediately
	 * after the callback or
	 * <CODE>startApp</CODE>
	 * returns back to the implementation.
	 * 
	 * @param ticker - the ticker object used on this screen
	 */
	public void setTicker( Ticker ticker)
	{
		this.ticker = ticker;
		if (isShown()) {
			repaint();
		}
	}

	/**
	 * Gets the ticker used by this Screen.
	 * 
	 * @return ticker object used, or null if no ticker is present
	 */
	public javax.microedition.lcdui.Ticker getTicker()
	{
		//TODO rob adjust calls to getTicker accordingly
		return this.ticker;
	}
		
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#keyPressed(int)
	 */
	protected final void keyPressed(int keyCode) {
		//#ifdef polish.debugVerbose
			try {
		//#endif
		int gameAction = getGameAction(keyCode);
		//#ifdef tmp.menuFullScreen
			if (keyCode == FullCanvas.KEY_SOFTKEY1) {
				if ( this.menuSingleCommand != null) {
					callCommandListener( this.menuSingleCommand );
					return;
				} else {
					if (!this.menuOpened) {
						this.menuOpened = true;
						repaint();
						return;
					} else {
						gameAction = Canvas.FIRE;
					}
				}
			}
			if (this.menuOpened) {
				if (keyCode == FullCanvas.KEY_SOFTKEY2 ) {
					this.menuOpened = false;
				} else  if ( gameAction == Canvas.FIRE ) {
					int focusedIndex = this.menuContainer.getFocusedIndex();
					Command cmd = (Command) this.menuCommands.get( focusedIndex );
					this.menuOpened = false;
					callCommandListener( cmd );
				} else { 
					this.menuContainer.handleKeyPressed(keyCode, gameAction);
				}
				repaint();
				return;
			}
		//#endif
		boolean processed = handleKeyPressed(keyCode, gameAction);
		if (!processed) {
			//TODO Screen could try to switch to the last screen when Canvas.LEFT 
			// or Canvas.UP has been pressed. 
			// It could use the StyleSheet.currentScreen variable
			// for this purpose
			//#debug
			Debug.debug("unable to handle key [" + keyCode + "].");
		}
		if (processed) {
			//#if polish.useFullScreen && polish.api.nokia-ui
				requestRepaint();
			//#else
				repaint();
			//#endif
		}
		//#ifdef polish.debugVerbose
			} catch (Exception e) {
				Debug.debug("keyPressed() threw an exception", e );
				//#ifdef polish.useDebugGui
					// set the current screen to the debug-screen:
					StyleSheet.display.setCurrent( Debug.getLogForm(true, this) );
				//#endif
			}
		//#endif
	}
		
	//#ifdef polish.useDynamicStyles	
	/**
	 * Retrieves the CSS selector for this item.
	 * The CSS selector is used for the dynamic assignment of styles -
	 * that is the styles are assigned by the usage of the item and
	 * not by a predefined style-name.
	 * With the #style preprocessing command styles are set fix, this method
	 * yields in a faster GUI and is recommended. When in a style-sheet
	 * dynamic styles are used, e.g. "form>p", than the selector of the
	 * item is needed.
	 * This abstract method needs only be implemented, when dynamic styles
	 * are used: #ifdef polish.useDynamicStyles
	 * 
	 * @return the name of the appropriate CSS Selector for this screen.
	 */
	protected abstract String createCssSelector();	
	//#endif
	
	/**
	 * Retrieves all root-items of this screen.
	 * The root items are those in first hierarchy, in a Form this is 
	 * a Container for example.
	 * The default implementation does return an empty array, since apart
	 * from the container no additional items are used.
	 * Subclasses which use more root items than the container needs
	 * to override this method.
	 * 
	 * @return the root items an array, the array can be empty but not null.
	 */
	protected Item[] getRootItems() {
		return new Item[0];
	}
	
	/**
	 * Handles the key-pressed event.
	 * Please note, that implementation should first try to handle the
	 * given key-code, before the game-action is processed.
	 * 
	 * @param keyCode the code of the pressed key, e.g. Canvas.KEY_NUM2
	 * @param gameAction the corresponding game-action, e.g. Canvas.UP
	 * @return
	 */
	protected boolean handleKeyPressed( int keyCode, int gameAction ) {
		return this.container.handleKeyPressed(keyCode, gameAction);
	}
	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Displayable#setCommandListener(javax.microedition.lcdui.CommandListener)
	 */
	public void setCommandListener(CommandListener listener) {
		//#ifndef tmp.menuFullScreen
		super.setCommandListener(listener);
		//#endif
		this.cmdListener = listener;
	}
	
	//#ifdef tmp.menuFullScreen
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Displayable#addCommand(javax.microedition.lcdui.Command)
	 */
	public synchronized void addCommand(Command cmd) {
		if (this.menuCommands == null) {
			this.menuCommands = new ArrayList( 6, 50 );
			//#style menu, default
			 this.menuContainer = new Container( true );
		}
		//#style menuitem, menu, default
		StringItem menuItem = new StringItem( null, cmd.getLabel(), Item.HYPERLINK );
		this.menuContainer.add( menuItem );
		if (this.menuContainer.size() == 1) {
			this.menuSingleCommand = cmd;
		} else {
			this.menuSingleCommand = null;
		}
		this.menuCommands.add( cmd );
		if (isShown()) {
			repaint();
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Displayable#removeCommand(javax.microedition.lcdui.Command)
	 */
	public void removeCommand(Command cmd) {
		this.menuCommands.remove( cmd );
		if (this.menuSingleCommand == cmd ) {
			this.menuSingleCommand = null;
		}
		if (isShown()) {
			repaint();
		}
	}
	//#endif
	
	/**
	 * Calls the command listener with the specified command.
	 * 
	 * @param cmd the command wich should be issued to the listener
	 */
	protected void callCommandListener( Command cmd ) {
		if (this.cmdListener != null) {
			try {
				this.cmdListener.commandAction(cmd, this );
			} catch (Exception e) {
				//#debug error
				Debug.debug("unable to process command [" + cmd.getLabel() + "]: " + e.getMessage(), e );
			}
		}
	}
	
	/**
	 * Retrieves the available height for this screen.
	 * This is equivalent to the Canvas#getHeight() method.
	 * This method cannot be overriden for Nokia's FullScreen though.
	 * So this method is used insted.
	 * 
	 * @return the available height in pixels.
	 */
	public int getAvailableHeight() {
		return this.screenHeight - this.titleHeight;
	}
	 
	//#if polish.useFullScreen && polish.api.nokia-ui 
	/**
	 * Requests a repaint of the screen.
	 * This request is ignored when the paint method is currently called.
	 * J2ME Polish needs to implement this method because the
	 * Nokia FullCanvas implementation crashes sometimes when calling repaint() in
	 * the middlet of the paint method.
	 * This method is only available when the Nokia FullCanvas is used. 
	 */
	public void requestRepaint() {
		if (this.isInPaintMethod  || (System.currentTimeMillis() - this.lastPaintTime) < 100) {
			this.repaintRequested = true;
			return;
		}
		repaint();
	}
	//#endif
	
	
	//#if polish.debugVerbose && polish.useDebugGui
	public void commandAction( Command command, Displayable screen ) {
		StyleSheet.display.setCurrent( this );
	}
	//#endif

}
