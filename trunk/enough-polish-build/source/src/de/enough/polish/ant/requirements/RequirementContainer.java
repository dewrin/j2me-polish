/*
 * Created on 15-Feb-2004 at 18:33:05.
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
package de.enough.polish.ant.requirements;

import de.enough.polish.Variable;

import java.util.ArrayList;

/**
 * <p>Represents an "AND", "OR", "XOR" and "NOT" relation between several requirements.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        15-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public abstract class RequirementContainer
implements DeviceFilter
{
	
	private ArrayList filters;

	/**
	 * Creates a new empty container
	 */
	public RequirementContainer() {
		this.filters = new ArrayList();
	}
	
	public void addConfiguredRequirement( Variable req ) {
		String name = req.getName(); 
		String value = req.getValue();
		String type = req.getType();
		Requirement requirement = Requirement.getInstance( name, value, type );
		this.filters.add( requirement );
	}
	
	public void addConfiguredAnd( AndRelation andRelation ) {
		this.filters.add( andRelation );
	}
	
	public void addConfiguredOr( OrRelation orRelation ) {
		this.filters.add( orRelation );
	}
	
	public void addConfiguredNot( NotRelation notRelation ) {
		this.filters.add( notRelation );
	}
	
	public void addConfiguredNand( NotRelation notRelation ) {
		this.filters.add( notRelation );
	}
	
	public void addConfiguredXor( XorRelation xorRelation ) {
		this.filters.add( xorRelation );
	}
	
	public DeviceFilter[] getFilters() {
		return (DeviceFilter[]) this.filters.toArray( new DeviceFilter[this.filters.size()] );
	}
	
	
}
