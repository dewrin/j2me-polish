/*
 * Created on 20-Feb-2004 at 21:15:33.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant;

import de.enough.polish.Variable;

import java.util.*;
import java.util.HashMap;
import java.util.Iterator;

/**
 * <p>Represents a Java Application Description file.</p>
 *
 * <p>copyright enough software 2004</p>
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
