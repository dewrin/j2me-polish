/*
 * Created on 15-Jan-2004 at 16:10:59.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish;


/**
 * <p>Represents a J2ME device.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        15-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class Device extends PolishComponent {
	
	boolean supportsPolish;
	String identifier;


	/**
	 * Creates a new device.
	 * 
	 * @param parent the manufacturer of this device.
	 */
	public Device(Vendor parent) {
		super("device", parent);
	}

	/**
	 * Determines whether this device supports the polish-framework.
	 * 
	 * @return true when this device supports polish.
	 */
	public boolean supportsPolish() {
		return this.supportsPolish;
	}

	/**
	 * Retrieves a specific property of this device.
	 * 
	 * @param name the name of the property. 
	 * @return the value of the property or null when the given property is not defined.
	 */
	public String getProperty(String name) {
		return (String) getVariables().get( name );
	}

	/**
	 * @return the identifier of this device in the form [vendor]/[modell], e.g. Nokia/6600
	 */
	public String getIdentifier() {
		return this.identifier;
	}

}
