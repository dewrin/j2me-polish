/*
 * Created on 15-Jan-2004 at 16:04:52.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.build;

/**
 * <p>Represents a manufacturer of J2ME devices like Nokia, Sony-Ericsson, Motorola and so on.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        15-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class Vendor extends PolishComponent {

	/**
	 * Creates a new Vendor.
	 * 
	 * @param parent the project to which this definition of a manufacturer belongs to.
	 */
	public Vendor( Project parent ) {
		super("manufacturer", parent );
	}


}
