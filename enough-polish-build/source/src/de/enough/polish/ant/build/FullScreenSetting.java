/*
 * Created on 22-Jan-2003 at 15:37:27.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.build;

/**
 * <p>Represents the settings for the use of full-screen-classes.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        22-Jan-2003 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class FullScreenSetting {

	private boolean menu;
	private boolean enable;

	/**
	 * Creates a new full-screen setting
	 */
	public FullScreenSetting() {
		// initialisation is done via the setter methods
	}
	
	public void setEnable( boolean enable ) {
		this.enable = enable;
	}
	
	public void setMenu( boolean menu ) {
		this.menu = menu;
	}

	/**
	 * @return Returns the enable.
	 */
	public boolean isEnabled() {
		return this.enable;
	}

	/**
	 * @return Returns the menu.
	 */
	public boolean isMenu() {
		return this.menu;
	}

}
