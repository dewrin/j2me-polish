/*
 * Created on 24-Jan-2004 at 17:41:39.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.requirements;


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
public class BitsPerPixelRequirement extends IntRequirement {
	

	/**
	 * Creates a new bits per pixel-requirement.
	 * 
	 * @param value how many bits per pixel are needed
	 */
	public BitsPerPixelRequirement(String value ) {
		super(value, "BitsPerPixel");
	}

}
