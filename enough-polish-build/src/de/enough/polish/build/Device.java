/*
 * Created on 15-Jan-2004 at 16:10:59.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.build;

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


	/**
	 * Creates a new device.
	 * 
	 * @param parent the manufacturer of this device.
	 */
	public Device(Vendor parent) {
		super("device", parent);
	}

}
