/*
 * Created on 10-Mar-2004 at 11:15:57.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.preprocess;

import org.apache.tools.ant.BuildException;

/**
 * <p>Base class for several Creator classes.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        10-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class Converter {
	protected final static String STANDALONE_MODIFIER = "\tpublic final static "; 
	protected ColorConverter colorConverter;


	/**
	 * Creates a new instance. 
	 */
	public Converter() {
		super();
	}
	
	/**
	 * Sets the color converter.
	 * 
	 * @param colorConverter the initialised color converter
	 */
	public void setColorConverter( ColorConverter colorConverter ) {
		this.colorConverter = colorConverter;
	}

	/**
	 * Parses the given integer.
	 * 
	 * @param styleName the style name
	 * @param groupName the name of the group
	 * @param name the name of the field
	 * @param value the int value as a String
	 * @return the int value.
	 * @throws BuildException when the value could not be parsed.
	 */
	public static final int parseInt(String styleName, String groupName, String name, String value) {
		try {
			return Integer.parseInt( value );
		} catch (NumberFormatException e) {
			throw new BuildException("Unable to parse the field [" + groupName + "-" + name + "] with the value [" + value + "] from the style [" + styleName + "]: " + e.getMessage(), e );
		}
	}
	
	/**
	 * Parses the given number which can be a percentage value.
	 * If the number is a percentage value, 
	 *  
	 * @param styleName the style name
	 * @param groupName the name of the group
	 * @param name the name of the field
	 * @param value the int or percentage value as a String. A percentage value needs
	 *          to end with a "%" character.
	 * @param relativeValue the number representing 100% if a percentage value is given.
	 * @return the resulting int value
	 * @throws BuildException when the value could not be parsed or when a percentage value
	 * 			is given and the given relativeValue is -1.
	 */
	public int parseInt(String styleName, String groupName, String name, String value, int relativeValue) {
		int lastCharPos = value.length() - 1;
		if (value.charAt( lastCharPos ) == '%') {
			if (relativeValue == -1) {
				throw new BuildException("Unable to parse the field [" + groupName + "-" + name + "] with the value [" + value + "] from the style [" + styleName + "]: No relative value is known for the current device, e.g. no ScreenSize is defined.");
			}
			value = value.substring( 0, lastCharPos );
			int percentageValue = parseInt(styleName, groupName, name, value);
			int realValue = (percentageValue * relativeValue) / 100;
			return realValue;
		} else {
			return parseInt(styleName, groupName, name, value);
		}
	}
	
	/**
	 * Extracts the correct url from the given resource URL.
	 * 
	 * @param url the URL of the resource, e.g. "url( myPic.png )"
	 * @return the clean J2ME url, e.g. "/myPic.png"
	 * @throws NullPointerException when the url is null
	 */
	public static final String getUrl(String url) {
		if ( url.startsWith("url") ) {
			int startPos = url.indexOf('(');
			int endPos = url.lastIndexOf(')');
			if (startPos != -1 && endPos != -1) {
				url = url.substring( startPos + 1, endPos ).trim();
			}
		}
		if ( url.charAt(0) != '/' ) {
			url = "/" + url;
		}
		return url;
	}
	
	
}
