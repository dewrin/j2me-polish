/*
 * Created on 24-Jan-2004 at 18:17:46.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.requirements;

import de.enough.polish.Device;
import de.enough.polish.util.TextUtil;

/**
 * <p>Selects devices by their vendor.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        24-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class VendorRequirement extends Requirement {

	private StringMatcher matcher;

	/**
	 * Creates a new vendor requirement.
	 * 
	 * @param value the names of the allowed vendors seperated by commas
	 */
	public VendorRequirement(String value) {
		super(value, "Vendor");
		String[] vendors = TextUtil.split(value, ',');
		this.matcher = new StringMatcher( vendors, true ); 
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ant.requirements.Requirement#isMet(de.enough.polish.build.Device, java.lang.String)
	 */
	protected boolean isMet(Device device, String property) {
		return this.matcher.matches( property );
	}

}
