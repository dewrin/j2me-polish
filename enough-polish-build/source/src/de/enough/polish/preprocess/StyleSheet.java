/*
 * Created on 16-Jan-2004 at 12:04:15.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.preprocess;

import java.util.HashMap;

/**
 * <p>Represents a StyleSheet for a specific application.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        16-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class StyleSheet {
	
	private HashMap definedStyles;

	/**
	 * Creates a new empty style-sheet 
	 */
	public StyleSheet() {
		this.definedStyles = new HashMap();
	}
	
	/**
	 * Adds a style to the list of used styles.
	 *  
	 * @param name the name of the style
	 */
	public void addStyle( String name ) {
		this.definedStyles.put( name, Boolean.TRUE );
	}
	
	/**
	 * Determines whether the specified style is used in the current project.
	 * 
	 * @param name the name of the style
	 * @return true when the style has been used somewhere in the code
	 */
	public boolean isDefined( String name ) {
		return (this.definedStyles.get( name ) != null);
	}
	

}
