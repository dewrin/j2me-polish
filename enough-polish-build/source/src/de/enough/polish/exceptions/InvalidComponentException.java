/*
 * Created on 16-Feb-2004 at 19:35:53.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.exceptions;

/**
 * <p>Is thrown when a defintion of a polish-component (device, vendor, group) contains errors.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        16-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class InvalidComponentException extends Exception {
	
	public InvalidComponentException() {
		super();
	}
	
	public InvalidComponentException(String message) {
		super(message);
	}
	
	public InvalidComponentException(Throwable cause) {
		super(cause);
	}
	
	public InvalidComponentException(String message, Throwable cause) {
		super(message, cause);
	}
}
