/*
 * Created on 16-Feb-2004 at 19:48:06.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish;

import de.enough.polish.exceptions.InvalidComponentException;

import org.jdom.*;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Manages all known groups of devices.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        16-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class DeviceGroupManager {
	
	private HashMap groups;
	
	/**
	 * Creates a new group manager.
	 * 
	 * @param groupsFile the file containing the groups definitions.
	 * 			Usally this is the groups.xml file in the current directory.
	 * @throws JDOMException when there are syntax errors in groups.xml
	 * @throws IOException when groups.xml could not be read
	 * @throws InvalidComponentException when a group definition has errors
	 */
	public DeviceGroupManager( File groupsFile ) 
	throws InvalidComponentException, JDOMException, IOException 
	{
		this.groups = new HashMap();
		loadGroups( groupsFile );
	}
	
	/**
	 * Loads all group definitions.
	 * 
	 * @param groupsFile the file containing the groups definitions.
	 * 			Usally this is the groups.xml file in the current directory.
	 * @throws JDOMException when there are syntax errors in groups.xml
	 * @throws IOException when groups.xml could not be read
	 * @throws InvalidComponentException when a group definition has errors
	 */
	private void loadGroups(File groupsFile) 
	throws InvalidComponentException, JDOMException, IOException 
	{
		SAXBuilder builder = new SAXBuilder( false );
		Document document = builder.build( groupsFile );
		List xmlList = document.getRootElement().getChildren();
		for (Iterator iter = xmlList.iterator(); iter.hasNext();) {
			Element deviceElement = (Element) iter.next();
			DeviceGroup group = new DeviceGroup( deviceElement );
			this.groups.put( group.getName(), group );
		}
		DeviceGroup[] groupArray = (DeviceGroup[]) this.groups.values().toArray( new DeviceGroup[ this.groups.size() ] );
		for (int i = 0; i < groupArray.length; i++) {
			DeviceGroup group = groupArray[i];
			String parentName = group.getParentName();
			if (parentName != null) {
				DeviceGroup parent = getGroup( parentName );
				if (parent == null) {
					throw new InvalidComponentException("The group [" + group.getName() + "] has the non-existing parent [" + parentName + "]. Check your [groups.xml]");
				}
				group.addComponent( parent );
			}
		}
	}

	/**
	 * Retrieves the group for the given name.
	 * 
	 * @param name The name of the group, e.g. "Series60"
	 * @return The found group or null when it has not been defined.
	 */
	public DeviceGroup getGroup( String name ) {
		return (DeviceGroup) this.groups.get( name );
	}
}
