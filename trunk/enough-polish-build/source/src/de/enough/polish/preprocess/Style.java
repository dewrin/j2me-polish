/*
 * Created on 01-Mar-2004 at 15:17:38.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.preprocess;

import java.util.*;
import java.util.HashMap;
import java.util.Set;

/**
 * <p>Represents a CSS-style-definition.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        01-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class Style {
	
	public Background background;
	public Border border;
	public Font font;
	public int fontColor;
	public Font labelFont;
	public int labelFontColor;
	public int paddingLeft;
	public int paddingTop;
	public int paddingRight;
	public int paddingBottom;
	public int marginLeft;
	public int marginTop;
	public int marginRight;
	public int marginBottom;
	
	public int appearanceMode;
	public int layout;
	
	private HashMap properties;
	private HashMap groupsByName;
	private ArrayList groupNamesList;
	private String selector;
	private String parentName;

	
	public Style( String selector, String parent, CssBlock cssBlock ) {
		this.selector = selector;
		this.parentName = parent;
		this.properties = new HashMap();
		this.groupsByName = new HashMap();
		this.groupNamesList = new ArrayList();
		add( cssBlock );
	}

	/**
	 * Creates a new Style.
	 * 
	 * @param style the base style.
	 */
	public Style(Style style) {
		this.properties = new HashMap( style.properties );
		this.selector = style.selector;
		this.parentName = style.parentName;
		HashMap source = style.groupsByName;
		HashMap target = new HashMap();
		Set keys = source.keySet();
		for (Iterator iter = keys.iterator(); iter.hasNext();) {
			Object key = iter.next();
			HashMap original = (HashMap) source.get( key );
			HashMap copy = new HashMap( original );
			target.put(key, copy );
		}
		this.groupsByName = target;
	}
	
	public String getParentName() {
		return this.parentName;
	}
	
	/**
	 * Sets all style declarations of the parent.
	 * All styles implicitely extend the default-style. Theuy also
	 * can extend another style explicitely with the "extends" keyword.
	 * 
	 * @param parent the parent of this style.
	 */
	public void setParent( Style parent ) {
		//System.out.println("setting parent [" + parent.getSelector() + "] for style [" + this.selector + "].");
		// set the standard properties:
		Set set = parent.properties.keySet();
		for (Iterator iter = set.iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			if ( this.properties.get(key) == null) {
				this.properties.put( key, parent.properties.get(key));
			}
		}
		// set the group properties:
		String[] groupNames = parent.getGroupNames();
		for (int i = 0; i < groupNames.length; i++) {
			String groupName = groupNames[i];
			HashMap parentGroup = parent.getGroup(groupName);
			HashMap targetGroup = (HashMap) this.groupsByName.get( groupName );
			if (targetGroup == null) {
				//System.out.println("setting group [" + groupName + "].");
				// set the complete group when it is not defined:
				this.groupsByName.put( groupName, new HashMap( parentGroup ) ); 
				this.groupNamesList.add( groupName );
			} else {
				//System.out.println("setting only new group-properties of  [" + groupName + "].");
				// only set the properties which are not defined yet:
				set = parentGroup.keySet();
				for (Iterator iter = set.iterator(); iter.hasNext();) {
					String key = (String) iter.next();
					if ( targetGroup.get(key) == null) {
						//System.out.println("setting property [" + key + "] with value [" + parentGroup.get(key) + "]." );
						targetGroup.put( key, parentGroup.get(key));
					//} else {
					//	System.out.println("skipping property key [" + key + "] with value [" +targetGroup.get(key) +"]." );
					}
				}
			}
		}
	}

	/**
	 * @param cssBlock
	 */
	public void add(CssBlock cssBlock) {
		this.properties.putAll( cssBlock.getDeclarationsMap() );
		String[] groupNames = cssBlock.getGroupNames();
		for (int i = 0; i < groupNames.length; i++) {
			String groupName = groupNames[i];
			HashMap group = cssBlock.getGroupDeclarations( groupName );
			HashMap targetGroup = (HashMap) this.groupsByName.get( groupName );
			if (targetGroup == null) {
				this.groupsByName.put( groupName, group ); 
				this.groupNamesList.add( groupName );
			} else {
				targetGroup.putAll( group );
			}
		}
	}

	/**
	 * Adds another style to this one.
	 * Existing properties will be overwritten.
	 * 
	 * @param style the style
	 */
	public void add(Style style) {
		this.properties.putAll( style.properties );
		String[] groupNames = style.getGroupNames();
		for (int i = 0; i < groupNames.length; i++) {
			String groupName = groupNames[i];
			HashMap group = style.getGroup( groupName );
			HashMap targetGroup = (HashMap) this.groupsByName.get( groupName );
			if (targetGroup == null) {
				this.groupsByName.put( groupName, group ); 
				this.groupNamesList.add( groupName );
			} else {
				targetGroup.putAll( group );
			}
		}
	}

	
	/**
	 * @param groupName the name of the group
	 * @return the map containing all defined attributes of the group
	 */
	public HashMap getGroup(String groupName ) {
		return (HashMap) this.groupsByName.get( groupName );
	}
	
	/**
	 * Retrieves the names of all stored groups.
	 * 
	 * @return an array with the names of all included groups. 
	 */
	public String[] getGroupNames() {
		return (String[]) this.groupNamesList.toArray( new String[ this.groupNamesList.size() ] );
	}

	/**
	 * Retrieves the name of this style.
	 * 
	 * @return the name of this style.
	 */
	public String getSelector() {
		return this.selector;
	}

}
