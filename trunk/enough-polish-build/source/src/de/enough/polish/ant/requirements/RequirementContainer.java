/*
 * Created on 15-Feb-2004 at 18:33:05.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.requirements;

import de.enough.polish.Capability;

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
	
	public void addConfiguredRequirement( Capability req ) {
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
