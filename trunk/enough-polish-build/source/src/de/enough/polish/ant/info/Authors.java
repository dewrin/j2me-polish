/*
 * Created on 23-Jan-2003 at 08:19:23.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.info;


import org.apache.tools.ant.BuildException;

import java.util.ArrayList;

/**
 * <p>A collection of authors.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        23-Jan-2003 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class Authors {
	
	private ArrayList authors;

	/**
	 * Creates a new authors collection.
	 */
	public Authors() {
		this.authors = new ArrayList();
	}
	
	public void addConfiguredAuthor( Author author ) {
		if (author.getName() == null) {
			throw new BuildException("The element [author] needs to have the attribute [name] defined.");
		}
		this.authors.add( author );
	}
	
	public Author[] getAuthors() {
		return (Author[]) this.authors.toArray( new Author[ this.authors.size() ] );
	}

}
