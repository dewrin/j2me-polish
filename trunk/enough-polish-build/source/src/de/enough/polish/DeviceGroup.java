/*
 * Created on 15-Jan-2004 at 16:08:33.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish;

import de.enough.polish.exceptions.InvalidComponentException;

import org.jdom.Element;


/**
 * <p>Respresents a group to which a device belongs to.</p>
 * <p>A group can be independent of a specific manufacturer, 
 * and a device can belong to several groups.
 * Also a group can belong do other groups.
 * </p>
 * 
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        15-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class DeviceGroup extends PolishComponent {


	private String name;
	private String parentName;

	/**
	 * Creates a new device group.
	 * 
	 * @param definition The xml definition of this group.
	 * @throws InvalidComponentException when the given definition contains errors.
	 */
	public DeviceGroup( Element definition ) 
	throws InvalidComponentException 
	{
		super("polish.group", null );
		this.name = definition.getChildTextTrim( "name" );
		if (this.name == null) {
			throw new InvalidComponentException("A group needs to have the element <name> defined. Please check your [groups.xml] file.");
		}
		this.parentName  = definition.getChildTextTrim( "parent" );
		loadCapabilities(definition, this.name, "groups.xml");
	}
	
	/**
	 * @return Returns the name of this group.
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @return The name of the parent of this group.
	 */
	public String getParentName() {
		return this.parentName;
	}

}