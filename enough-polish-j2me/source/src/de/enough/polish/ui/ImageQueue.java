//#condition polish.images.backgroundLoad && polish.usePolishGui
/*
 * Created on 19-Apr-2004 at 15:09:38.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ui;

import de.enough.polish.util.Debug;

import javax.microedition.lcdui.Image;

/**
 * <p>Provides a queue for loading images in the background.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        19-Apr-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class ImageQueue {
	public boolean cache;
	private int pos = 1;
	private ImageConsumer[] consumers = new ImageConsumer[5];
	public ImageQueue( ImageConsumer consumer, boolean cache ) {
		this.consumers[0] = consumer;
		this.cache = cache;
	}
	public void addConsumer( ImageConsumer consumer ) {
		if (this.pos < 5 ) {
			this.consumers[this.pos] = consumer;
			this.pos++;
		}
	}
	public void notifyConsumers( String name, Image image ) {
		for (int i = 0; i < this.pos; i++) {
			try {
				this.consumers[i].setImage(name, image );
			} catch (Exception e) {
				//#debug error
				Debug.debug( "Unable to notify ImageConsumer about picture [" + name + "]: " + e.getMessage(), e );
			}
		}
	}
}
