/*
 * Created on 23-Jan-2004 at 23:52:57.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.build;

/**
 * <p>Manages the source-directories of a J2ME project.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        23-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class Source {
	
	private String url;
	private String polish;

	/**
	 * Creates a new source
	 */
	public Source() {
		// initialisation is done in the setter methods
	}

	/**
	 * @return the path to the polish-source
	 */
	public String getPolish() {
		return this.polish;
	}

	/**
	 * @param polish the path to the souce of the polish project.
	 */
	public void setPolish(String polish) {
		this.polish = polish;
	}

	/**
	 * @return Returns the source directories.
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * @param url The url of the source-directory(ies) to set. Different directories are seperated either by ':' or by ';'
	 */
	public void setUrl(String url) {
		this.url = url;
	}

}
