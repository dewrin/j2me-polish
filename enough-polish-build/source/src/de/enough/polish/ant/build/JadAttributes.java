/*
 * Created on Jun 18, 2004
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
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.ant.build;

import java.util.ArrayList;

import org.apache.tools.ant.BuildException;

/**
 * <p>Represents user-defined attributes for the JAD and the MANIFEST</p>
 * 
 * @author robert virkus, j2mepolish@enough.de
 */
public class JadAttributes {

	private ArrayList list;

	/**
	 * Creates a new list 
	 */
	public JadAttributes() {
		this.list = new ArrayList();
	}
	
	public void addAttribute( Attribute attribute ) {
		if (attribute.getName() == null) {
			throw new BuildException("Please check your <jad> definition, each attribute needs to have the attribute [name]");
		}
		if (attribute.getValue() == null) {
			throw new BuildException("Please check your <jad> definition, each attribute needs to have the attribute [value]");
		}
		this.list.add( attribute );
	}
	
	public Attribute[] getAttributes(){
		return (Attribute[]) this.list.toArray( new Attribute[ this.list.size() ] );
	}

}
