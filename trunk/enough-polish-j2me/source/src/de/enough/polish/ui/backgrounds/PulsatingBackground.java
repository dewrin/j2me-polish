//#condition polish.usePolishGui
/*
 * Created on 15-Mar-2004 at 10:57:00.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ui.backgrounds;

import de.enough.polish.ui.Background;

import javax.microedition.lcdui.Graphics;

/**
 * <p>Paints an animated background, which colors change.</p>
  * <p>Following CSS parameters are supported:
 * <ul>
 * 		<li><b>start-color</b>: The color with which the animation is started.</li>
 * 		<li><b>end-color</b>: The color with which the animation is stopped.</li>
 * 		<li><b>repeat</b>: true when the animation should run without stopping. 
 * 			Is enabled by default.</li>
 * 		<li><b>back-and-forth</b>: true when the animation should run backward when 
 * 			the end-color is reached. Is enabled by default</li>
 * 		<li><b></b>: </li>
 * 		<li><b></b>: </li>
 * </ul>
*
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        15-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class PulsatingBackground extends Background {
	private int currentColor;
	private int[] colors;
	private int currentStep;
	private boolean repeat;
	private boolean backAndForth;
	private boolean directionUp = true;
	private boolean animationStopped;
	

	/**
	 * @param colors
	 * @param repeat
	 * @param backAndForth
	 */
	public PulsatingBackground(int[] colors, boolean repeat,
			boolean backAndForth) 
	{
		super();
		this.currentColor = colors[0];
		this.colors = colors;
		this.repeat = repeat;
		this.backAndForth = backAndForth;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		g.setColor( this.currentColor );
		g.fillRect(x, y, width + 1, height + 1);
	}
	
	public boolean animate() {
		if (this.animationStopped) {
			return false;
		}
		if (this.backAndForth) {
			if (this.directionUp) {
				this.currentStep++;
				if (this.currentStep == this.colors.length ) {
					this.currentStep--;
					this.directionUp = false;
				}
			} else {
				this.currentStep--;
				if (this.currentStep == -1) {
					this.currentStep = 0;
					if ( this.repeat ) {
						this.directionUp = true;
					} else {
						this.animationStopped = true;
					}
				}
			}
		} else {
			this.currentStep++;
			if (this.currentStep == this.colors.length ) {
				if (this.repeat) {
					this.currentStep = 0;
				} else {
					this.currentStep--;
					this.animationStopped = true;
				}
			}
			
		}
		this.currentColor = this.colors[ this.currentStep ];
		return true;
	}

}
