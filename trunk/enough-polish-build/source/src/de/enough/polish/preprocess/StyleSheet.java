/*
 * Created on 16-Jan-2004 at 12:04:15.
 *
 * Copyright (c) 2004 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.preprocess;

import de.enough.polish.util.TextUtil;

import org.apache.tools.ant.BuildException;

import java.util.*;
import java.util.HashMap;
import java.util.Set;

/**
 * <p>Represents a StyleSheet for a specific application.</p>
 *
 * <p>copyright Enough Software 2004</p>
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
		KEYWORDS.put("int", Boolean.TRUE );
		KEYWORDS.put("double", Boolean.TRUE );
		KEYWORDS.put("float", Boolean.TRUE );
		KEYWORDS.put("boolean", Boolean.TRUE );
		KEYWORDS.put("class", Boolean.TRUE );
		KEYWORDS.put("public", Boolean.TRUE );
		KEYWORDS.put("private", Boolean.TRUE );
		KEYWORDS.put("protected", Boolean.TRUE );
		KEYWORDS.put("final", Boolean.TRUE );
		KEYWORDS.put("static", Boolean.TRUE );
		KEYWORDS.put("transient", Boolean.TRUE );
	}
	// some CSS classes which are NOT dynamic classes (like p, a, or form) 
	private static final HashMap PSEUDO_CLASSES = new HashMap();
	static {
		PSEUDO_CLASSES.put("focused", Boolean.TRUE );
		PSEUDO_CLASSES.put("title", Boolean.TRUE );
		PSEUDO_CLASSES.put("default", Boolean.TRUE );
		PSEUDO_CLASSES.put("menu", Boolean.TRUE );
		PSEUDO_CLASSES.put("menuitem", Boolean.TRUE );
	}
	private final static CssBlock DEFAULT_STYLE = new CssBlock( 
			"default {"
			+ "font-color: black;"
			+ "font-face: system;"
			+ "font-style: bold;"
			+ "font-size: medium;"
			+ "background-color: white;"
			+ "}"
			);

	private HashMap stylesByName;
	private ArrayList styles;
	private HashMap backgrounds;
	private HashMap borders;
	private HashMap fonts;
	private HashMap colors;
	private HashMap usedStyles;
	
	private boolean isInitialised;
	private long lastModified;

	private boolean containsDynamicStyles;
	private boolean containsBeforeStyle;
	private boolean containsAfterStyle;
	
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
		this.lastModified = sheet.lastModified;
		this.containsDynamicStyles = sheet.containsDynamicStyles;
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
			if (original.getGroup("before") != null) {
				this.containsBeforeStyle = true;
			}
			if (original.getGroup("after") != null) {
				this.containsAfterStyle = true;
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
	 * Retrieves the default style.
	 * 
	 * @return the default style
	 * @see #getStyle(String) - getStyle("default") yields the same result.
	 */
	public Style getDefaultStyle() {
		return (Style) this.stylesByName.get( "default" );
	}	
	/**
	 * Determines whether the specified style is known.
	 * 
	 * @param name the (not case-sensitive) name of the style.
	 * @return true when the specified style is known.
	 */
	public boolean isDefined( String name ) {
		name = name.toLowerCase();
		if ("default".equals(name)) {
			// the default style is always defined!
			return true;
		}
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
	 * Retrieves all styles which are actually used by the application.
	 * 
	 * @return an array of all styles which are actually used.
	 */
	public String[] getUsedStyleNames() {
		return (String[]) this.usedStyles.keySet().toArray( new String[ this.usedStyles.size() ] );
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
			String parent = null;
			int extendsPos = selector.indexOf(" extends ");
			if (extendsPos != -1) {
				parent = selector.substring( extendsPos + 9).trim();
				if (parent.charAt(0) == '.') {
					parent = parent.substring(1);
				}
				selector = selector.substring(0, extendsPos ).trim();
				if ("default".equals(selector ) ) {
					throw new BuildException( "Invalid CSS code: The style [default] must not extend any other style.");
				}
			}
			boolean isDynamicStyle = false;
			// check if this style is dynamic:
			isDynamicStyle =  (selector.indexOf(' ') != -1)
						   || (selector.indexOf('\t') != -1)
						   || (selector.indexOf('>') != -1)
						   || (selector.indexOf('*') != -1);
			if (selector.charAt(0) == '.') {
				selector = selector.substring( 1 );
				if (PSEUDO_CLASSES.get(selector) != null) { 
					throw new BuildException("Invalid CSS code: The style [." + selector + "] uses a reserved name, please choose another one.");
				}
			} else {
				// this could be a DYNAMIC style:
				if (PSEUDO_CLASSES.get(selector) == null) {
					isDynamicStyle = true;
				}
			}
			if (isDynamicStyle) {
				this.containsDynamicStyles = true;
				//System.out.println("project uses dynamic style: [" + selector + "]");				
			}
			// check for reserved names of the style-selector:
			if (KEYWORDS.get(selector) != null) {
				throw new BuildException( "Invalid CSS code: The style-selector [" + selector + "] uses a reserved keyword, please choose another name.");
			}
			String styleName = TextUtil.replace( selector, '-', '_' );
			if (isDynamicStyle) {
				selector = TextUtil.replace( selector, ".", "");
				selector = TextUtil.replace( selector, '\t', ' ');
				selector = TextUtil.replace( selector, " > ", ">");
				selector = TextUtil.replace( selector, " > ", ">");
				selector = TextUtil.replace( selector, " * ", "*");
				selector = TextUtil.replace( selector, "  ", " ");
				styleName = TextUtil.replace( selector, ' ', '_');
				styleName = TextUtil.replace( styleName, ">", "__");
				styleName = TextUtil.replace( styleName, "*", "___");
			}
			// check style name for invalid characters:
			if ( (styleName.indexOf('.') != -1 )
				|| (styleName.indexOf('"') != -1 )
				|| (styleName.indexOf('\'') != -1 )
				|| (styleName.indexOf('*') != -1 )
				|| (styleName.indexOf('+') != -1 )
				|| (styleName.indexOf('-') != -1 )
				|| (styleName.indexOf('/') != -1 )
				|| (styleName.indexOf(':') != -1 )
				|| (styleName.indexOf('=') != -1 )
				|| (styleName.indexOf('|') != -1 )
				|| (styleName.indexOf('&') != -1 )
				|| (styleName.indexOf('~') != -1 )
				|| (styleName.indexOf('!') != -1 )
				|| (styleName.indexOf('^') != -1 )
				|| (styleName.indexOf('(') != -1 )
				|| (styleName.indexOf(')') != -1 )
				|| (styleName.indexOf('%') != -1 )
				|| (styleName.indexOf('?') != -1 )
				|| (styleName.indexOf('#') != -1 )
				|| (styleName.indexOf('$') != -1 )
				|| (styleName.indexOf('@') != -1 ) ) {
				throw new BuildException( "Invalid CSS code: The style-selector [" + selector + "] contains invalid characters, please use only alpha-numeric characters for style-names.");
			}
				
			
			Style style = (Style) this.stylesByName.get( styleName );
			if (style == null) {
				style = new Style( selector, styleName, isDynamicStyle, parent, cssBlock );
				this.styles.add( style );
				//System.out.println("added new style [" + style.getStyleName() + "].");
				this.stylesByName.put(  selector, style );
			} else {
				style.add( cssBlock );
			}
			if (style.getGroup("before") != null) {
				this.containsBeforeStyle = true;
			}
			if (style.getGroup("after") != null) {
				this.containsAfterStyle = true;
			}
		}
	}
	
	/**
	 * Determines whether this sheet contains dynamic styles.
	 * Dynamic styles are set during runtime and can be used
	 * to make design changes without actually changing the source
	 * code at all.
	 * Since dynamic styles are much slower than static ones,
	 * a preprocessing-variable "polish.usesDynamicStyles" is
	 * set whenever dynamic styles are used.
	 * 
	 * @return true when this sheet contains dynamic styles.
	 */
	public boolean containsDynamicStyles() {
		return this.containsDynamicStyles;
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
		// create default-style when not explicitely defined:
		if (this.stylesByName.get("default") == null ) {
			addCssBlock(DEFAULT_STYLE);
		}
		Style[] allStyles = getAllStyles();
		for (int i = 0; i < allStyles.length; i++) {
			Style style = allStyles[i];
			//System.out.println("inheriting style [" + style.getSelector() + "].");
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
		this.isInitialised = true;
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
	 * Checks if a specific style is actually used.
	 * 
	 * @param name the name of the style
	 * @return true when the style is being used.
	 */
	public boolean isUsed(String name) {
		return ( this.usedStyles.get( name ) != null );
	}

	/**
	 * @return the time of the last modification 
	 */
	public long lastModified() {
		return this.lastModified;
	}
	
	public void setLastModified( long lastModified ) {
		this.lastModified = lastModified;
	}

	/**
	 * Retrieves all independent color definitions.
	 * 
	 * @return the list of colors which has been defined.
	 */
	public HashMap getColors() {
		return this.colors;
	}
	
	/**
	 * Gets all independently defined defined fonts.
	 * 
	 * @return a map with all defined fonts.
	 */
	public HashMap getFonts() {
		return this.fonts;
	}
	
	/**
	 * Gets all independently defined backgrounds.
	 * 
	 * @return all backgrounds in a map.
	 */
	public HashMap getBackgrounds() {
		return this.backgrounds;
	}
	
	/**
	 * Gets all independently defined borders.
	 * 
	 * @return all borders in a map.
	 */
	public HashMap getBorders() {
		return this.borders;
	}

	/**
	 * Gets the names of all defined styles.
	 * 
	 * @return the names of all defined styles.
	 */
	public String[] getStyleNames() {
		return (String[]) this.stylesByName.keySet().toArray( new String[ this.stylesByName.size()] );
	}

	/**
	 * Retrieves all dynamic styles.
	 *
	 * @return all dynamic styles.
	 */
	public Style[] getDynamicStyles() {
		ArrayList dynamicStyles = new ArrayList();
		Style[] allStyles = getAllStyles();
		for (int i = 0; i < allStyles.length; i++) {
			Style style = allStyles[i];
			if (style.isDynamic()) {
				dynamicStyles.add( style );
			}
		}
		return (Style[]) dynamicStyles.toArray( new Style[ dynamicStyles.size() ] );
	}

	/**
	 * Determines whether this sheet contains style which use the before-element.
	 * 
	 * @return true when this sheet contains styles which make use of the before element
	 */
	public boolean containsAfterStyle() {
		return this.containsAfterStyle;
	}
	
	/**
	 * Determines whether this sheet contains style which use the after-element.
	 * 
	 * @return true when this sheet contains styles which make use of the after element
	 */
	public boolean containsBeforeStyle() {
		return this.containsBeforeStyle;
	}
}
