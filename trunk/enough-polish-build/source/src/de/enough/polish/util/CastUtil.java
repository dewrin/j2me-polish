/*
 * Created on 26-Feb-2004 at 10:02:20.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.util;

/**
 * <p>A helper class for casting values between different types.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        26-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public final class CastUtil {
	
	/**
	 * Determines whether the given object represents a boolean "true".
	 * 
	 * @param value The value
	 * @return True when the value is a Boolean representing true,
	 *         or when the String representation of the value
	 *         is either "true" or "yes".
	 *         False is returned in all other cases, e.g. when null is given.
	 */
	public static boolean getBoolean( Object value ) {
		if (value == null) {
			return false;
		}
		if (value instanceof Boolean) {
			return ((Boolean)value).booleanValue();
		}
		String valueStr = value.toString();
		return ( ("true".equals(valueStr))
			  || ("yes".equals(valueStr)) );
	}
	
	/**
	 * Retrieves the integer-value represented by the given object.
	 * 
	 * @param value the value which represents an integer
	 * @return the integer value
	 * @throws NullPointerException when the given value is null
	 * @throws NumberFormatException when the value does not represent an integer
	 */
	public static int getInt( Object value ) {
		if (value == null) {
			throw new NullPointerException("Unable to parse int-value [null].");
		}
		if (value instanceof Integer) {
			return ((Integer) value).intValue();
		}
		return Integer.parseInt(value.toString());
	}
	
}
