/*
 * Created on 04-Feb-2004 at 20:16:05.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.exceptions;

/**
 * <p>Is thrown when an invalid device definition is found.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        04-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class InvalidDeviceException extends Exception {

	public InvalidDeviceException() {
		super();
	}

	public InvalidDeviceException(String description) {
		super(description);
	}

	public InvalidDeviceException(Throwable cause) {
		super(cause);
	}

	public InvalidDeviceException(String description, Throwable cause) {
		super(description, cause);
	}

}
