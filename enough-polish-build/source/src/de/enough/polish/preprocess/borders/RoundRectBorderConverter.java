/*
 * Created on 10-Mar-2004 at 15:59:40.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.preprocess.borders;

import de.enough.polish.preprocess.*;

import org.apache.tools.ant.BuildException;

import java.util.HashMap;

/**
 * <p>Creates a border with round edges.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        10-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class RoundRectBorderConverter extends BorderConverter {
	
	/**
	 * Creates a new instance
	 */
	public RoundRectBorderConverter() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.BorderConverter#createNewStatement(java.util.HashMap, de.enough.polish.preprocess.Style, de.enough.polish.preprocess.StyleSheet)
	 */
	protected String createNewStatement(HashMap border, Style style, StyleSheet styleSheet) throws BuildException 
	{
		String arc = (String) border.get("arc");
		if (arc != null) {
			parseInt( "arc", arc );
		} else {
			arc = "10";
		}
		String arcHeight = (String) border.get("arc-height");
		if (arcHeight != null) {
			parseInt( "arc-height", arc );
		} else {
			arcHeight = arc;
		}
		String arcWidth = (String) border.get("arc-width");
		if (arcWidth != null) {
			parseInt( "arc-width", arc );
		} else {
			arcWidth = arc;
		}
		return "new " + BORDERS_PACKAGE + "RoundRectBorder( " 
					+ this.color + "," + this.width + ", " 
					+ arcWidth + ", " + arcHeight + ")";
	}
}
