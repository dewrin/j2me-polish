/*
 * Created on 24-Jan-2004 at 00:34:05.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.requirements;

import de.enough.polish.Device;
import de.enough.polish.util.TextUtil;

import org.apache.tools.ant.BuildException;

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
public class ScreenSizeRequirement extends Requirement {
	
	protected IntegerMatcher widthMatcher;
	protected IntegerMatcher  heightMatcher;

	/**
	 * Creates a new screen requirement
	 *  
	 * @param value the value of this requirement.
	 */
	public ScreenSizeRequirement( String value ) {
		super(value, "ScreenSize");
		init(value);
	}

	/**
	 * Creates a new screen requirement
	 *  
	 * @param value the value of this requirement.
	 * @param property the property on which this requirement operates, e.g. "CanvasSize".
	 */
	public ScreenSizeRequirement( String value, String property ) {
		super( value, property );
		init( value );
	}

	/**
	 * Inits this requirement.
	 * @param value the value of this requirement.
	 */
	private void init(String value) {
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
