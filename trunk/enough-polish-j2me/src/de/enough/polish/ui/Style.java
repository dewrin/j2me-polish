/*
 * Created on 04-Jan-2004 at 19:43:08.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ui;

import javax.microedition.lcdui.Font;

import java.util.Hashtable;

/**
 * <p>Style defines the design of any widget.</p>
 * <p>This class is used by the widgets. If you only use the predefined
 *       widgets you do not need to work with this class.
 * </p>
 *
 * @author Robert Virkus, robert@enough.de
 * <pre>
 * history
 *        04-Jan-2004 - rob creation
 * </pre>
 */
public class Style {
	
	public Background background;
	public Border border;
	public Font font;
	private Hashtable properties;

	/**
	 * Creates a new Style.
	 * 
	 * @param background the background for this style
	 * @param border the border for this style
	 * @param font the font for this style
	 */
	public Style( Background background, Border border, Font font ) {
		this.background = background;
		this.border = border;
		this.font = font;
	}
	
	/**
	 * Defines a non-standard property of this style.
	 * For a CheckBox the check-image could be defined with:
	 * <code>style.addProperty("img-checked", "/cb_checked.png");</code>
	 * 
	 * @param name the name of the property
	 * @param value the value of the property
	 */
	public void addProperty( String name, Object value ) {
		if (this.properties ==null) {
			this.properties = new Hashtable();
		}
		this.properties.put( name, value );
	}
	
	/**
	 * Retrieves a non-standard property of this style.
	 * 
	 * @param name the name of the property
	 * @return the value of this property. If none has been defined, null will be returned.
	 */
	public Object getProperty( String name ) {
		if (this.properties ==null) {
			return null;
		}
		return this.properties.get( name );
	}

}
