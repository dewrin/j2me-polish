/*
 * Created on 11-Feb-2004 at 22:49:15.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.requirements;

import junit.framework.TestCase;

/**
 * <p>Tests the string matcher calss.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        11-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class StringMatcherTest extends TestCase {

	public StringMatcherTest(String name) {
		super(name);
	}
	
	public void testMatches() {
		String needed = "mmapi";
		StringMatcher matcher = new StringMatcher( needed );
		assertTrue( matcher.matches("someapi, anotherapi, mmapi, weirdapi"));
		assertTrue( matcher.matches("someapi,anotherapi,mmapi,weirdapi"));
		assertTrue( matcher.matches("someapi,anotherapi,mmapi"));
		assertTrue( matcher.matches("mmapi,someapi,anotherapi,weirdapi"));
		assertTrue( matcher.matches(" mmapi, someapi, anotherapi, weirdapi"));
		assertFalse( matcher.matches("someapi,anotherapi,weirdapi"));
		
		matcher = new StringMatcher( new String[]{"mmapi", "nokia-ui"}, false );
		assertTrue( matcher.matches("someapi, anotherapi, mmapi, weirdapi, nokia-ui"));
		assertTrue( matcher.matches("someapi,anotherapi,mmapi,weirdapi,nokia-ui"));
		assertFalse( matcher.matches("someapi,anotherapi,mmapi,weirdapi"));
		assertFalse( matcher.matches("someapi,anotherapi,nokia-ui,weirdapi"));
		
		matcher = new StringMatcher( new String[]{"mmapi", "nokia-ui"}, true );
		assertTrue( matcher.matches("someapi, anotherapi, mmapi, weirdapi, nokia-ui"));
		assertTrue( matcher.matches("someapi,anotherapi,mmapi,weirdapi,nokia-ui"));
		assertTrue( matcher.matches("someapi,anotherapi,mmapi,weirdapi"));
		assertTrue( matcher.matches("someapi,anotherapi,nokia-ui,weirdapi"));
	}

}
