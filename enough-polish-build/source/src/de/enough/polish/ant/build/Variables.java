/*
 * Created on 23-Jan-2004 at 23:14:37.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.build;


import de.enough.polish.*;

import org.apache.tools.ant.BuildException;

import java.util.ArrayList;

/**
 * <p>Manages a list of variables.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        23-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class Variables {
	
	private ArrayList variables;

	/**
	 * Creates a new list of variables.
	 */
	public Variables() {
		this.variables = new ArrayList();
	}
	
	public void addConfiguredVariable( Capability var ) {
		if (var.getName() == null) {
			throw new BuildException("Please check your variable definition, each variable needs to have the attribute [name]");
		}
		if (var.getValue() == null) {
			throw new BuildException("Please check your variable definition, each variable needs to have the attribute [value]");
		}
		this.variables.add( var );
	}
	
	public Capability[] getVariables() {
		return (Capability[]) this.variables.toArray( new Capability[ this.variables.size() ] );
	}

}
