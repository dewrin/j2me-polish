//#condition polish.usePolishGui
/*
 * Created on 12-Mar-2004 at 21:46:17.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
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
//#= extends ${polish.classes.fullscreen}
//#else
extends Canvas
//#endif
{
	
	protected StringItem title;
	protected Background background;
	protected Border border;
	protected Style style;
	protected int screenHeight;
	protected int screenWidth;
	protected Ticker ticker;
	protected String cssSelector;
	protected CommandListener cmdListener;
	protected Container container;
	//#if polish.useMenuFullScreen && polish.classes.fullscreen:defined
		//#define tmp.menuFullScreen
		private Command menuSingleCommand;
		//#style menu, default
		private Container menuContainer = new Container( true );
		private ArrayList menuCommands = new ArrayList( 6, 50 );
		private boolean menuOpened;
		private Font menuFont = Font.getFont( Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM );
		private int menuFontColor = 0;
		private int menuHeightClosed = this.menuFont.getHeight() + 2;
		private int menuMaxWidth = ( getWidth() * 2 ) / 3;
		private int menuBarColor = 0xFFFFFF;
	//#endif

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
		if (title != null) {
			//#style title, default
			this.title = new StringItem( null, title );
		}
		this.screenHeight = getHeight();
		this.screenWidth = getWidth();
		this.container = new Container( true );
		this.container.screen = this;
		setStyle( style );
	}

	/**
	 * Sets the style of this screen.
	 * 
	 * @param style the style
	 */
	public void setStyle(Style style) {
		this.style = style;
		if (style != null) {
			this.background = style.background;
			this.border = style.border;
			this.container.setStyle(style, true);
		} else {
			this.background = null;
			this.border = null;
		}
		//#ifdef tmp.menuFullScreen
		Style menuStyle = StyleSheet.getStyle("menu");
		if (menuStyle != null) {
		String colorStr = menuStyle.getProperty("menubar-color");
		if (colorStr != null) {
			this.menuBarColor = Integer.parseInt(colorStr);
		}
		colorStr = menuStyle.getProperty("menufont-color");
		if (colorStr != null) {
			this.menuFontColor = Integer.parseInt(colorStr);
		}
		}
	//#endif
	}
	
	public boolean animate() {
		//TODO rob animate ticker, container, border
		boolean animated = false;
		if (this.background != null) {
			animated = this.background.animate();
		}
		if (animated) {
			repaint();
			return true;
		} else {
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#paint(javax.microedition.lcdui.Graphics)
	 */
	protected void paint(Graphics g) {
		// paint background:
		if (this.background != null) {
			this.background.paint(0, 0, this.screenWidth, this.screenHeight, g);
		}
		int y = 0;
		// paint title:
		if (this.title != null) {
			this.title.paint(0, 0, 0, this.screenWidth, g);
			y = this.title.getItemHeight( this.screenWidth, this.screenWidth );
		}
		// paint content:
		paintScreen( 0, y, this.screenWidth, g);
		// paint border:
		if (this.border != null) {
			this.border.paint(0, 0, this.screenWidth, this.screenHeight, g);
		}
		//#ifdef tmp.menuFullScreen
			if (this.menuOpened) {
				int menuHeight = this.menuContainer.getItemHeight(this.menuMaxWidth, this.menuMaxWidth);
				int titleHeight = this.title.getItemHeight( this.screenWidth, this.screenWidth )
					+ 1; //TODO add paddingVertical?
				y = this.screenHeight - (menuHeight + this.menuHeightClosed + 1);
				if (y < titleHeight) {
					y = titleHeight; 
				}
				this.menuContainer.paint(0, y, 0, this.menuMaxWidth, g);
			} 
			if (this.menuContainer.size() > 0) {
				// clear menu-bar:
				g.setColor( this.menuBarColor );
				int yStart = this.screenHeight - this.menuHeightClosed;
				g.fillRect(0, yStart, this.screenWidth,  this.menuHeightClosed );
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
				g.drawString(menuText, 2, yStart + 1, Graphics.TOP | Graphics.LEFT );
				if ( this.menuOpened ) {
					// draw select string:
					//TODO rob internationalise cmd.cancelMenu
					menuText = "Cancel";
					g.drawString(menuText, this.screenWidth - 2, yStart + 1, Graphics.TOP | Graphics.RIGHT );
				}
			}
		//#endif
		//TODO rob paint the ticker
	}
	
	/**
	 * Paints the screen.
	 * 
	 * @param x the horizontal start position
	 * @param y the vertical start position
	 * @param rightBorder the vertical position of the right border of the screen.
	 * 		       No painting right of this position is allowed.
	 * @param g the graphics on which the screen should be painted
	 */
	protected void paintScreen( int x, int y, int rightBorder, Graphics g ) {
		this.container.paint( x, y, x, rightBorder, g );
	}
	
	/**
	 * Gets the title of the Screen. 
	 * Returns null if there is no title.
	 * 
	 * @return the title of this screen
	 */
	public String getTitle()
	{
		return this.title.getText();
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
		if (this.title == null && s != null) {
			//#style title
			this.title = new StringItem( null, s );
		} else if ( s != null ) {
			this.title.setText(s);
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
	protected void keyPressed(int keyCode) {
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
				int focussedIndex = this.menuContainer.getFocussedIndex();
				Command cmd = (Command) this.menuCommands.get( focussedIndex );
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
			System.out.println("unable to handle key [" + keyCode + "].");
		}
		if (processed) {
			repaint();
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#showNotify()
	 */
	protected void showNotify() {
		StyleSheet.currentScreen = this;
		if (StyleSheet.animationThread == null) {
			StyleSheet.animationThread = new AnimationThread();
			StyleSheet.animationThread.start();
		}
		// inform all root items that they belong to this screen:
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
	
	//#if tmp.menuFullScreen
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Displayable#addCommand(javax.microedition.lcdui.Command)
	 */
	public synchronized void addCommand(Command cmd) {
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
		// TODO enough implement removeCommand
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
}
