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
	private boolean includeAntProperties;

	/**
	 * Creates a new list of variables.
	 */
	public Variables() {
		this.variables = new ArrayList();
	}
	
	public void addConfiguredVariable( Variable var ) {
		if (var.getName() == null) {
			throw new BuildException("Please check your variable definition, each variable needs to have the attribute [name]");
		}
		if (var.getValue() == null) {
			throw new BuildException("Please check your variable definition, each variable needs to have the attribute [value]");
		}
		this.variables.add( var );
	}
	
	public Variable[] getVariables() {
		return (Variable[]) this.variables.toArray( new Variable[ this.variables.size() ] );
	}

	/**
	 * @return Returns the includeAntProperties.
	 */
	public boolean includeAntProperties() {
		return this.includeAntProperties;
	}
	/**
	 * @param includeAntProperties The includeAntProperties to set.
	 */
	public void setIncludeAntProperties(boolean includeAntProperties) {
		this.includeAntProperties = includeAntProperties;
	}
}
