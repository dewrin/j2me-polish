//#condition polish.usePolishGui
/*
 * Created on 04-Jan-2004 at 19:43:08.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ui;

import de.enough.polish.util.Debug;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;

import java.util.Hashtable;

/**
 * <p>Style defines the design of any widget.</p>
 * <p>This class is used by the widgets. If you only use the predefined
 *       widgets you do not need to work with this class.
 * </p>
 * @author Robert Virkus, robert@enough.de
 * <pre>
 * history
 *        04-Jan-2004 - rob creation
 * </pre>
 */
public class Style
//#if (polish.useBeforeStyle || polish.useAfterStyle) && polish.images.backgroundLoad
//#define tmp.imageConsumer
implements ImageConsumer
//#endif
{
	
	//#ifdef polish.useDynamicStyles
	/**
	 * The name of this style. The name is only accessible when
	 * the preprocessing-symbol polish.useDynamicStyles is defined.
	 */
	public String name;
	//#endif
	//#ifdef polish.useBeforeStyle
	//#ifdef tmp.imageConsumer
	private String beforeUrl;
	//#endif
	/**
	 * The width of the before-element. This value can only be
	 * accessed when the preprocessing-symbol polish.useBeforeStyle is defined.
	 */
	public int beforeWidth;
	/**
	 * The height of the before-element. This value can only be
	 * accessed when the preprocessing-symbol polish.useBeforeStyle is defined.
	 */
	public int beforeHeight;
	/**
	 * The before-element. This value can only be
	 * accessed when the preprocessing-symbol polish.useBeforeStyle is defined.
	 */
	public Image before;
	//#endif
	//#ifdef polish.useAfterStyle
	//#ifdef tmp.imageConsumer
	private String afterUrl;
	//#endif
	/**
	 * The width of the after-element. This value can only be
	 * accessed when the preprocessing-symbol polish.useAfterStyle is defined.
	 */
	public int afterWidth;
	/**
	 * The height of the after-element. This value can only be
	 * accessed when the preprocessing-symbol polish.useAfterStyle is defined.
	 */
	public int afterHeight;
	/**
	 * The after-element. This value can only be
	 * accessed when the preprocessing-symbol polish.useAfterStyle is defined.
	 */
	public Image after;
	//#endif
	public Background background;
	public Border border;
	public Font font;
	public int fontColor;
	public Font labelFont;
	public int labelFontColor;
	public int paddingLeft;
	public int paddingTop;
	public int paddingRight;
	public int paddingBottom;
	public int paddingVertical;
	public int paddingHorizontal;
	public int marginLeft;
	public int marginTop;
	public int marginRight;
	public int marginBottom;

	public int layout;
	
	
	private Hashtable properties;

	/**
	 * Creates a new Style.
	 * 
	 * @param marginLeft the margin in pixels to the next element on the left
	 * @param marginRight the margin in pixels to the next element on the right
	 * @param marginTop the margin in pixels to the next element on the top
	 * @param marginBottom the margin in pixels to the next element on the bottom
	 * @param paddingLeft the padding between the left border and content in pixels
	 * @param paddingRight the padding between the right border and content in pixels
	 * @param paddingTop the padding between the top border and content in pixels
	 * @param paddingBottom the padding between the bottom border and content in pixels
	 * @param paddingVertical the vertical padding between internal elements of an item 
	 * @param paddingHorizontal the horizontal padding between internal elements of an item
	 * @param layout the layout for this style, e.g. Item.LAYOUT_CENTER
	 * @param fontColor the color of the font
	 * @param font the content-font for this style
	 * @param labelFontColor the color for labels
	 * @param labelFont the font for labels
	 * @param background the background for this style
	 * @param border the border for this style
	 * @param beforeUrl the URL of the before element. This is inserted before items
	 *            with this style. 
	 * @param afterUrl the URL of the after element. This is inserted after items
	 *            with this style.
	 * @param name the name of this style. Is only used when dynamic styles are used.
	 * 			
	 */
	public Style( int marginLeft, int marginRight, int marginTop, int marginBottom,
			int paddingLeft, int paddingRight, int paddingTop, int paddingBottom, int paddingVertical, int paddingHorizontal,
			int layout,
			int fontColor, Font font, int labelFontColor, Font labelFont, 
			Background background, Border border 
			//#ifdef polish.useBeforeStyle
			, String beforeUrl
			//#endif
			//#ifdef polish.useAfterStyle
			, String afterUrl
			//#endif
			//#ifdef polish.useDynamicStyles
			, String name
			//#endif
			) 
	{
		this.marginLeft = marginLeft;
		this.marginRight = marginRight;
		this.marginTop = marginTop;
		this.marginBottom = marginBottom;
		this.paddingLeft = paddingLeft;
		this.paddingRight = paddingRight;
		this.paddingTop = paddingTop;
		this.paddingBottom = paddingBottom;
		this.paddingVertical = paddingVertical;
		this.paddingHorizontal = paddingHorizontal;
		this.layout = layout;
		this.fontColor = fontColor;
		this.font = font;
		this.labelFontColor = labelFontColor;
		this.labelFont = labelFont;
		this.background = background;
		this.border = border;
		//#ifdef polish.useBeforeStyle
			if (beforeUrl != null) {
				try {
					//#ifdef tmp.imageConsumer
						this.beforeUrl = beforeUrl;
					//#endif
					this.before = StyleSheet.getImage(beforeUrl, this, true);
				} catch (Exception e) {
					//#debug error
					Debug.debug("unable to load before-image [" + beforeUrl + "]. ", e );
				}
				if (this.before != null) {
					this.beforeWidth = this.before.getWidth();
					this.beforeHeight = this.before.getHeight();
				}
			}
		//#endif
		//#ifdef polish.useAfterStyle
			if (afterUrl != null) {
				try {
					//#ifdef tmp.imageConsumer
						this.beforeUrl = beforeUrl;
					//#endif
					this.before = StyleSheet.getImage(beforeUrl, this, true);
				} catch (Exception e) {
					//#debug error
					Debug.debug("unable to load before-image [" + beforeUrl + "]. ", e );
				}
				if (this.before != null) {
					this.beforeWidth = this.after.getWidth();
					this.beforeHeight = this.after.getHeight();
				}
			}
		//#endif
		//#ifdef polish.useDynamicStyles
		this.name = name;
		//#endif
	}
	
	/**
	 * Defines a non-standard property of this style.
	 * For a CheckBox the check-image could be defined with:
	 * <code>style.addProperty("img-checked", "/cb_checked.png");</code>
	 * 
	 * @param propName the name of the property
	 * @param value the value of the property
	 */
	public void addProperty( String propName, Object value ) {
		if (this.properties ==null) {
			this.properties = new Hashtable();
		}
		this.properties.put( propName, value );
	}
	
	/**
	 * Retrieves a non-standard property of this style.
	 * 
	 * @param propName the name of the property
	 * @return the value of this property. If none has been defined, null will be returned.
	 */
	public Object getProperty( String propName ) {
		if (this.properties ==null) {
			return null;
		}
		return this.properties.get( propName );
	}

	//#ifdef tmp.imageConsumer
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ImageConsumer#setImage(java.lang.String, javax.microedition.lcdui.Image)
	 */
	public void setImage(String name, Image image) {
		//#ifdef polish.useBeforeStyle
		if (name.equals(this.beforeUrl)) {
			this.before = image;
			this.beforeHeight = image.getHeight();
			this.beforeWidth = image.getWidth();
		}
		//#endif
		//#ifdef polish.useAfterStyle
		if (name.equals(this.afterUrl)) {
			this.after = image;
			this.afterHeight = image.getHeight();
			this.afterWidth = image.getWidth();
		}
		//#endif
	}
	//#endif

}
