/*
 * Created on 24-Jan-2004 at 00:23:02.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.requirements;

import de.enough.polish.Device;

/**
 * <p>A requirement which a supported device needs to satisfy.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        24-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public abstract class Requirement {
	
	private String value;
	protected String propertyName;
	

	/**
	 * Creates a new requirement.
	 * 
	 * @param value the value of this requirement.
	 * @param propertyName the property on which this requirement operates.
	 */
	public Requirement( String value, String propertyName ) {
		this.value = value;
		this.propertyName = propertyName;
	}
	
	/**
	 * Retrieves the requirement-property of the given device.
	 *  
	 * @param device the device for which the property should be retrieved
	 * @return the property-value or null when no such property is defined.
	 */
	public String getProperty( Device device ) {
		String property = device.getProperty( this.propertyName );
		if (property == null) {
			property = device.getProperty( "SoftwarePlatform." + this.propertyName );
		}
		if (property == null) {
			property = device.getProperty( "HardwarePlatform." + this.propertyName );
		}
		return property;
	}
	
	/**
	 * Checks if this requirement is met by the given device.
	 * 
	 * @param device the device which should be tested against this requirement.
	 * @return true when this requirement is satisfied by the given device.
	 */
	public boolean isMet( Device device ) {
		String property = getProperty(device);
		if (property == null) {
			return false;
		} else {
			return isMet( device, property );
		}
	}

	/**
	 * Checks if this requirement is met by the given device.
	 * 
	 * @param device the device which should be tested against this requirement.
	 * @param property the property-value of the given device. Property is never null.
	 * @return true when this requirement is satisfied by the given device.
	 */
	protected abstract boolean isMet( Device device, String property ); 
	
	
}
