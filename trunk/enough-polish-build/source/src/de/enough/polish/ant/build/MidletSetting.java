/*
 * Created on 22-Jan-2003 at 14:31:40.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.build;

import org.apache.tools.ant.BuildException;

import java.util.ArrayList;

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
	
	public Midlet[] getMidlets() {
		return (Midlet[]) this.midlets.toArray( new Midlet[ this.midlets.size() ] );
	}

}
