/*
 * Created on 10-Mar-2004 at 11:15:57.
 *
 * Copyright (c) 2004 Robert Virkus / Enough Software
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
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.preprocess;

import org.apache.tools.ant.BuildException;

/**
 * <p>Base class for several Creator classes.</p>
 *
 * <p>copyright Enough Software 2004</p>
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
