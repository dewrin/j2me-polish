/*
 * Created on 22-Jan-2003 at 14:31:40.
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
package de.enough.polish.ant.build;

import org.apache.tools.ant.BuildException;

import java.util.*;

/**
 * <p>Manages all midlets of a specific project.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        22-Jan-2003 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class MidletSetting {
	
	private ArrayList midlets;

	public MidletSetting() {
		this.midlets = new ArrayList();
	}
	
	public void addConfiguredMidlet( Midlet midlet ) {
		if (midlet.getNumber() == 0) {
			midlet.setNumber( this.midlets.size() + 1 );
		}
		if (midlet.getClassName() == null) {
			throw new BuildException("The <midlet> elements needs to have the attribute [class], which defines the name of a MIDlet-class!");
		}
		this.midlets.add( midlet );
	}
	
	/**
	 * Gets all the defined midlets in the correct order.
	 * 
	 * @return All midlets in the correct order, that means Midlet-1 is the first element of the array. 
	 */
	public Midlet[] getMidlets() {
		Midlet[] midletsArray = (Midlet[]) this.midlets.toArray( new Midlet[ this.midlets.size() ] );
		Arrays.sort( midletsArray, new MidletComparator() );
		return midletsArray;
	}
	
	/**
	 * <p>Is used to sort midlets.</p>
	 * <p>copyright Enough Software 2004</p>
	 * <pre>
	 * history
	 *        18-Feb-2004 - rob creation
	 * </pre>
	 * @author Robert Virkus, robert@enough.de
	 */
	class MidletComparator implements Comparator {

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Object one, Object two) {
			if ( ! ( one instanceof Midlet && two instanceof Midlet )) {
				return 0;
			}
			Midlet midlet1 = (Midlet) one;
			Midlet midlet2 = (Midlet) two;
			return midlet1.getNumber() - midlet2.getNumber();
		}
		
	}

}
