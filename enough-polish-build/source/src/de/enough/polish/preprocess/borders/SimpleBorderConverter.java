/*
 * Created on 10-Mar-2004 at 15:54:41.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.preprocess.borders;

import de.enough.polish.preprocess.*;

import org.apache.tools.ant.BuildException;

import java.util.HashMap;

/**
 * <p>Creates the source code for a simple border.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        10-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class SimpleBorderConverter extends BorderConverter {
	/**
	 * Creates a new simple border creator
	 */
	public SimpleBorderConverter() {
		super();
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.BorderConverter#createNewStatement(java.util.HashMap, de.enough.polish.preprocess.Style, de.enough.polish.preprocess.StyleSheet)
	 */
	protected String createNewStatement(HashMap border, Style style, StyleSheet styleSheet) throws BuildException {
		return "new " + BORDERS_PACKAGE + "SimpleBorder( " + this.color 
													+ ", " + this.width + ")";	
	}
}
