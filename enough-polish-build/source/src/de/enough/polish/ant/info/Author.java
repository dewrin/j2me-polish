/*
 * Created on 23-Jan-2003 at 08:17:15.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.info;

/**
 * <p>Represents an author of a project.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        23-Jan-2003 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class Author {
	
	private String name;
	private String email;

	/**
	 * Creates a new author
	 */
	public Author() {
		// initialisation is done in the setter methods
	}

	/**
	 * @return Returns the email.
	 */
	public String getEmail() {
		return this.email;
	}

	/**
	 * @param email The email to set.
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

}
