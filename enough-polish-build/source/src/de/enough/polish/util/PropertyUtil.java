/*
 * Created on 18-Jan-2003 at 20:58:08.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Helps to parse properties in the form of ${ property.name }.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        18-Jan-2003 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public final class PropertyUtil {

	static final Pattern PROPERTY_PATTERN = 
		Pattern.compile("\\$\\{\\s*(\\w|\\.|-)+\\s*\\}"); // == \$\{\s*(\w|\.|-)+\s*\}
	
	/**
	 * Inserts the property-values in a string with property-definitions.
	 * 
	 * @param input the string in which property definition might be included, e.g. "file=${source}/MyFile.java"
	 * @param properties the map in which all properties are defined
	 * @return the input with all properties replaced by their values.
	 * 			When a property is not defined
	 *             the full property-name is inserted instead (e.g. "${ property-name }").  
	 */
	public static String writeProperties( String input, Map properties ) {
		return writeProperties( input, properties, false );
	}

	/**
	 * Inserts the property-values in a string with property-definitions.
	 * 
	 * @param input the string in which property definition might be included, e.g. "file=${source}/MyFile.java"
	 * @param properties the map in which all properties are defined
	 * @param needsToBeDefined true when an IllegalArgumentException should be thrown when
	 *              no value for a property was found.
	 * @return the input with all properties replaced by their values.
	 * 			When a property is not defined (and needsToBeDefined is false),
	 *             the full property-name is inserted instead (e.g. "${ property-name }").  
	 * @throws IllegalArgumentException when a property-value was not found and needsToBeDefined is true.
	 */
	public static String writeProperties( String input, Map properties, boolean needsToBeDefined ) {
		Matcher matcher = PROPERTY_PATTERN.matcher( input );
		boolean propertyFound = matcher.find();
		if (!propertyFound) {
			return input;
		}
		StringBuffer buffer = new StringBuffer();
		int startPos = 0;
		while (propertyFound) {
			// append string til start of the pattern:
			buffer.append( input.substring( startPos, matcher.start() ) );
			startPos = matcher.end();
			// append property:
			String group = matcher.group(); // == ${ property.name }
			String property = group.substring( 2, group.length() -1 ).trim(); // == property.name
			Object value = properties.get( property );
			if (value == null) {
				if (needsToBeDefined) {
					throw new IllegalArgumentException("property " + group + " is not defined.");
				} else {
					value = group;
				}
			}
			buffer.append( value );
			// look for another property:
			propertyFound = matcher.find();
		}
		// append tail:
		buffer.append( input.substring( startPos ) );
		return buffer.toString();
	}
}
