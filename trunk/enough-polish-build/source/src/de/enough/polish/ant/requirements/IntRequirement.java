/*
 * Created on 10-Feb-2004 at 22:45:41.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.requirements;

import de.enough.polish.Device;

/**
 * <p>Selects devices by matching simple numbers, e.g. "4" matches "2+".</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        10-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class IntRequirement extends Requirement {

	private IntegerMatcher matcher;
	
	/**
	 * @param value
	 * @param propertyName
	 */
	public IntRequirement(String value, String propertyName) {
		super(value, propertyName);
		this.matcher = new IntegerMatcher( value );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ant.requirements.Requirement#isMet(de.enough.polish.build.Device, java.lang.String)
	 */
	protected boolean isMet(Device device, String property) {
		return this.matcher.matches( property );
	}

}
