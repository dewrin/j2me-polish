/*
 * Created on 24-Jan-2004 at 17:58:55.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.requirements;

/**
 * <p>Matches several string-values.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        24-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class StringMatcher implements Matcher {

	private String[] elements;
	private boolean or;

	/**
	 * Creates a new string matcher.
	 * 
	 * @param element the element which needs to be found in the device value.
	 */
	public StringMatcher(String element ) {
		this( new String[]{ element }, true );
	}
	
	/**
	 * Creates a new string matcher.
	 * 
	 * @param elements array of elements which needs to be found in the device value.
	 * @param or true when only one of the given elements needs to be found,
	 * 			 otherwiese all elements need to match.
	 */
	public StringMatcher(String[] elements, boolean or ) {
		for (int i = 0; i < elements.length; i++) {
			elements[i] = elements[i].trim().toLowerCase();
		}
		this.elements = elements;
		this.or = or;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ant.requirements.Matcher#matches(java.lang.String)
	 */
	public boolean matches(String deviceValue) {
		deviceValue = deviceValue.toLowerCase();
		boolean found = false;
		for (int i = 0; i < this.elements.length; i++) {
			String element = this.elements[i];
			if (deviceValue.indexOf( element ) != -1) {
				if (this.or) {
					return true;
				} else {
					found = true;
				}
			} else if (! this.or) {
				return false;
			}
		}
		return found;
	}

}
