/*
 * Created on 05-Jan-2004 at 20:41:52.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ui;

import de.enough.polish.ui.tasks.ImageTask;

import javax.microedition.lcdui.*;

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
	
	private static Hashtable imagesByName;
	//#ifdef polish.images.backgroundLoad
	private static Hashtable scheduledImagesByName;
	private static final Boolean TRUE = new Boolean( true );
	private static Timer timer;
	//#endif
	//#ifdef false
	public static final Style defaultStyle = null;
	public static Style focusedStyle = null;
	//#endif
	private static Hashtable stylesByName = new Hashtable();
	
	
	// do not change the following line!
//$$IncludeStyleSheetDefinitionHere$$//
		
	public static Style currentStyle = defaultStyle;
	public static Screen currentScreen;	
	public static AnimationThread animationThread;

	/**
	 * Retrieves the image with the given name.
	 * When the image has been cached before, it will be returned immediately.
	 * When it has not been cached before, it either will be loaded directly
	 * or in a background thread. This behaviour is set in the 
	 * <a href="../../../../definitions/polish_xml.html">polish.xml</a> file.
	 * 
	 * @param name the name of the Image
	 * @param parent the object which needs the image, when the image should be loaded
	 * 		   		in the background, the parent need to implement
	 * 				the ImageConsumer interface when it wants to be notified when
	 * 				the picture has been loaded.
	 * @param cache true when the image should be cached for later retrieval.
	 *              This costs RAM obviously, so you should decide carefully if
	 *              large images should be cached.
	 * @return the image when it either was cached or is loaded directly.
 	 *              When the should be loaded in the background, it will be later
	 *              set via the ImageConsumer.setImage()-method.
	 * @throws IOException when the image could not be loaded directly
	 * @see ImageConsumer#setImage(String, Image)
	 */
	public static Image getImage( String name, Object parent, boolean cache )
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
		// when images should be loaded in the background, 
		// tell the background-thread to do so now:		
		if ( ! (parent instanceof ImageConsumer)) {
			//#debug error
			System.out.println("StyleSheet.getImage(..) needs an ImageConsumer when images are loaded in the background!");
			return null;
		}
		if (scheduledImagesByName == null ) {
			scheduledImagesByName = new Hashtable();
		}
		ImageQueue queue = (ImageQueue) scheduledImagesByName.get(name);
		if (queue != null) {
			// this image is already scheduled to load:
			queue.addConsumer((ImageConsumer) parent);
			return null;
		}
		scheduledImagesByName.put( name, new ImageQueue( (ImageConsumer) parent, cache ) );
		if (imagesByName == null ) {
			imagesByName = new Hashtable();
		}
		if (timer == null) {
			timer = new Timer();
		}
		ImageTask task = new ImageTask( name );
		timer.schedule( task, 10 );
		return null;
		//#endif
	}
	
	//#ifdef polish.images.backgroundLoad
	public static void notifyImageConsumers( String name, Image image ) {
		ImageQueue queue = (ImageQueue) scheduledImagesByName.remove(name);
		if (queue != null) {
			if (queue.cache) {
				imagesByName.put( name, image );
			}
			queue.notifyConsumers(name, image);
			if (currentScreen != null) {
				currentScreen.repaint();
			}
		}
	}
	//#endif
	
	/**
	 * Gets the style with the specified name.
	 * 
	 * @param name the name of the style
	 * @return the specified style or null when no style with the given 
	 * 	       name has been defined.
	 */
	public static Style getStyle( String name ) {
		return (Style) stylesByName.get( name );
	}
	
	//#ifdef polish.useDynamicStyles
	/**
	 * Retrieves the style for the given item.
	 * This function is only available when the &lt;buildSetting&gt;-attribute
	 * [useDynamicStyles] is enabled.
	 * This function allows to set styles without actually using the preprocessing-
	 * directive //#style. Beware that this dynamic style retrieval is not as performant
	 * as the direct-style-setting with the //#style preprocessing directive.
	 *  
	 * @param item the item for which the style should be retrieved
	 * @return the appropriate style. When no specific style is found,
	 *         the default style is returned.
	 */
	public static Style getStyle( Item item ) {
		String itemCssSelector = item.cssSelector;
		String screenCssSelector = item.screen.cssSelector;
		Style style = null;
		String fullStyleName;
		StringBuffer buffer = new StringBuffer();
		buffer.append( screenCssSelector );
		if (item.parent == null) {
			//#debug
			System.out.println("item.parent == null");
			buffer.append('>').append( itemCssSelector );
			fullStyleName = buffer.toString();
			style = (Style) stylesByName.get( fullStyleName );
			if (style != null) {
				return style;
			}
			style = (Style) stylesByName.get( screenCssSelector + " " + itemCssSelector );
		} else if (item.parent.parent == null) {
			// this item is propably in a form or list,
			// typical hierarchy is for example "form>containter>p"                                                 
			Item parent = item.parent;
			String parentCssSelector = parent.cssSelector;
			buffer.append('>').append( parentCssSelector )
				  .append('>').append( itemCssSelector );
			fullStyleName = buffer.toString();
			//#debug
			System.out.println("trying " + fullStyleName);
			style = (Style) stylesByName.get( fullStyleName );
			if (style != null) {
				return style;
			}
			// 1. try: "screen item":
			String styleName = screenCssSelector + " " + itemCssSelector;
			//#debug
			System.out.println("trying " + styleName);
			style = (Style) stylesByName.get( styleName );
			if (style == null) {
				// 2. try: "screen*item":
				styleName = screenCssSelector + "*" + itemCssSelector;
				//#debug
				System.out.println("trying " + styleName);
				style = (Style) stylesByName.get( styleName );
				if (style == null) {
					// 3. try: "parent>item"
					styleName = parentCssSelector + ">" + itemCssSelector;
					//#debug
					System.out.println("trying " + styleName);
					style = (Style) stylesByName.get( styleName );
					if (style == null) {
						// 4. try: "parent item"
						styleName = parentCssSelector + " " + itemCssSelector;
						//#debug
						System.out.println("trying " + styleName);
						style = (Style) stylesByName.get( styleName );
					}
				}
			}
			//#debug
			System.out.println("found style: " + (style != null));
		} else {
			System.out.println("so far unable to set style: complex item setup");
			// this is a tiny bit more complicated....
			fullStyleName = null;
		}
		if (style == null) {
			// try just the item:
			style = (Style) stylesByName.get( itemCssSelector );
			if (style == null) {
				style = defaultStyle;
			}
		}
		stylesByName.put( fullStyleName, style );
		return style;
	}
	//#endif

	//#ifdef polish.useDynamicStyles
	/**
	 * Retrieves a dynamic style for the given screen.
	 * 
	 * @param screen the screen for which a style should be retrieved
	 * @return
	 */
	public static Style getStyle(Screen screen) {
		return (Style) stylesByName.get( screen.cssSelector );
	}		
	//#endif
}
