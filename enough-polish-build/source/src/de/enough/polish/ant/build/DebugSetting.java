/*
 * Created on 21-Jan-2003 at 15:24:03.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.build;

import de.enough.polish.ant.ConditionalElement;

import org.apache.tools.ant.BuildException;

import java.util.ArrayList;


/**
 * <p>Represents the debug settings.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        21-Jan-2003 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class DebugSetting extends ConditionalElement {
	
	private boolean enable;
	private boolean verbose;
	private boolean visual;
	private String level;
	private ArrayList filters;

	/**
	 * Creates a new empty debug-setting.
	 */
	public DebugSetting() {
		this.filters = new ArrayList();
		this.level = "debug";
	}
	

	/**
	 * Determines whether debugging is enabled.
	 * When this is not the case, no debugging information will be
	 * included at all.
	 * 
	 * @return true when debugging is enabled.
	 */
	public boolean isEnabled() {
		return this.enable;
	}

	/**
	 * Sets the debugging mode.
	 * 
	 * @param enable true when the debugging mode is enabled.
	 */
	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	/**
	 * Gets the general debugging level.
	 * 
	 * @return the general debug level for classes which have no explicit setting.
	 */
	public String getLevel() {
		return this.level;
	}

	/**
	 * Sets the general debugging level.
	 * 
	 * @param level the general debug level for classes which have no explicit setting, 
	 * 		e.g. "debug", "info", "warn", "error" or user-defined.
	 */
	public void setLevel(String level) {
		this.level = level;
	}

	/**
	 * Determines whether the verbose mode is enabled.
	 * 
	 * @return true when before each debugging the time, class-name and source-location should
	 * 			be printed out.
	 */
	public boolean isVerbose() {
		return this.verbose;
	}

	/**
	 * Sets the verbose mode.
	 * 
	 * @param verbose true when before each debugging the time, class-name and source-location should
	 * 			be printed out.
	 */
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/**
	 * Determines if the visual debugging mode is enabled.
	 * When this mode is enabled, the preprocessing-symbol "polish.debug.visual"
	 * is defined.
	 *  
	 * @return true when the visual debugging mode is enabled.
	 */
	public boolean isVisual() {
		return this.visual;
	}

	/**
	 * Sets the visual debugging mode.
	 * 
	 * @param visual true when the visual debugging mode is enabled.
	 */
	public void setVisual(boolean visual) {
		this.visual = visual;
	}
	
	public void addConfiguredFilter( Filter filter ) {
		if (filter.getPattern() == null) {
			throw new BuildException("Error in debug settings: the element [filter] needs to define the attribute [pattern].");
		}
		if (filter.getLevel() == null) {
			throw new BuildException("Error in debug settings: the element [filter] needs to define the attribute [level].");
		}
		this.filters.add( filter );
	}


	/**
	 * @return an array of all debug-filters.
	 */
	public Filter[] getFilters() {
		return (Filter[]) this.filters.toArray( new Filter[ this.filters.size() ] );
	}

}
