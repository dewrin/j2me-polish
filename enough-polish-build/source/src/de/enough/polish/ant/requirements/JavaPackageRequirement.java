/*
 * Created on 24-Jan-2004 at 18:05:57.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.requirements;

import de.enough.polish.Device;
import de.enough.polish.util.TextUtil;

/**
 * <p>Selects a device by its support of specific software-APIs.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        24-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class JavaPackageRequirement extends Requirement {

	private StringMatcher matcher;

	/**
	 * Creates a new java package requirement.
	 * 
	 * @param value the needed apis seperated by comma.
	 */
	public JavaPackageRequirement(String value) {
		super(value, "JavaPackage");
		String[] apis = TextUtil.split( value, ',');
		this.matcher = new StringMatcher( apis, false );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ant.requirements.Requirement#isMet(de.enough.polish.build.Device, java.lang.String)
	 */
	protected boolean isMet(Device device, String property) {
		return this.matcher.matches( property );
	}

}
