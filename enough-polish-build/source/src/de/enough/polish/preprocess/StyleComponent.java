/*
 * Created on 01-Mar-2004 at 15:28:21.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.preprocess;

/**
 * <p>Provides basic features of Background, Border and Font.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        01-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public abstract class StyleComponent {
	
	protected String name;
	
	/**
	 * Creates a new style component.
	 * 
	 * @param name the name of this component
	 */
	public StyleComponent( String name ) {
		this.name = name;
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
	
	/**
	 * Retrieves the source code for this component.
	 * 
	 * @return the source code for this component.
	 */
	public abstract String[] getSourceCode();

}
