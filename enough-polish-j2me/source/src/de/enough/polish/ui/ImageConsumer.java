//#condition polish.usePolishGui
/*
 * Created on 05-Jan-2004 at 21:52:12.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ui;

import javax.microedition.lcdui.Image;

/**
 * <p>Defines the interface of any object which needs images.</p>
 *
 * @author Robert Virkus, robert@enough.de
 * <pre>
 * history
 *        05-Jan-2004 - rob creation
 * </pre>
 */
public interface ImageConsumer {
	
	/**
	 * sets the image which has been loaded in the background.
	 * 
	 * @param name the name of the image. 
	 * 			This allows the image-consumer to differentiate between several images.
	 * @param image the image which was loaded in the background thread.
	 */
	public void setImage( String name, Image image );
}
