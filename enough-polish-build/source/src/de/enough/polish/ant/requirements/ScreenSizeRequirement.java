/*
 * Created on 24-Jan-2004 at 00:34:05.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.requirements;


/**
 * <p>Selects a device by its screen-size.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        24-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class ScreenSizeRequirement extends SizeRequirement {
	
	/**
	 * Creates a new screen requirement
	 *  
	 * @param value the value of this requirement.
	 */
	public ScreenSizeRequirement( String value ) {
		super(value, "ScreenSize");
	}

}
