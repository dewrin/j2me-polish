/*
 * Created on 15-Jan-2004 at 15:57:31.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish;

import de.enough.polish.preprocess.*;


/**
 * <p>Represents a J2ME project defined by the ant settings in the build.xml file.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        15-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class Project extends PolishComponent {
	
	private boolean usesPolishGui;
	private boolean isDebugEnabled;
	private DebugManager debugManager;

	/**
	 * Creates a new project.
	 * 
	 * @param usesPolishGui true when this project uses the polish GUI framework.
	 * @param isDebugEnabled true when debugging is enabled at all.
	 * @param debugManager manages specific debugging settings.
	 */
	public Project( boolean usesPolishGui, boolean isDebugEnabled, DebugManager debugManager ) {
		this.usesPolishGui = usesPolishGui;
		this.isDebugEnabled = isDebugEnabled;
		this.debugManager = debugManager;
	}
	
	/**
	 * Determines whether this project  uses the polish GUI-framework.
	 * 
	 * @return true when this project uses the polish GUI-framework.
	 */
	public boolean usesPolishGui() {
		return this.usesPolishGui;
	}
	
	/**
	 * Retrieves the debug manager which is responsible for specific debugging settings.
	 * 
	 * @return the debug manager.
	 */
	public DebugManager getDebugManager() {
		return this.debugManager;
	}

	/**
	 * Determines whether debugging is allowed.
	 * 
	 * @return true when debugging is enabled at all.
	 */
	public boolean isDebugEnabled() {
		return this.isDebugEnabled;
	}


}
