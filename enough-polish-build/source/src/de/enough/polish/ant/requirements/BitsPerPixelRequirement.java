/*
 * Created on 24-Jan-2004 at 17:41:39.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.requirements;

import de.enough.polish.Device;

/**
 * <p>Selects a device by its screen-colors.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        24-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class BitsPerPixelRequirement extends Requirement {
	

	private IntegerMatcher matcher;

	/**
	 * Creates a new bits per pixel-requirement.
	 * 
	 * @param value how many bits per pixel are needed
	 */
	public BitsPerPixelRequirement(String value ) {
		super(value, "BitsPerPixel");
		this.matcher = new IntegerMatcher( value );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ant.requirements.Requirement#isMet(de.enough.polish.build.Device, java.lang.String)
	 */
	protected boolean isMet(Device device, String property) {
		return this.matcher.matches( property );
	}

}
