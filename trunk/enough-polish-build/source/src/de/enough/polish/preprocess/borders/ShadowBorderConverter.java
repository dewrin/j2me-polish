/*
 * Created on 10-Mar-2004 at 16:18:39.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.preprocess.borders;

import de.enough.polish.preprocess.*;

import org.apache.tools.ant.BuildException;

import java.util.HashMap;

/**
 * <p>Creates a shadow-like border.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        10-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class ShadowBorderConverter extends BorderConverter {
	
	private static final HashMap TYPES = new HashMap();
	static {
		TYPES.put("bottom-right-shadow", BORDERS_PACKAGE + "BottomRightShadowBorder");
		TYPES.put("right-bottom-shadow", BORDERS_PACKAGE + "BottomRightShadowBorder");
		TYPES.put("shadow", BORDERS_PACKAGE + "BottomRightShadowBorder");
	}
	
	/**
	 * Creates a new instance
	 */
	public ShadowBorderConverter() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.BorderConverter#createNewStatement(java.util.HashMap, de.enough.polish.preprocess.Style, de.enough.polish.preprocess.StyleSheet)
	 */
	protected String createNewStatement(HashMap border, Style style, StyleSheet styleSheet) throws BuildException {
		String typeName = (String) border.get("type");
		String type = (String) TYPES.get( typeName );
		if (type == null) {
			throw new BuildException("Invalid CSS: the shadow border [" + typeName + "] is not supported. Please define another shadow-border in the [type] argument, e.g. \"type: bottom-right-shadow\".");
		}
		String offset = (String) border.get("offset");
		if (offset == null) {
			offset = "1"; // default ofset
		} else {
			parseInt( "offset", offset );
		}
		return "new " + type + "( " + this.color + ", " + this.width 
				+ ", " + offset + ")";
	}
}
