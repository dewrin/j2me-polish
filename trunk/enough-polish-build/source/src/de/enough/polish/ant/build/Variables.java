/*
 * Created on 23-Jan-2004 at 23:14:37.
 *
 * Copyright (c) 2004 Robert Virkus / enough software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * www.enough.de/j2mepolish for details.
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
