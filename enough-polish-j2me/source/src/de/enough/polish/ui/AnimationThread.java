//#condition polish.usePolishGui
/*
 * Created on 15-Mar-2004 at 10:52:57.
 *
 * Copyright (c) 2004 Robert Virkus / enough software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * www.enough.de/j2mepolish for details.
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
				Screen screen = StyleSheet.currentScreen;
				if (screen != null && screen.isShown()) {
					if (screen.animate()) {
						sleeptime = ANIMATION_INTERVAL;
						animationCounter = 0;
					} else {
						animationCounter++;
						if (animationCounter > 10) {
							sleeptime = 1000;
							animationCounter = 0;
						}
					}
				} else {
					sleeptime = 1000;
				}
			} catch (InterruptedException e) {
				// ignore
			} catch (Exception e) {
				//#debug error
				Debug.debug("unable to animate screen", e );
			}
		}
	}
}
