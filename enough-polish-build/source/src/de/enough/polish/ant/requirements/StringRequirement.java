/*
 * Created on 10-Feb-2004 at 22:50:02.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.requirements;

import de.enough.polish.Device;
import de.enough.polish.util.TextUtil;

/**
 * <p>Selects devices by a specific string within the given capability.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        10-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class StringRequirement extends Requirement {

	private StringMatcher matcher;
	
	/**
	 * Creates a new requirement for a specific string.
	 * @param value the string which needs to be defined within the capability
	 * @param propertyName the name of the capability
	 */
	public StringRequirement(String value, String propertyName) {
		this( value, propertyName, false );
	}
	
	/**
	 * Creates a new requirement for a specific string.
	 * @param value the string which needs to be defined within the capability
	 * @param propertyName the name of the capability
	 * @param or true when only one of the given elements needs to be found,
	 * 			 otherwiese all elements need to match.
	 */
	public StringRequirement(String value, String propertyName, boolean or ) {
		super(value, propertyName);
		String[] apis = TextUtil.split( value, ',');
		this.matcher = new StringMatcher( apis, or );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ant.requirements.Requirement#isMet(de.enough.polish.build.Device, java.lang.String)
	 */
	protected boolean isMet(Device device, String property) {
		return this.matcher.matches( property );
	}
	
	
}
