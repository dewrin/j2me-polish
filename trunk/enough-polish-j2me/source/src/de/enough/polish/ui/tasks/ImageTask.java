//#condition polish.usePolishGui
/*
 * Created on 05-Jan-2004 at 22:27:59.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ui.tasks;

import de.enough.polish.ui.StyleSheet;
import de.enough.polish.util.Debug;

import javax.microedition.lcdui.Image;

import java.io.IOException;
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

	/**
	 * Creates a new ImageTask.
	 * 
	 * @param name the name of the image
	 */
	public ImageTask(String name ) 
	{
		this.name = name;
	}

	/**
	 * tries to load the image. 
	 */
	public void run() {
		try {
			Image image = Image.createImage( this.name );
			StyleSheet.notifyImageConsumers(this.name, image);
		} catch (IOException e) {
			//#debug error
			Debug.debug( "ImageTask: unable to load image [" + this.name + "].", e);
		}

	}

}
