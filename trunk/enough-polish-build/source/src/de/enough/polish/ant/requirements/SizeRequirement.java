/*
 * Created on 10-Feb-2004 at 22:38:46.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.requirements;

import de.enough.polish.Device;
import de.enough.polish.util.TextUtil;

import org.apache.tools.ant.BuildException;

/**
 * <p>Selects devices by the size of specific features.</p>
 * <p>The size-based capability needs to be defined as "11x22" etc.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        10-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class SizeRequirement extends Requirement {

	protected IntegerMatcher widthMatcher;
	protected IntegerMatcher  heightMatcher;
	
	/**
	 * Creates a new Size requirement.
	 * 
	 * @param value the needed size 
	 * @param propertyName the name of the capability
	 */
	public SizeRequirement(String value, String propertyName) {
		super(value, propertyName);
		String[] values = TextUtil.split( value, 'x');
		if (values.length != 2) {
			throw new BuildException( "The value of the requirement [" + this.propertyName + "] is not valid, it needs to be in the form \"[width] x [height]\".");
		}
		this.widthMatcher = new IntegerMatcher( values[0] );
		this.heightMatcher = new IntegerMatcher( values[1] );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ant.Requirement#isMet(de.enough.polish.build.Device, java.lang.String)
	 */
	protected boolean isMet(Device device, String property) {
		String[] parts = TextUtil.split( property, 'x' );
		if (parts.length != 2) {
			throw new BuildException("The property [" + this.propertyName + "] of the device [" + device.getIdentifier() + "] is not valid. It meeds to be in the form \"[width] x [height]\".");
		}
		return (  this.widthMatcher.matches( parts[0] )
				&& this.heightMatcher.matches( parts[1] ) );
	}

}
