/*
 * Created on 10-Mar-2004 at 11:14:38.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.preprocess;

import org.apache.tools.ant.BuildException;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Creates different borders from CSS declarations.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        10-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public abstract class BorderConverter extends Converter {

	protected static final String BORDERS_PACKAGE = "de.enough.polish.ui.borders.";
	
	protected String color;
	protected String width;
	
	protected String styleName;

	/**
	 * Creates a new instance 
	 */
	public BorderConverter() {
		super();
	}
	
	/**
	 * Adds the J2ME code for the given border.
	 * 
	 * @param codeList the list at which the generated could should be appended to
	 * @param border the map containing all border settings
	 * @param borderName the name of this border
	 * @param style the parent style
	 * @param styleSheet the style-sheet into which the style is embedded
	 * @param isStandalone true when a new public border-field should be created,
	 *        otherwise the border will be embedded in a style instantiation. 
	 * @throws BuildException when there are invalid CSS declarations in the given background
	 */
	public void addBorder( ArrayList codeList, 
			HashMap border, 
			String borderName,
			Style style, 
			StyleSheet styleSheet,
			boolean isStandalone)
	throws BuildException
	{
		this.styleName = borderName;
		// parse standard values:
		this.color = (String) border.get("color");
		if (this.color == null) {
			this.color = "0x000000"; // black is default border color
		} else {
			this.color = this.colorConverter.parseColor(this.color);
		}
		this.width = (String) border.get("width");
		if (this.width == null) {
			this.width = "1";	// 1 is the default border width
		} else {
			parseInt( "width", this.width );
		}
		if (isStandalone) {
			codeList.add( STANDALONE_MODIFIER + "Border " + borderName + "Border = ");
		}
		String newStatement = createNewStatement(border, style, styleSheet);
		if (isStandalone) {
			newStatement = "\t\t" + newStatement + ";";
		} else {
			newStatement = "\t\t" + newStatement; // border is the last argument of a style,
										  // so no comma needs to be appended
		}
		codeList.add( newStatement);
	}
	
	/**
	 * Creates the J2ME code for a new border based on the given properties.
	 * 
	 * @param border the map containing all border settings
	 * @param style the parent style
	 * @param styleSheet the style-sheet into which the style is embedded
	 * @return the new statement, e.g. "new de.enough.polish.ui.borders.SimpleBorder( 0x000000, 1 )"
	 * 		no semicolon or comma must appended.
	 * @throws BuildException when there are invalid CSS declarations in the given background
	 */
	protected abstract String createNewStatement( 
			HashMap border, 
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
		return parseInt( this.styleName, "border", name, value );
	}
	
}
