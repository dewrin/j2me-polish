/*
 * Created on 05-Jan-2004 at 20:41:52.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ui;

import de.enough.polish.ui.tasks.ImageTask;

import javax.microedition.lcdui.Image;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Timer;

/**
 * <p>Manages all defined styles of a specific project.</p>
 * <p>This class is actually pre-processed to get the styles specific for the project and the device.</p>
 *
 * @author Robert Virkus, robert@enough.de
 * <pre>
 * history
 *        05-Jan-2004 - rob creation
 * </pre>
 */
public final class StyleSheet {
	
	private static Hashtable stylesByName;
	private static Hashtable backgroundsByName;
	private static Hashtable bordersByName;
	private static Hashtable imagesByName;
	//#ifdef polish.images.backgroundLoad
	private static Hashtable scheduledImagesByName;
	private static Timer timer;
	//#endif
	private static Style defaultStyle;
	
	static {
		// do not change the following line!
		//$$IncludeStyleSheetDefinitionHere$$//
	}
	
	/**
	 * Retrieves a the style with the given name.
	 * If that style is not defined, the default-style will be returned.
	 *  
	 * @param name the name of the style
	 * @return the style with the given name or the default-style
	 * @throws NullPointerException when the name is null
	 */
	public static Style getStyle( String name ) {
		//TODO set the style as the current style?
		if (stylesByName==null) {
			return defaultStyle;
		}
		Style style = (Style) stylesByName.get( name );
		if (style == null) {
			return defaultStyle;
		} else {
			return style;
		}
	}
	
	/**
	 * Retrieves a the background with the given name.
	 * If that background is not defined, the default-background will be returned.
	 *  
	 * @param name the name of the background
	 * @return the background with the given name or the default-background
	 * @throws NullPointerException when the name is null
	 */
	public static Background getBackground( String name ) {
		if (backgroundsByName == null) {
			return defaultStyle.background;
		}
		Background background = (Background) backgroundsByName.get( name );
		if (background == null) {
			return defaultStyle.background;
		} else {
			return background;
		}
	}

	/**
	 * Retrieves a the border with the given name.
	 * If that border is not defined, the default-border will be returned.
	 *  
	 * @param name the name of the border
	 * @return the border with the given name or the default-border
	 * @throws NullPointerException when the name is null
	 */
	public static Border getBorder( String name ) {
		if (bordersByName == null) {
			return defaultStyle.border;
		}
		Border border = (Border) bordersByName.get( name );
		if (border == null) {
			return defaultStyle.border;
		} else {
			return border;
		}
	}
	
	/**
	 * Retrieves the image with the given name.
	 * When the image has been cached before, it will be returned immediately.
	 * When it has not been cached before, it either will be loaded directly
	 * or in a background thread. This behaviour is set in the 
	 * <a href="../../../../definitions/polish_xml.html">polish.xml</a> file.
	 * 
	 * @param name the name of the Image
	 * @param parent the object which needs the image
	 * @param cache true when the image should be cached for later retrieval.
	 *              This costs RAM obviously, so you should decide carefully if
	 *              large images should be cached.
	 * @return the image when it either was cached or is loaded directly.
 	 *              When the should be loaded in the background, it will be later
	 *              set via the ImageConsumer.setImage()-method.
	 * @throws IOException when the image could not be loaded directly
	 * @see ImageConsumer#setImage(String, Image)
	 */
	public static Image getImage( String name, ImageConsumer parent, boolean cache )
	throws IOException 
	{
		// check if the image has been cached before:
		if ( imagesByName != null ) {
			Image image = (Image) imagesByName.get( name );
			if (image != null) {
				return image;
			}
		}
		//#ifdef polish.images.directLoad
		// when images should be loaded directly, try to do so now:
		Image image = Image.createImage( name );
		if (cache) {
			if (imagesByName == null ) {
				imagesByName = new Hashtable();
			}
			imagesByName.put( name, image );
		}
		//# return image;
		//#else
		// when images should be loaded in the background, tell the background-thread to do so now:
		if (cache) {
			if ( (scheduledImagesByName != null) && (scheduledImagesByName.get(name) != null) ) {
				// this image is already scheduled to load:
				return null;
			}
			if (scheduledImagesByName == null ) {
				scheduledImagesByName = new Hashtable();
				scheduledImagesByName.put( name, Boolean.TRUE );
			}
			if (imagesByName == null ) {
				imagesByName = new Hashtable();
			}
		}
		if (timer == null) {
			timer = new Timer();
		}
		ImageTask task = new ImageTask( name, parent, imagesByName, cache );
		timer.schedule( task, 10 );
		return null;
		//#endif
	}
	
}
