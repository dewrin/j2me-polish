/*
 * Created on 09-Mar-2004 at 21:48:27.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.preprocess.backgrounds;

import de.enough.polish.preprocess.*;

import org.apache.tools.ant.BuildException;

import java.util.HashMap;

/**
 * <p>Creates a RoundRect- or BorderedRoundRectBackground.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        09-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class RoundRectBackgroundConverter extends BackgroundConverter {
	
	/**
	 * Instantiates a new creator
	 */
	public RoundRectBackgroundConverter() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.BackgroundConverter#createNewStatement(java.util.HashMap, de.enough.polish.preprocess.Style, de.enough.polish.preprocess.StyleSheet)
	 */
	protected String createNewStatement(HashMap background, Style style, StyleSheet styleSheet) throws BuildException {
		String arc = (String) background.get("arc");
		if (arc != null) {
			parseInt( "arc", arc );
		} else {
			arc = "10";
		}
		String arcHeight = (String) background.get("arc-height");
		if (arcHeight != null) {
			parseInt( "arc-height", arc );
		} else {
			arcHeight = arc;
		}
		String arcWidth = (String) background.get("arc-width");
		if (arcWidth != null) {
			parseInt( "arc-width", arc );
		} else {
			arcWidth = arc;
		}
		if (this.hasBorder ) {
			return "new " + BACKGROUNDS_PACKAGE + "BorderedRoundRectBackground( " 
					+ this.color + "," + arcWidth + ", " + arcHeight + ", " 
					+ this.borderColor + ", " + this.borderWidth + ")";
		} else {
			return "new " + BACKGROUNDS_PACKAGE + "RoundRectBackground( " 
					+ this.color + "," + arcWidth + ", " + arcHeight + ")";
		}
	}

}
