/*
 * Created on 04-Jan-2004 at 23:04:12.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.preprocess;

import de.enough.polish.util.StringList;

import org.apache.tools.ant.BuildException;

/**
 * <p>Converts CSS files to Java-Code.</p>
 * general thoughts:
 * - all widgets, backgrounds and borders have an xml-file describing the possible parameters
 *   (for the predefined widgets, this xml-file is build-in)
 *   ImageItem.xml:
 * 	<parameters> !!es muessen nur zusaetzliche parameter beschrieben werden!
 * 		<parameter name="image" alt="img,img-source" type="String" mandatory="yes" />
 * 		??<parameter name="style" type="Style" mandatory="yes" default="ImageItem" />
 * 	</parameters>
 * 	<selector default="image" alt="img,images" />
 * - The Css2Java-Task will create backgrounds and borders directly
 * - also the new-statements will be generated
 * 
 * Wenn sich Styles, Backgrounds oder Borders wiederholen, so erstellt der Task automatisch
 * nur eine Version davon und gibt nur referenzen weiter, ohne dass dies vom designer vorher
 * festgelegt werden muesste. 
 *
 * @author Robert Virkus, robert@enough.de
 * <pre>
 * history
 *        04-Jan-2004 - rob creation
 * </pre>
 */
public class CssConverter {
	
	private static final String INCLUDE = "//$$IncludeStyleSheetDefinitionHere$$//";

	/**
	 * 
	 */
	public CssConverter() {
		super();
		// TODO enough implement Css2JavaConverter
	}
	
	public void convertStyleSheet( StringList sourceCode, StyleSheet styleSheet ) {
		// search for the position to include the style-sheet definitions:
		int index = -1;
		while (sourceCode.next()) {
			String line = sourceCode.getCurrent();
			if (INCLUDE.equals(line)) {
				index = sourceCode.getCurrentIndex() + 1;
				break;
			}
		}
		if (index == -1) {
			throw new BuildException("Unable to modify SytleSheet.java, include point [" + INCLUDE + "] not found.");
		}
		// now insert all used style-definitions:
		String[] styleNames = styleSheet.getUsedStyleNames();
		// problem: could be that there are dependencies between styles...
		// than the base style needs to be defined before the dependent style.
	}

}
