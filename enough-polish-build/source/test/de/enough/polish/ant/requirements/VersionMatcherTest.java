/*
 * Created on 11-Feb-2004 at 23:30:01.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.requirements;

import org.apache.tools.ant.BuildException;

import junit.framework.TestCase;

/**
 * <p>Tests the version matcher class.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        11-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class VersionMatcherTest 
extends TestCase {

	public VersionMatcherTest( String name ) {
		super( name );
	}
	
	public void testMatches() throws BuildException {
		String needed = "1.2.5+";
		VersionMatcher matcher = new VersionMatcher( needed );
		assertFalse( matcher.matches( "1.2.4" ) );
		assertFalse( matcher.matches( "1.2" ) );
		assertTrue( matcher.matches( "1.2.5" ) );
		assertTrue( matcher.matches( "1.2.6" ) );
		assertFalse( matcher.matches( "1.3.0" ) );
		assertFalse( matcher.matches( "2.2.4" ) );
		
		needed = "1.2.3";
		matcher = new VersionMatcher( needed );
		assertFalse( matcher.matches( "1.1.9" ) );
		assertFalse( matcher.matches( "1.2" ) );
		assertTrue( matcher.matches( "1.2.3" ) );
		assertFalse( matcher.matches( "1.2.6" ) );
		assertFalse( matcher.matches( "1.3.0" ) );
		assertFalse( matcher.matches( "2.2.4" ) );
		
		needed = "1.2+";
		matcher = new VersionMatcher( needed );
		assertFalse( matcher.matches( "1.1.9" ) );
		assertTrue( matcher.matches( "1.2" ) );
		assertTrue( matcher.matches( "1.2.5" ) );
		assertTrue( matcher.matches( "1.2.6" ) );
		assertTrue( matcher.matches( "1.3.0" ) );
		assertFalse( matcher.matches( "2.2.4" ) );
		
		needed = "1.2+.5+";
		matcher = new VersionMatcher( needed );
		assertFalse( matcher.matches( "1.2" ) );
		assertFalse( matcher.matches( "1.2.4" ) );
		assertTrue( matcher.matches( "1.2.5" ) );
		assertTrue( matcher.matches( "1.2.6" ) );
		assertTrue( matcher.matches( "1.3.0" ) );
		assertFalse( matcher.matches( "2.2.4" ) );
		
		needed = "1+.2+.5+";
		matcher = new VersionMatcher( needed );
		assertFalse( matcher.matches( "1.2" ) );
		assertFalse( matcher.matches( "1.2.4" ) );
		assertTrue( matcher.matches( "1.2.5" ) );
		assertTrue( matcher.matches( "1.2.6" ) );
		assertTrue( matcher.matches( "1.3.0" ) );
		assertTrue( matcher.matches( "2.0.0" ) );
		assertTrue( matcher.matches( "2.2.4" ) );
	}

}
