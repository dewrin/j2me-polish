/*
 * Created on 20-Apr-2004 at 01:30:49.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.util;

import javax.microedition.lcdui.Font;


/**
 * <p>Provides some usefull String methods.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        20-Apr-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public final class TextUtil {
	
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
	
	/**
	 * Splits the given string so it fits on the specified lines.
	 * First of al it is splitted at the line-breaks ('\n'), subsequently the substrings
	 * are splitted when they do not fit on a single line.
	 *  
	 * @param value the string which should be splitted
	 * @param font the font which is used to display the font
	 * @param firstLineWidth the allowed width for the first line
	 * @param lineWidth the allowed width for all other lines
	 * @return the array containing the substrings
	 */
	public static String[] split( String value, Font font, int firstLineWidth, int lineWidth ) {
		boolean hasLineBreaks = (value.indexOf('\n') != -1);
		int completeWidth = font.stringWidth(value);
		if ( completeWidth <= firstLineWidth ) {
			// the given string fits on the first line:
			if (hasLineBreaks) {
				return split( value, '\n');
			} else {
				return new String[]{ value };
			}
		}
		// the given string does not fit on the first line:
		ArrayList lines = new ArrayList();
		if (!hasLineBreaks) {
			split( value, font, completeWidth, firstLineWidth, lineWidth, lines );
		} else {
			// now the string will be splitted at the line-breaks and
			// then each line is processed:
			char[] valueChars = value.toCharArray();
			int lastIndex = 0;
			for (int i = 0; i < valueChars.length; i++) {
				char c = valueChars[i];
				if (c == '\n') {
					String line = new String( valueChars, lastIndex, i - lastIndex );
					completeWidth = font.stringWidth(line);
					if (completeWidth <= firstLineWidth ) {
						lines.add( line);						
					} else {
						TextUtil.split(line, font, completeWidth, firstLineWidth, lineWidth, lines);
					}
					lastIndex = i + 1;
					// after the first line all line widths are the same:
					firstLineWidth = lineWidth;
				} // for each line
			} // for all chars
		}		
		return (String[]) lines.toArray( new String[ lines.size() ]);
	}
	
	/**
	 * Splits the given string so that the substrings fit into the the given line-widths.
	 * It is expected that the specified completeWidth > firstLineWidth.
	 * The resulting substrings will be added to the given ArrayList.
	 * When the complete string fits into the first line, it will be added
	 * to the list.
	 * When the string needs to be splited to fit on the lines, it is tried to
	 * split the string at a gab between words. When this is not possible, the
	 * given string will be splitted in the middle of the corresponding word. 
	 * 
	 * 
	 * @param value the string which should be splitted
	 * @param font the font which is used to display the font
	 * @param completeWidth the complete width of the given string for the specified font.
	 * @param firstLineWidth the allowed width for the first line
	 * @param lineWidth the allowed width for all other lines
	 * @param list the list to which the substrings will be added.
	 */
	public static void split( String value, Font font, 
			int completeWidth, int firstLineWidth, int lineWidth, 
			ArrayList list ) 
	{
		char[] valueChars = value.toCharArray();
		int widthPerChar = (completeWidth * 100) / valueChars.length;
		int startIndex = 0;
		while (true) {
			int index = (firstLineWidth * 100) / widthPerChar;
			int i = index + startIndex;
			if (i > valueChars.length) {
				// reached the end of the given string:
				list.add( new String( valueChars, startIndex, valueChars.length - startIndex ) );
				break;
			}
			// find the last word gap:
			while (valueChars[i] != ' ') {
				i--;
				if (i < startIndex ) {
					// unable to find a gap:
					i = index + startIndex;
					break;
				}
			}
			//TODO rob maybe check whether the found string really fits into the line
			list.add( new String( valueChars, startIndex, i - startIndex ) );
			if (valueChars[i] == ' ') {
				startIndex = i + 1;
			} else {				
				startIndex = i;
			}
			firstLineWidth = lineWidth;
		}
	}

}
