/*
 * Created on 16-Jan-2004 at 12:04:15.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.preprocess;

import org.apache.tools.ant.BuildException;

import java.util.*;
import java.util.HashMap;
import java.util.Set;

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
	
	private static final HashMap KEYWORDS = new HashMap();
	static {
		KEYWORDS.put("font", Boolean.TRUE );
		KEYWORDS.put("background", Boolean.TRUE );
		KEYWORDS.put("border", Boolean.TRUE );
		KEYWORDS.put("extends", Boolean.TRUE );
	}
	
	private HashMap stylesByName;
	private ArrayList styles;
	private HashMap backgrounds;
	private HashMap borders;
	private HashMap fonts;
	private HashMap colors;
	private HashMap usedStyles;
	
	private boolean isInitialised;
	
	/**
	 * Creates a new empty style sheet
	 */
	public StyleSheet() {
		this.stylesByName = new HashMap();
		this.usedStyles = new HashMap();
		this.styles = new ArrayList();
		this.backgrounds = new HashMap();
		this.borders = new HashMap();
		this.fonts = new HashMap();
		this.colors = new HashMap();
	}
	
	/**
	 * Creates a new style sheet which is initialised with the given stylesheet.
	 * @param sheet the base style sheet
	 */
	public StyleSheet(StyleSheet sheet) {
		this();
		add( sheet );
	}
	

	/**
	 * Copies all styles from the parent style sheet.
	 * 
	 * @param parent the parent style sheet
	 */
	private void copyStyles( StyleSheet parent ) {
		HashMap source = parent.stylesByName;
		HashMap target = this.stylesByName;
		Set keys = source.keySet();
		for (Iterator iter = keys.iterator(); iter.hasNext();) {
			Object key = iter.next();
			Style original = (Style) source.get( key );
			Style existing = (Style) target.get( key );
			if (existing == null) {
				Style copy = new Style( original );
				target.put(key, copy );
				this.styles.add( copy );
			} else {
				existing.add( original );
			}
		}
	}

	/**
	 * Copies the contents of the given source HashMap to the target.
	 * 
	 * @param source the source map containing other HashMaps
	 * @param target the target 
	 */
	private void copyHashMap(HashMap source, HashMap target) {
		Set keys = source.keySet();
		for (Iterator iter = keys.iterator(); iter.hasNext();) {
			Object key = iter.next();
			HashMap original = (HashMap) source.get( key );
			HashMap existing = (HashMap) target.get( key );
			if (existing == null ) {
				HashMap copy = new HashMap( original );
				target.put(key, copy );
			} else {
				existing.putAll( original );
			}
		}
	}

	/**
	 * Retrieves the specified (usually unprocessed) style.
	 *   
	 * @param name the (not case-sensitive) name of the style
	 * @return the style.
	 */
	public Style getStyle( String name ) {
		name = name.toLowerCase();
		return (Style) this.stylesByName.get( name );
	}
	
	/**
	 * Determines whether the specified style is known.
	 * 
	 * @param name the (not case-sensitive) name of the style.
	 * @return true when the specified style is known.
	 */
	public boolean isDefined( String name ) {
		name = name.toLowerCase();
		return (this.stylesByName.get(name) != null);
	}
	
	/**
	 * Indicates that the given style is really used.
	 * 
	 * @param name the name of the style which is used.
	 */
	public void addUsedStyle( String name ) {
		this.usedStyles.put( name, Boolean.TRUE );
	}

	/**
	 * Removes all style-definitions
	public void clear() {
		this.isInitialised = false;
		this.styles.clear();
	}
	 */
	
	/**
	 * Adds a CSS block to this style sheet.
	 * A CSS block contains either a style or the colors, fonts,
	 * borders or backgrounds areas.
	 * 
	 * @param cssBlock the block containing CSS declarations
	 */
	public void addCssBlock( CssBlock cssBlock ) {
		this.isInitialised = false;
		String selector = cssBlock.getSelector().toLowerCase();
		if (selector.charAt(0) == '.') {
			selector = selector.substring( 1 );
		}
		String[] groupNames = cssBlock.getGroupNames();
		HashMap[] groups = new HashMap[ groupNames.length ];
		for (int i = 0; i < groups.length; i++) {
			groups[i] = cssBlock.getGroupDeclarations(groupNames[i]);
		}
		HashMap target = null;
		if ("colors".equals(selector)) {
			target = this.colors;
		} else if ("fonts".equals(selector)) {
			target = this.fonts;
		} else if ("backgrounds".equals(selector)) {
			target = this.backgrounds;
		} else if ("borders".equals(selector)) {
			target = this.borders;
		}
		if (target != null) {
			for (int i = 0; i < groups.length; i++) {
				String name = groupNames[i];
				HashMap group = groups[i]; 
				HashMap targetGroup = (HashMap) target.get( name );
				if (targetGroup == null) {
					target.put( name, group );
				} else {
					targetGroup.putAll( group );
				}
			}
		} else { // this is a style:
			String parent = "default";
			int extendsPos = selector.indexOf(" extends ");
			if (extendsPos != -1) {
				parent = selector.substring( extendsPos + 9).trim();
				selector = selector.substring(0, extendsPos ).trim();
				if ("default".equals(selector ) ) {
					throw new BuildException( "Invalid CSS code: The style [default] must not extend any other style.");
				}
			}
			if ("default".equals(selector ) ) {
				parent = null;
			}
			// check for reserved names of the style-selector:
			if (KEYWORDS.get(selector) != null) {
				throw new BuildException( "Invalid CSS code: The style-selector [" + selector + "] uses a reserved keyword, please choose another name.");
			}
			// check for invalid selector-names:
			if ( (selector.indexOf(' ') != -1) || (selector.indexOf('\t') != -1)) {
				throw new BuildException( "Invalid CSS code: The style-selector [" + selector + "] is invalid, please do not include spaces in selector-names.");
			}
			Style style = (Style) this.stylesByName.get( selector );
			if (style == null) {
				style = new Style( selector, parent, cssBlock );
				this.styles.add( style );
				this.stylesByName.put(  selector, style );
			} else {
				style.add( cssBlock );
			}
		}
	}
	
	/**
	 * Retrieves all defined styles of this sheet.
	 * 
	 * @return array of Style with all defined styles.
	 */
	public Style[] getAllStyles() {
		return (Style[]) this.styles.toArray( new Style[ this.styles.size() ]);
	}
	
	/**
	 * Sets the parents of the styles.
	 * This method is automatically called when the sourcecode 
	 * will be retrieved.
	 * 
	 * @throws BuildException when invalid heritances are found.
	 * @see #isInherited()
	 * @see #getSourceCode()
	 */
	public void inherit() {
		Style[] allStyles = getAllStyles();
		for (int i = 0; i < allStyles.length; i++) {
			Style style = allStyles[i];
			checkInheritanceHierarchy( style );
			String parentName = style.getParentName();
			if (parentName != null) {
				Style parent = getStyle( parentName );
				if (parent == null) {
					throw new BuildException("Invalid CSS code: the style [" + style.getSelector() + "] extends the non-existing style [" + parentName + "].");
				}
				style.setParent( parent );
			}
		}
	}
	
	/**
	 * Checks whether the inheritance hierarchy is correct.
	 * An incorrect hierarchy is for example a loop definition,
	 * in which a parent-style extends a child-style:
	 * <pre>
	 * myStyle extends parent {
	 * 		font-face: monospace;
	 * }
	 * parent extends grandparent {
	 * 		font-size: large;
	 * 		font-face: system;
	 * }
	 * grandparent extends myStyle {
	 * 		background-color: white;
	 * }
	 * </pre>
	 * 
	 * 
	 * @param style the style whose inheritance hierarchy should be checked.
	 * @throws BuildException when a circle definition is found.
	 */
	private void checkInheritanceHierarchy(Style style) {
		HashMap parentNames = new HashMap( 5 );
		//System.out.println("checking inheritance of style " + style.getSelector());
		String originalSelector = style.getSelector();
		String parentName = style.getParentName(); 
		while (parentName != null) {
			//System.out.println( style.getSelector() + " extends " + parentName );
			String currentSelector = style.getSelector();
			parentName = parentName.toLowerCase();
			if (parentNames.get(parentName) == null) {
				// okay, this ancestor is not known yet.
				parentNames.put( parentName, Boolean.TRUE );
			} else {
				throw new BuildException("Invalid CSS code: Loop in inheritance found: The style [" + originalSelector + "] extends the child-style [" + parentName + "]. Please check your extends operator.");
			}
			style = getStyle( parentName );
			if (style == null) {
				throw new BuildException("Invalid CSS code: The style [" + currentSelector + "] extends the non-existing style [" + parentName + "]. Please define the style [" + parentName + "] or remove the extends operator.");
			}
			parentName = style.getParentName();
		}
	}

	/**
	 * Determines whether the styles have sorted their inheritance out.
	 * 
	 * @return true when the inheritance of the styles was already effected.
	 * @see #inherit()
	 */
	public boolean isInherited() {
		return this.isInitialised;
	}
	
	public String[] getSourceCode() {
		throw new BuildException("to be done.");
	}

	/**
	 * Adds another style sheet to this one.
	 * All properties in the given sheet will override this ones.
	 * 
	 * @param sheet the style sheet. The sheet will not be changed.
	 */
	public void add(StyleSheet sheet) {
		copyStyles( sheet );
		copyHashMap( sheet.backgrounds, this.backgrounds);
		copyHashMap( sheet.borders, this.borders);
		copyHashMap( sheet.fonts, this.fonts );
		copyHashMap( sheet.colors, this.colors);
	}

	/**
	 * Retrieves all styles which are actually used by the application.
	 * 
	 * @return an array of all styles which are actually used.
	 */
	public String[] getUsedStyleNames() {
		return (String[]) this.usedStyles.keySet().toArray( new String[ this.usedStyles.size() ] );
	}

	/**
	 * Checks if a specific style is actually used.
	 * 
	 * @param name the name of the style
	 * @return true when the style is being used.
	 */
	public boolean isUsed(String name) {
		return ( this.usedStyles.get( name ) != null );
	}

}
