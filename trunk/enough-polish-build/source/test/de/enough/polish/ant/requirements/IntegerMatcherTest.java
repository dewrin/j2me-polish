/*
 * Created on 11-Feb-2004 at 22:43:10.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.requirements;

import junit.framework.TestCase;

/**
 * <p>Tests the integer matcher.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        11-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class IntegerMatcherTest extends TestCase {

	public IntegerMatcherTest(String name) {
		super(name);
	}
	
	public void testMatches() {
		String needed = "120+";
		IntegerMatcher matcher = new IntegerMatcher( needed );
		assertTrue( matcher.matches( "120" ) );
		assertTrue( matcher.matches( "400" ) );
		assertFalse( matcher.matches( "119" ) );
		needed = "120 +";
		matcher = new IntegerMatcher( needed );
		assertTrue( matcher.matches( "120" ) );
		assertTrue( matcher.matches( "400" ) );
		assertFalse( matcher.matches( "119" ) );
		needed = "120";
		matcher = new IntegerMatcher( needed );
		assertTrue( matcher.matches( "120" ) );
		assertFalse( matcher.matches( "400" ) );
		assertFalse( matcher.matches( "119" ) );
		assertFalse( matcher.matches( "121" ) );
	}

}
