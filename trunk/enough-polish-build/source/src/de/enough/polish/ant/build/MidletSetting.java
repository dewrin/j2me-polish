/*
 * Created on 22-Jan-2003 at 14:31:40.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.build;

import org.apache.tools.ant.BuildException;

import java.util.*;

/**
 * <p>Manages all midlets of a specific project.</p>
 *
 * <p>copyright enough software 2004</p>
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
	 * <p>copyright enough software 2004</p>
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
