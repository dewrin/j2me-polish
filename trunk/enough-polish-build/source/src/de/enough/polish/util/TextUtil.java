/*
 * Created on 24-Nov-2003 at 14:38:43
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.util;

import java.util.ArrayList;

/**
 * <p>Provides some usefull String methods.</p>
 * <p></p>
 * <p>copyright enough software 2003, 2004</p>
 * <pre>
 *    history
 *       14-Jan-2004 (rob) added split-method  
 *       24-Nov-2003 (rob) creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public final class TextUtil {

	/**
	 * Replaces all search-strings in the given input.
	 *  
	 * @param input the original string
	 * @param search the string which should be replaced in the input
	 * @param replacement the replacement for the search-string
	 * @return the input string in which all search-strings are replaced with the replacement-string.
	 * @throws IllegalArgumentException when on of the parameters is null.
	 */
	public static final String replace(String input, String search, String replacement) {
		if (input == null || search == null || replacement == null) {
			throw new IllegalArgumentException( "TextUtil.replace: given input parameters must not be null.");
		}
		int startPos = 0;
		int pos;
		int add = replacement.length() - search.length();
		int totalAdd = 0;
		int replaceLength = search.length();
		StringBuffer replace = new StringBuffer(input);
		while ((pos = input.indexOf(search, startPos)) != -1) {
			replace.replace(pos + totalAdd, pos + totalAdd + replaceLength, replacement);
			totalAdd += add;
			startPos = pos + 1;
		}
		return replace.toString();
	}

	/**
	 * Replaces the first search-string in the given input.
	 *  
	 * @param input the original string
	 * @param search the string which should be replaced in the input
	 * @param replacement the replacement for the search-string
	 * @return the input string in which all search-strings are replaced with the replacement-string.
	 * @throws IllegalArgumentException when on of the parameters is null.
	 */
	public static String replaceFirst(String input, String search, String replacement) {
		if (input == null || search == null || replacement == null) {
			throw new IllegalArgumentException( "TextUtil.replace: given input parameters must not be null.");
		}
		int pos = input.indexOf( search ); 
		if (pos == -1){
			return input;
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append( input.substring(0, pos ))
			    .append( replacement )
				.append( input.substring( pos + search.length() ) );
		return buffer.toString();
	}
	
	/**
	 * Splits the given String around the matches defined by the given delimiter into an array.
	 * Example:
	 * <value>TextUtil.split("one;;two;;three", ";;")</value> results into the array
	 * <value>{"one", "two", "three"}</value>.
	 *
	 * @param value the String which should be split into an array
	 * @param delimiter the delimiter which marks the boundaries of the array 
	 * @return an array, when the delimiter was not found, the array will only have a single element.
	 * @see #split(String, char) for an even faster alternative
	 */
	public static String[] split(String value, String delimiter) {
		int lastIndex = 0;
		ArrayList strings = null;
		int currentIndex = 0;
		while ( (currentIndex = value.indexOf(delimiter, lastIndex ) ) != -1) {
			if (strings == null) {
				strings = new ArrayList();
			}
			strings.add( value.substring( lastIndex, currentIndex ) );
			lastIndex = currentIndex + delimiter.length();
		}
		if (strings == null) {
			return new String[]{ value };
		}
		// add tail:
		strings.add( value.substring( lastIndex ) );
		return (String[]) strings.toArray( new String[ strings.size() ] );
	}
	
	/**
	 * Splits the given String around the matches defined by the given delimiter into an array.
	 * Example:
	 * <value>TextUtil.split("one;two;three", ';')</value> results into the array
	 * <value>{"one", "two", "three"}</value>.
	 *
	 * @param value the String which should be split into an array
	 * @param delimiter the delimiter which marks the boundaries of the array 
	 * @return an array, when the delimiter was not found, the array will only have a single element.
	 */
	public static String[] split(String value, char delimiter) {
		char[] valueChars = value.toCharArray();
		int lastIndex = 0;
		ArrayList strings = null;
		for (int i = 0; i < valueChars.length; i++) {
			char c = valueChars[i];
			if (c == delimiter) {
				if (strings == null) {
					strings = new ArrayList();
				}
				strings.add( new String( valueChars, lastIndex, i - lastIndex ) );
				lastIndex = i + 1;
			}
		}
		if (strings == null) {
			return new String[]{ value };
		}
		// add tail:
		strings.add( new String( valueChars, lastIndex, valueChars.length - lastIndex ) );
		return (String[]) strings.toArray( new String[ strings.size() ] );
	}


}
