/*
 * Created on 21-Jan-2003 at 15:44:47.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.build;

/**
 * <p></p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        21-Jan-2003 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class Filter {
	String pattern;
	String level;

	/**
	 * 
	 */
	public Filter() {
		super();
		// TODO enough implement Filter
	}

	/**
	 * @return Returns the level.
	 */
	public String getLevel() {
		return this.level;
	}

	/**
	 * @param level The level to set.
	 */
	public void setLevel(String level) {
		this.level = level;
	}

	/**
	 * @return Returns the pattern.
	 */
	public String getPattern() {
		return this.pattern;
	}

	/**
	 * @param pattern The pattern to set.
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

}
