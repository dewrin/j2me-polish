/*
 * Created on 24-Jan-2004 at 11:52:41.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.requirements;


/**
 * <p>Provides help for comparing integers, e.g. 150 with "40+"</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        24-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class IntegerMatcher 
implements Matcher 
{

	protected int number;
	protected boolean equalsOrGreater;

	/**
	 * Creates a new integer matcher.
	 * 
	 * @param value the requirement, e.g. "20" or "20+"
	 * @throws NumberFormatException when no integer number is given, e.g. "j5+"
	 */
	public IntegerMatcher( String value ) {
		value = value.trim();
		if (value.endsWith("+")) {
			this.equalsOrGreater = true;
			value = value.substring(0, value.length() -1).trim();
		}
		this.number = Integer.parseInt( value );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ant.Matcher#matches(java.lang.String)
	 */
	public boolean matches(String deviceValue) {
		int value = Integer.parseInt( deviceValue );
		if (this.equalsOrGreater) {
			return ( value >= this.number );
		} else {
			return (value == this.number );
		}
	}

}
