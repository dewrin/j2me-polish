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
	
	private static final HashMap REPEAT_TYPES = new HashMap();
	static {
		REPEAT_TYPES.put("repeat", BACKGROUNDS_PACKAGE + "ImageBackground.REPEAT");
		REPEAT_TYPES.put("no-repeat", BACKGROUNDS_PACKAGE + "ImageBackground.NO_REPEAT");
		REPEAT_TYPES.put("none", BACKGROUNDS_PACKAGE + "ImageBackground.NO_REPEAT");
		REPEAT_TYPES.put("repeat-x", BACKGROUNDS_PACKAGE + "ImageBackground.REPEAT_X");
		REPEAT_TYPES.put("repeat-y", BACKGROUNDS_PACKAGE + "ImageBackground.REPEAT_Y");
	}
	
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
		String imageUrl = (String) background.get("image");
		imageUrl = getUrl( imageUrl );
		String repeat = (String) background.get("repeat");
		if (repeat == null) {
			repeat = BACKGROUNDS_PACKAGE + "ImageBackground.NO_REPEAT";
		} else {
			String rep = (String) REPEAT_TYPES.get( repeat );
			if (rep == null) {
				throw new BuildException("Invalid CSS: the repeat-type [" + repeat +"] is not supported by the image background.");
			}
			repeat = rep;
		}
		return "new " + BACKGROUNDS_PACKAGE + "ImageBackground( " 
				+ this.color + ", \"" + imageUrl + "\", " + repeat + " )";
	}
}
