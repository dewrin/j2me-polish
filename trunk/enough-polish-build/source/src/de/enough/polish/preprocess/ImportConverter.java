/*
 * Created on 26-Feb-2004 at 15:03:21.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.preprocess;

import de.enough.polish.util.StringList;

import java.util.*;
import java.util.HashMap;
import java.util.Set;

/**
 * <p>Converts the import-statements to allow the parallel usage of the polish-gui and the standard java-gui.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        26-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public final class ImportConverter {
	
	private HashMap polishToMidp1;
	private HashMap midp1ToPolish;
	private HashMap midp2ToPolish;
	private String[] completeLcdUiReplacement;
	
	/**
	 * Creates a new import converter.
	 */
	public ImportConverter() {
		// init import statements to translate from the J2ME- to the polish-GUI:
		HashMap toPolish = new HashMap();
		toPolish.put( "javax.microedition.lcdui.Choice", "de.enough.polish.ui.Choice");
		toPolish.put( "javax.microedition.lcdui.ChoiceGroup", "de.enough.polish.ui.ChoiceGroup");
		toPolish.put( "javax.microedition.lcdui.CustomItem", "de.enough.polish.ui.CustomItem");
		toPolish.put( "javax.microedition.lcdui.DateField", "de.enough.polish.ui.DateField");
		toPolish.put( "javax.microedition.lcdui.Form", "de.enough.polish.ui.Form");
		toPolish.put( "javax.microedition.lcdui.Gauge", "de.enough.polish.ui.Gauge");
		toPolish.put( "javax.microedition.lcdui.ImageItem", "de.enough.polish.ui.ImageItem");
		toPolish.put( "javax.microedition.lcdui.Item", "de.enough.polish.ui.Item");
		toPolish.put( "javax.microedition.lcdui.ItemCommandListener", "de.enough.polish.ui.ItemCommandListener");
		toPolish.put( "javax.microedition.lcdui.ItemStateListener", "de.enough.polish.ui.ItemStateListener");
		toPolish.put( "javax.microedition.lcdui.List", "de.enough.polish.ui.List");
		toPolish.put( "javax.microedition.lcdui.Spacer", "de.enough.polish.ui.Spacer");
		toPolish.put( "javax.microedition.lcdui.StringItem", "de.enough.polish.ui.StringItem");
		toPolish.put( "javax.microedition.lcdui.TextBox", "de.enough.polish.ui.TextBox");
		toPolish.put( "javax.microedition.lcdui.TextField", "de.enough.polish.ui.TextField");
		toPolish.put( "javax.microedition.lcdui.Ticker", "de.enough.polish.ui.Ticker");
		this.midp2ToPolish = toPolish;
		
		toPolish = new HashMap( toPolish );
		toPolish.put( "javax.microedition.lcdui.game.GameCanvas", "de.enough.polish.ui.game.GameCanvas");
		toPolish.put( "javax.microedition.lcdui.game.Layer", "de.enough.polish.ui.game.Layer");
		toPolish.put( "javax.microedition.lcdui.game.LayerManager", "de.enough.polish.ui.game.LayerManager");
		toPolish.put( "javax.microedition.lcdui.game.Sprite", "de.enough.polish.ui.game.Sprite");
		toPolish.put( "javax.microedition.lcdui.game.TiledLayer", "de.enough.polish.ui.game.TiledLayer");
		toPolish.put( "javax.microedition.lcdui.game.*", "de.enough.polish.ui.game.*");
		this.midp1ToPolish = toPolish;
		
		// that was peanuts now the tricky part comes:
		// when javax.microedition.lcdui.* is imported, there are still
		// some base classes which cannot be implemented by the polish framework,
		// these have to be inserted as well:
		this.completeLcdUiReplacement = new String[]{
				"import javax.microedition.lcdui.CommandListener;",
				"import javax.microedition.lcdui.Alert;",
				"import javax.microedition.lcdui.AlertType;",
				"import javax.microedition.lcdui.Canvas;",
				"import javax.microedition.lcdui.Command;",
				"import javax.microedition.lcdui.Display;",
				"import javax.microedition.lcdui.Displayable;",
				"import javax.microedition.lcdui.Font;",
				"import javax.microedition.lcdui.Graphics;",
				"import javax.microedition.lcdui.Image;",
				"import javax.microedition.lcdui.Screen;",
				"import de.enough.polish.ui.*;"
		};

		
		// init import statements to translate from the polish- to the J2ME-GUI:
		HashMap toJavax = new HashMap();
		toJavax.put( "de.enough.polish.ui.*", "javax.microedition.lcdui.*" );
		// add all javaxToPolish-elements switched over: 
		Set set = toPolish.keySet();
		for (Iterator iter = set.iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			toJavax.put( toPolish.get(key), key );
		}
		this.polishToMidp1 = toJavax;
	}
	
	/**
	 * Changes the import statements for the given file.
	 * 
	 * @param usePolishGui True when the polish-GUI should be used instead of the standard J2ME-GUI.
	 * @param isMidp1 True when the MIDP/1-standard is supported, false when the MIDP/2-standard is supported.
	 * @param sourceCode The source code
	 * @return True when the source code has been changed,
	 *         otherwise false is returned.
	 */
	public boolean processImports( boolean usePolishGui, boolean isMidp1, StringList sourceCode ) {
		// go through the code and search for import statements:
		boolean changed = false;
		while (sourceCode.next()) {
			String line = sourceCode.getCurrent().trim();
			if (line.startsWith("import ")) {
				String importContent = line.substring( 7, line.length() -1 ).trim();
				if ( usePolishGui ) {
					HashMap translations = this.midp1ToPolish;
					if (!isMidp1) {
						translations = this.midp2ToPolish;
					}
					// translate import statements from javax.microedition.lcdui to polish:
					String replacement = (String) translations.get(importContent);
					if ( replacement != null) {
						changed = true;
						sourceCode.setCurrent("import " + replacement + ";");
					} else if ("javax.microedition.lcdui.*".equals( importContent) ) {
						// check for the javax.microedition.lcdui.* import:
						changed = true;
						// neutralise this import:
						sourceCode.setCurrent( "// neutralised: " + importContent );
						// insert replacement:
						sourceCode.insert( this.completeLcdUiReplacement );
						int index = sourceCode.getCurrentIndex() + this.completeLcdUiReplacement.length;
						sourceCode.setCurrentIndex( index );
					}
				} else {
					// translate import statements from polish to javax.microedition.lcdui:
					String replacement = (String) this.polishToMidp1.get(importContent);
					if ( replacement != null) {
						changed = true;
						sourceCode.setCurrent("import " + replacement + ";");
					}
				}
				
			} else if (line.startsWith("public class ")) {
				break;
			} else if (line.startsWith("protected class ")) {
				break;
			} else if (line.startsWith("class ")) {
				break;
			} else if (line.startsWith("public interface ")) {
				break;
			} else if (line.startsWith("protected interface ")) {
				break;
			} else if (line.startsWith("interface ")) {
				break;
			}
		}
		return changed;
	}
	
}
