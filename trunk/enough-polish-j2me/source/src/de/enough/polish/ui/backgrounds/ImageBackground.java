//#condition polish.usePolishGui
/*
 * Created on 14-Mar-2004 at 21:31:51.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ui.backgrounds;

import de.enough.polish.ui.*;
import de.enough.polish.ui.Background;
import de.enough.polish.ui.StyleSheet;
import de.enough.polish.util.Debug;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import java.io.IOException;

/**
 * <p>Paints an image as a background.</p>
 * <p>Following CSS parameters are supported:
 * <ul>
 * 		<li><b>image</b>: the image url, e.g. url( bg.png )</li>
 * 		<li><b>color</b>: the background color, should the image
 * 							be smaller than the actual background-area.</li>
 * 		<li><b></b>: </li>
 * 		<li><b></b>: </li>
 * </ul>
 * </p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        14-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class ImageBackground 
extends Background
//#ifdef polish.images.backgroundLoad
implements ImageConsumer
//#endif
{
	private Image image;
	private int color;
	
	/**
	 * Creates a new image background.
	 * 
	 * @param color the background color
	 * @param imageUrl the url of the image, e.g. "/bg.png"
	 */
	public ImageBackground( int color, String imageUrl ) {
		this.color = color;
		try {
			this.image = StyleSheet.getImage(imageUrl, this, false);
		} catch (IOException e) {
			//#debug error
			Debug.debug( "unable to load image [" + imageUrl + "]: " + e.getMessage(), e );
		}
	}

	//#ifdef polish.images.backgroundLoad
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ImageConsumer#setImage(java.lang.String, javax.microedition.lcdui.Image)
	 */
	public void setImage(String name, Image image) {
		this.image = image;
	}
	//#endif
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		g.setColor( this.color );
		g.fillRect( x, y, width + 1, height + 1 );
		if (this.image != null) {
			int centerX = x + (width / 2);
			int centerY = x + (height / 2);
			g.drawImage(this.image, centerX, centerY, Graphics.HCENTER | Graphics.VCENTER );
		}
	}
	
}
