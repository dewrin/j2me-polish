/*
 * Created on 15-Feb-2004 at 18:55:33.
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

import de.enough.polish.Device;

/**
 * <p>A filter for requirements and requirement-groups which all need to be met.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        15-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class AndRelation extends RequirementContainer {
	
	/**
	 * Creates a new empty and-relation.
	 */
	public AndRelation() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ant.requirements.DeviceFilter#isMet(de.enough.polish.Device)
	 */
	public boolean isMet(Device device) {
		DeviceFilter[] filters = getFilters();
		for (int i = 0; i < filters.length; i++) {
			DeviceFilter filter = filters[i];
			if (!filter.isMet(device)) {
				return false;
			}
		}
		return true;
	}
}