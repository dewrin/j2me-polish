/*
 * Created on 11-Feb-2004 at 20:32:08.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.requirements;

import org.apache.tools.ant.BuildException;

import java.util.HashMap;

/**
 * <p>Compares memory value like "120+ kb" and "5mb".</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        11-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class MemoryMatcher 
implements Matcher 
{
	private static final HashMap UNITS = new HashMap();
	static {
		UNITS.put( "bytes", new Long(1));
		UNITS.put( "b", new Long(1));
		UNITS.put( "kb", new Long(1024));
		UNITS.put( "mb", new Long(1024 * 1024));
		UNITS.put( "gb", new Long(1024 * 1024 * 1024));
	}
	
	private long bytes;
	private boolean equalsOrGreater;
	
	/**
	 * Creates a new memory matcher.
	 * 
	 * @param value the needed memory, e.g. "120+ k"
	 */
	public MemoryMatcher( String value ) {
		this.bytes = getBytes( value );
		this.equalsOrGreater = (value.indexOf('+') != -1);
	}
	
	/**
	 * Gets the number of bytes of the given value.
	 *  
	 * @param value the memory, e.g. "1 kb"
	 * @return the number of bytes of the given memory, e.g. 1024 for "1 kb"
	 */
	public static final long getBytes( String value ) {
		value = value.trim().toLowerCase();		
		int splitPos = value.indexOf('+');
		if (splitPos == -1) {
			splitPos = value.indexOf(' ');
		}
		if (splitPos == -1) {
			splitPos = value.indexOf('\t');
		}
		double valueNumber;
		String valueString = null;
		String valueUnit;
		if (splitPos != -1) {
			valueString = value.substring(0, splitPos).trim();
			valueUnit = value.substring( splitPos + 1 ).trim();
		} else {
			// check number char by char:
			char[] valueChars = value.toCharArray();
			StringBuffer buffer = new StringBuffer( value.length() );
			int pos = 0;
			for (pos = 0; pos < valueChars.length; pos++) {
				char c = valueChars[pos];
				if ( Character.isDigit( c ) || (c == '.' ) ) {
					buffer.append( c );
				} else {
					break;
				}
			}
			valueString = buffer.toString();
			valueUnit = value.substring( pos ).trim();
		}
		if (valueString.indexOf('.') == -1) {
			valueNumber = Integer.parseInt( valueString );
		} else {
			valueNumber = Double.parseDouble( valueString );
		}
		Long unitMultiply = (Long) UNITS.get(valueUnit);
		if (unitMultiply == null) {
			throw new BuildException("Invalid memory-value [" + value +"] / unit [" + valueUnit + "] found: please specify a valid unit (kb, mb etc).");
		}
		return (long) (valueNumber * unitMultiply.longValue());
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ant.requirements.Matcher#matches(java.lang.String)
	 */
	public boolean matches(String deviceValue) {
		long deviceBytes = getBytes( deviceValue );
		if (this.equalsOrGreater) {
			return deviceBytes >= this.bytes;
		} else {
			return deviceBytes == this.bytes;
		}
	}

}
