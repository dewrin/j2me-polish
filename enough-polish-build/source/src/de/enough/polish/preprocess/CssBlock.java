/*
 * Created on 02-Mar-2004 at 15:39:55.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.preprocess;

import de.enough.polish.util.TextUtil;

import org.apache.tools.ant.BuildException;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Interpretes a single CSS block.</p>
 * <p>A block can consist of a single style or of the 
 *   colors, borders, backgrounds or fonts-definition.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        02-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class CssBlock {
	
	private String selector;
	private HashMap declarationsByName;
	private HashMap groupsByNames;
	private ArrayList groups;
	
	/**
	 * Creates a new CSS Block
	 * 
	 * @param cssCode the CSS code of this block.
	 * @throws BuildException when the code contains errors.
	 */
	public CssBlock( String cssCode ) {
		this.declarationsByName = new HashMap();
		this.groupsByNames = new HashMap();
		this.groups = new ArrayList();
		int parenthesisPos = cssCode.indexOf('{');
		this.selector = cssCode.substring(0, parenthesisPos ).trim();
		cssCode = cssCode.substring( parenthesisPos + 1, cssCode.length() -1 ).trim();
		// now read any inner blocks:
		while ( (parenthesisPos = cssCode.indexOf('{')) != -1) {
			int start = cssCode.lastIndexOf(';', parenthesisPos);
			if (start == -1) {
				start = 0;
			} else {
				start++;
			}
			int end = cssCode.indexOf('}', parenthesisPos );
			if (end == -1) {
				throw new BuildException("Invalid CSS code: unable to parse [" + cssCode + "] - at least one closing parenthesis '}' is missing.");
			}
			String innerBlock = cssCode.substring( start, end + 1);
			parseInnerBlock( innerBlock );
			cssCode = TextUtil.replace( cssCode, innerBlock, "" );
		}
		// now read the rest of the definitions:
		String[] declarations = TextUtil.splitAndTrim(cssCode, ';');
		for (int i = 0; i < declarations.length; i++) {
			String declaration = declarations[i];
			if (declaration.length() > 0) {
				addDeclaration( declaration );
			}
		}
	}

	/**
	 * Adds a declaration to this block.
	 * 
	 * @param declaration the declaration in the form "attribute: value"
	 */
	private void addDeclaration(String declaration) {
		int colonPos = declaration.indexOf(':');
		if (colonPos == -1) {
			throw new BuildException("Invalid CSS code: unable to parse declaration [" + declaration + ";] - found no colon between attribute and value.");
		}
		String attribute = declaration.substring(0, colonPos).trim();
		String value = declaration.substring(colonPos + 1).trim();
		String groupName = attribute;
		String subAttribute = attribute;
		int hyphenPos = attribute.indexOf('-');
		if (hyphenPos != - 1) {
			groupName = attribute.substring(0, hyphenPos);
			subAttribute = attribute.substring(hyphenPos + 1);
		}
		this.declarationsByName.put(attribute, value);
		HashMap group = (HashMap) this.groupsByNames.get( groupName );
		if (group == null) {
			group = new HashMap();
			this.groupsByNames.put( groupName, group );
			this.groups.add( groupName );
		}
		group.put( subAttribute, value );
	}

	/**
	 * @param code the CSS code
	 */
	private void parseInnerBlock(String code) {
		int parenthesisPos = code.indexOf('{');
		String blockName = code.substring(0, parenthesisPos ).trim() + '-';
		String declarationBlock = code.substring( parenthesisPos + 1, code.length() -1 ).trim();
		String[] declarations = TextUtil.splitAndTrim( declarationBlock, ';' );
		try {
			for (int i = 0; i < declarations.length; i++) {
				String declaration = declarations[i];
				if (declaration.length() > 0) {
					addDeclaration( blockName + declaration );
				}
			}
		} catch (BuildException e ) {
			throw new BuildException( "Invalid CSS code: inner block [" + code + "] contains an invalid declaration: " + e.getMessage(), e );
		}
	}
	

	
	/**
	 * @return Returns the name of the selector of this block.
	 */
	public String getSelector() {
		return this.selector;
	}
	
	/**
	 * @return Returns all declarations stored in a HashMap
	 */
	public HashMap getDeclarationsMap() {
		return this.declarationsByName;
	}
	
	/**
	 * Retrieves the names of all found groups like "font" or "background"
	 * 
	 * @return String array with the names of all found groups
	 */
	public String[] getGroupNames() {
		return (String[]) this.groups.toArray( new String[ this.groups.size() ]);
	}
	
	/**
	 * Retrieves the declarations for the specific group.
	 * 
	 * @param group the name of the group.
	 * @return all declaration of the specified group in a HashMap
	 */
	public HashMap getGroupDeclarations( String group ) {
		return (HashMap) this.groupsByNames.get( group );
	}
	

}
