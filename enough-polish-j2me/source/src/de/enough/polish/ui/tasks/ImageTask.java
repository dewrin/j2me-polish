//#condition polish.usePolishGui
/*
 * Created on 05-Jan-2004 at 22:27:59.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ui.tasks;

import de.enough.polish.ui.ImageConsumer;
//#ifdef polish.usePolishGui
import de.enough.polish.ui.StyleSheet;
import de.enough.polish.ui.Screen;
//#endif
import de.enough.polish.util.Debug;

import javax.microedition.lcdui.Image;

import java.io.IOException;
import java.util.Hashtable;
import java.util.TimerTask;

/**
 * <p>Loads an Image in the background using a Timer.</p>
 * <p>The user needs to schedule this task at the timer himself/herself.</p>
 *
 * @author Robert Virkus, robert@enough.de
 * <pre>
 * history
 *        05-Jan-2004 - rob creation
 * </pre>
 */
public class ImageTask extends TimerTask {
	private String name;
	private ImageConsumer imageConsumer;
	private Hashtable imageCache;
	private boolean cache;

	/**
	 * Creates a new ImageTask.
	 * 
	 * @param name the name of the image
	 * @param imageConsumer the consumer of the image
	 * @param imageCache the cache for loaded images
	 * @param cache true when the loaded image should be written to the imageCache
	 */
	public ImageTask(String name, ImageConsumer imageConsumer,	Hashtable imageCache, boolean cache) 
	{
		this.name = name;
		this.imageConsumer = imageConsumer;
		this.imageCache = imageCache;
		this.cache = cache;
	}

	/**
	 * tries to load the image. 
	 */
	public void run() {
		try {
			Image image = Image.createImage( this.name );
			if ( this.cache ) {
				this.imageCache.put( this.name, image );
			}
			this.imageConsumer.setImage( this.name, image );
			//#ifdef polish.usePolishGui
			Screen currentScreen = StyleSheet.currentScreen;
			if (currentScreen != null) {
				currentScreen.repaint();
			}
			//#endif
		} catch (IOException e) {
			//#debug error
			Debug.debug( "ImageTask: unable to load image [" + this.name + "].", e);
		}

	}

}
