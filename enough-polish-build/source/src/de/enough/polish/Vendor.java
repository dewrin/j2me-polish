/*
 * Created on 15-Jan-2004 at 16:04:52.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish;

import de.enough.polish.exceptions.InvalidComponentException;

import org.jdom.Element;


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

	private String name;

	/**
	 * Creates a new Vendor.
	 * 
	 * @param parent the project to which this vendor manufacturer belongs to.
	 * @param definition the XML definition of this vendor.
	 * @throws InvalidComponentException when the given definition contains errors
	 */
	public Vendor( Project parent, Element definition )
	throws InvalidComponentException
	{
		super("polish.vendor", parent );
		this.name = definition.getChildTextTrim( "name");
		if (this.name == null) {
			throw new InvalidComponentException("Every vendor needs to define the element <name> - please check your vendors.xml.");
		}
		// load all capabilities:
		loadCapabilities(definition, this.name, "vendors.xml");
	}

	/**
	 * Retrieves the name of this vendor.
	 * 
	 * @return The name of this vendor, e.g. "Nokia". 
	 */
	public String getName() {
		return this.name;
	}


}
