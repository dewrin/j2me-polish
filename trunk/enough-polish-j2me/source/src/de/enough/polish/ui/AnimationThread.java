//#condition polish.usePolishGui
/*
 * Created on 15-Mar-2004 at 10:52:57.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ui;

import de.enough.polish.util.Debug;

/**
 * <p>Is used to animate Backgrounds, Borders and Items.</p>
 * <p>
 * 	In either devices.xml, vendors.xml, groups.xml, device.xml or settings.xml
 *  the animation-interval can be specified.
 *  Example:
 *  <pre>
 *  <variables>
 *		<variable name="polish.animationInterval" value="200" />
 *	</variables>
 * 	</pre> 
 *  sets the interval to 200 ms. When not specified, the default interval
 *  of 100 ms will be used. 
 * </p>
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        15-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class AnimationThread extends Thread {
	
	//#ifdef polish.animationInterval:defined
	//#=public final static int ANIMATION_INTERVAL = ${polish.animationInterval};
	//#else
	public final static int ANIMATION_INTERVAL = 100;
	//#endif
	
	/**
	 * Creates a new animation thread.
	 */
	public AnimationThread() {
		super();
	}
	
	/**
	 * Animates the current screen.
	 */
	public void run() {
		long sleeptime = ANIMATION_INTERVAL;
		int animationCounter = 0;
		while ( true ) {
			try {
				Thread.sleep(sleeptime);
				if (StyleSheet.currentScreen != null) {
					if (StyleSheet.currentScreen.animate()) {
						sleeptime = ANIMATION_INTERVAL;
						animationCounter = 0;
					} else {
						animationCounter++;
						if (animationCounter > 10) {
							sleeptime = 1000;
							animationCounter = 0;
						}
					}
				}
			} catch (InterruptedException e) {
				// ignore
			} catch (Exception e) {
				//#debug error
				Debug.debug("unable to animate: " + e.getMessage(), e );
			}
		}
	}
}
