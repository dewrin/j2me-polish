/*
 * Created on 09-Mar-2004 at 20:47:44.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.preprocess;

import org.apache.tools.ant.BuildException;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>The base class for all backgrounds.</p>
 * <p>The background creator is responsible for creating backgrounds
 *    for styles.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        09-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public abstract class BackgroundConverter extends Converter {

	protected static final String BACKGROUNDS_PACKAGE = "de.enough.polish.ui.backgrounds.";

	protected String color;
	protected String borderWidth;
	protected String borderColor;
	protected boolean hasBorder;
	
	protected String styleName;

	/**
	 * Creates a new empty background
	 */
	public BackgroundConverter() {
		super();
	}
		
	/**
	 * Adds the J2ME code for the given background.
	 * 
	 * @param codeList the list at which the generated could should be appended to
	 * @param background the map containing all background settings
	 * @param backgroundName the name of this background
	 * @param style the parent style if any
	 * @param styleSheet the style-sheet into which the style is embedded
	 * @param isStandalone true when a new public background-field should be created,
	 *        otherwise the background will be embedded in a style instantiation. 
	 * @throws BuildException when there are invalid CSS declarations in the given background
	 */
	public void addBackground( ArrayList codeList, 
			HashMap background,
			String backgroundName,
			Style style, 
			StyleSheet styleSheet,
			boolean isStandalone)
	throws BuildException
	{
		this.styleName = backgroundName;
		// check if no background at all should be used:
		String bg = (String) background.get("background");
		if (bg != null && "none".equals(bg) ) {
			if (isStandalone) {
				codeList.add( STANDALONE_MODIFIER + "Background " + backgroundName + "Background = null;\t// background:none was specified");
			} else {
				codeList.add( "\t\tnull,\t// background:none was specified");
			}
			return;
		}
		// parse standard values:
		this.color = (String) background.get("color");
		if (this.color == null) {
			this.color = "0xFFFFFF"; // white is default background color
		} else {
			this.color = this.colorConverter.parseColor(this.color);
		}
		this.borderWidth = (String) background.get("border-width");
		if (this.borderWidth != null) {
			// check if the border with is a correct value:
			parseInt( "border-width", this.borderWidth );
			this.hasBorder = true;
		}
		this.borderColor = (String) background.get("border-color");
		if (this.borderColor != null) {
			this.hasBorder = true;
			this.borderColor = this.colorConverter.parseColor( this.borderColor );
			if (this.borderWidth == null) {
				this.borderWidth = "1";
			}
		} else if (this.borderWidth != null) {
			this.borderColor = "0x000000"; // default border color is black
		}
		if (isStandalone) {
			codeList.add( STANDALONE_MODIFIER + "Background " + backgroundName + "Background = ");
		}
		String newStatement = createNewStatement( background, style, styleSheet );
		if (isStandalone) {
			newStatement = "\t\t" + newStatement + ";";
		} else {
			newStatement = "\t\t" + newStatement + ",";
		}
		codeList.add( newStatement);
	}
	
	/**
	 * Creates the statement for a new background based on the given properties.
	 * 
	 * @param background the map containing all background settings
	 * @param style the parent style
	 * @param styleSheet the style-sheet into which the style is embedded
	 * @return the new statement, e.g. "new de.enough.polish.ui.backgrounds.SimpleBackground( 0x000000 )"
	 * 		no semicolon or comma must appended.
	 * @throws BuildException when there are invalid CSS declarations in the given background
	 */
	protected abstract String createNewStatement( 
			HashMap background, 
			Style style, 
			StyleSheet styleSheet )
	throws BuildException;

	/**
	 * Parses the given integer.
	 * 
	 * @param name the name of the field
	 * @param value the int value as a String
	 * @return the int value.
	 * @throws BuildException when the value could not be parsed.
	 */
	public int parseInt( String name, String value) {
		return parseInt( this.styleName, "background", name, value );
	}
	
	
}
