/*
 * Created on 09-Mar-2004 at 21:39:34.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.preprocess.backgrounds;

import de.enough.polish.preprocess.*;

import org.apache.tools.ant.BuildException;

import java.util.HashMap;

/**
 * <p>Creates the Image- or the BorderedImageBackground.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        09-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class ImageBackgroundConverter extends BackgroundConverter {
	
	/**
	 * Creates a new creator
	 */
	public ImageBackgroundConverter() {
		super();
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.BackgroundConverter#createNewStatement(java.util.HashMap, de.enough.polish.preprocess.Style, de.enough.polish.preprocess.StyleSheet)
	 */
	protected String createNewStatement(HashMap background, Style style, StyleSheet styleSheet) throws BuildException {
		//TODO rob also allow other CSS settings:
		// background-attachment,
		// background-position ???,
		// background-repeat
		String imageUrl = (String) background.get("image");
		imageUrl = getUrl( imageUrl );
		if (this.hasBorder) {
			return "new " + BACKGROUNDS_PACKAGE + "BorderedImageBackground( "  
					+ this.color + ", " + imageUrl +  ", " 
					+ this.borderColor + ", " + this.borderWidth + ")";
		} else {
			return "new " + BACKGROUNDS_PACKAGE + "ImageBackground( " 
					+ this.color + ", \"" + imageUrl + "\")";
		}
	}
}
