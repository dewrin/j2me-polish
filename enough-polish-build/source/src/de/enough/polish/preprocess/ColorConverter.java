/*
 * Created on 01-Mar-2004 at 20:31:43.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.preprocess;

import de.enough.polish.util.TextUtil;

import org.apache.tools.ant.BuildException;

import java.util.*;
import java.util.HashMap;
import java.util.Set;

/**
 * <p>Translates colors.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        01-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class ColorConverter {
	
	/**
	 * Defines the standard VGA colors.
	 * Available colors are red, green, blue, lime, black, white, silver, gray,
	 * maroon, purple, fuchsia, olive, yellow, navy, teal and aqua. 
	 */
	public static final HashMap COLORS = new HashMap();
	static {
		COLORS.put("red",  	 	"0xFF0000");
		COLORS.put("lime",  	"0x00FF00");
		COLORS.put("blue",   	"0x0000FF");
		COLORS.put("black",  	"0x000000");
		COLORS.put("white",  	"0xFFFFFF");
		COLORS.put("silver",  	"0xC0C0C0");
		COLORS.put("gray",  	"0x808080");
		COLORS.put("maroon",  	"0x800000");
		COLORS.put("purple",  	"0x800080");
		COLORS.put("green",  	"0x008000");
		COLORS.put("fuchsia",  	"0xFF00FF");
		COLORS.put("olive",  	"0x808000");
		COLORS.put("yellow",  	"0xFFFF00");
		COLORS.put("navy",  	"0x000080");
		COLORS.put("teal",  	"0x008080");
		COLORS.put("aqua",  	"0x00FFFF");
	}
	
	private HashMap tempColors; 
	
	/**
	 * Creates a new colors parser.
	 */
	public ColorConverter() {
		this.tempColors = new HashMap();
	}
	
	/**
	 * Removes all found color definition from the internal cache.
	 */
	public void clear() {
		this.tempColors.clear();
	}
	
	/**
	 * Parses the given color definition and returns the appropriate hex-definition.
	 * 
	 * @param definition the value of the color, e.g. "black", "#00ff00", "rgb( 340, 0, 0)"
	 * @return the hexadecimal color-value, e.g. "0x000000" or
	 * 		   "Item.TRANSPARENT" when the definition equals "transparent".
	 */
	public String parseColor( String definition ) {
		if ("transparent".equals(definition)) {
			return "Item.TRANSPARENT";
		}
		
		// the definition could be a color which has been defined earlier:
		String value = (String) this.tempColors.get( definition );
		if (value != null) {
			return value;
		}
		
		// the definition could be a standard VGA color:
		value = (String) COLORS.get( definition );
		if (value != null) {
			return value;
		}
		
		// the definition could be a normal hexadecimal value.
		// In CSS hex-values start with '#':
		if (definition.startsWith("#")) {
			value = definition.substring( 1 );
			if (value.length() == 3) {
				// an allowed shortcut in CSS is to use only one character
				// for each color, when they are equal otherwise.
				// blue is e.g. "#00F"
				StringBuffer buffer = new StringBuffer(10);
				buffer.append("0x");
				char[] chars = value.toCharArray();
				for (int i = 0; i < chars.length; i++) {
					char c = chars[i];
					buffer.append( c ).append( c );
				}
				value = buffer.toString();
				// check value:
				try {
					Integer.decode(value);
				} catch (NumberFormatException e) {
					throw new BuildException("Invalid color definition in CSS: [" + definition + "] is not a valid hexadecimal value (" + e.getMessage() + ").");
				}
				return value;
			} // if value.length() == 3
			if (value.length() < 6 || value.length() > 8) {
				throw new BuildException("Invalid color definition in CSS: [" + definition + "] is not a valid hexadecimal value.");
			}
			value = "0x" + value;
			// check number:
			try {
				Long.decode(value);
			} catch (NumberFormatException e) {
				throw new BuildException("Invalid color definition in CSS: [" + definition + "] is not a valid hexadecimal value (" + e.getMessage() + ").");
			}
			return value;
		}
		// the color could be encoded as a decimal Red-Green-Blue value:
		if (definition.startsWith("rgb")) {
			value = definition.substring(3).trim();
			if ((!value.startsWith("(")) || (!value.endsWith(")")) ) {
				throw new BuildException("Invalid color definition in CSS: [" + definition + "] is not a valid RGB value. Allowed is [rgb(rrr,ggg,bbb)], e.g. [rgb(128, 255, 0)]." );
			}
			value = value.substring( 1, value.length() - 1 );
			String[] numbers = TextUtil.splitAndTrim( value, ',');
			if (numbers.length != 3) {
				throw new BuildException("Invalid color definition in CSS: [" + definition + "] is not a valid RGB value. Allowed is [rgb(rrr,ggg,bbb)], e.g. [rgb(128, 255, 0)]." );
			}
			return parseColors( numbers, definition );
		}
		if (definition.startsWith("argb")) {
			value = definition.substring(4).trim();
			if ((!value.startsWith("(")) || (!value.endsWith(")")) ) {
				throw new BuildException("Invalid color definition in CSS: [" + definition + "] is not a valid ARGB value. Allowed is [argb(aaa,rrr,ggg,bbb)], e.g. [rgb(128, 255, 0)]." );
			}
			value = value.substring( 1, value.length() - 1 );
			String[] numbers = TextUtil.splitAndTrim( value, ',');
			if (numbers.length != 4) {
				throw new BuildException("Invalid color definition in CSS: [" + definition + "] is not a valid ARGB value. Allowed is [argb(aaa,rrr,ggg,bbb)], e.g. [argb(128, 128, 255, 0)]." );
			}
			return parseColors( numbers, definition );
		}
		// this is an invalid color declaration:
		throw new BuildException("Invalid color definition in CSS: [" + definition + "] is not a valid value." );
	}
	
	/**
	 * Parses the given numbers and stores them into a hexadecimal string.
	 * 
	 * @param numbers String array with the numbers or percentage values
	 * @param definition the complete definition
	 * @return the parsed rgb or argb value as hex string
	 */
	private String parseColors(String[] numbers, String definition ) {
		StringBuffer buffer = new StringBuffer( numbers.length * 2 + 2 );
		buffer.append("0x");
		for (int i = 0; i < numbers.length; i++) {
			String numberStr = numbers[i];
			boolean isPercentage = (numberStr.charAt( numberStr.length() - 1) == '%');
			int number = -1;			
			try {
				if (isPercentage) {
					String doubleStr = numberStr.substring( 0, numberStr.length() - 1);
					double percentageValue = Double.parseDouble( doubleStr );
					number = (int) ((255D * percentageValue) / 100D);
				} else {
					number = Integer.parseInt( numberStr );
				}
			} catch (NumberFormatException e) {
				throw new BuildException("Invalid color definition in CSS: [" + definition + "] is not a valid RGB value. Allowed is [rgb(rrr,ggg,bbb)], e.g. [rgb(128, 255, 0)]. The value [" + numberStr + "] cannot be parsed: " + e.getMessage() );
			}
			if (number > 0xFF || number < 0 ) {
				throw new BuildException("Invalid color definition in CSS: [" + definition + "] is not a valid RGB value. Allowed is [rgb(rrr,ggg,bbb)], e.g. [rgb(128, 255, 0)]. The value [" + numberStr + "] is invalid." );
			}
			if ( number < 0x1F) {
				buffer.append( '0' );
			}
			String hexNumber = Integer.toHexString(number) ;
			buffer.append( hexNumber);
		}
		return buffer.toString();
	}

	/**
	 * Sets the temporary colors.
	 * 
	 * @param newColors all colors in a map.
	 * @throws BuildException when one of the given colors is invalid 
	 */
	public void setTemporaryColors( HashMap newColors ) {
		this.tempColors = new HashMap();
		Set keys = newColors.keySet();
		for (Iterator iter = keys.iterator(); iter.hasNext();) {
			String colorName = (String) iter.next();
			HashMap map = (HashMap) newColors.get( colorName );
			String color = (String) map.get(colorName);
			this.tempColors.put( colorName, parseColor( color ));
		}
	}
	
}
