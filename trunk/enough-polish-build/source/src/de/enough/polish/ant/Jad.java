/*
 * Created on 20-Feb-2004 at 21:15:33.
 *
 * Copyright (c) 2004 Robert Virkus / Enough Software
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
package de.enough.polish.ant;

import de.enough.polish.Variable;

import java.util.*;
import java.util.HashMap;
import java.util.Iterator;

/**
 * <p>Represents a Java Application Description file.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        20-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class Jad {
	
	private HashMap attributes;

	/**
	 * Creates a new empty JAD
	 */
	public Jad() {
		this.attributes = new HashMap();
	}
	
	public void addAttribute( String name, String value ) {
		if (!name.endsWith(":")) {
			name += ':';
		}
		this.attributes.put( name, value );
	}
	
	public String[] getContent() {
		String[] lines = new String[ this.attributes.size() ];
		Set set = this.attributes.keySet();
		int i = 0;
		for (Iterator iter = set.iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			lines[i] = key + " " + this.attributes.get( key );
			i++;
		}
		return lines;
	}

	/**
	 * Adds several variables to this JAD file.
	 * 
	 * @param vars The variables which should be added.
	 */
	public void addAttributes(Variable[] vars) {
		for (int i = 0; i < vars.length; i++) {
			Variable var = vars[i];
			addAttribute( var.getName(), var.getValue() );
		}
	}
}
