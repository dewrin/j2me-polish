/*
 * Created on 11-Feb-2004 at 21:42:17.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.requirements;

import org.apache.tools.ant.BuildException;

import junit.framework.TestCase;

/**
 * <p>Tests the memory matcher.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        11-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class MemoryMatcherTest extends TestCase {

	public MemoryMatcherTest(String name) {
		super(name);
	}
	
	public void testMatch() {
		String needed = "120+kb";
		MemoryMatcher matcher = new MemoryMatcher( needed );
		assertTrue( matcher.matches("500 kb"));
		assertTrue( matcher.matches("500kb"));
		assertTrue( matcher.matches("5 mb"));
		assertTrue( matcher.matches("5mb"));
		assertTrue( matcher.matches("120 kb"));
		assertTrue( matcher.matches("120kb"));
		assertFalse( matcher.matches("120 b"));
		assertFalse( matcher.matches("120 bytes"));
		assertFalse( matcher.matches("120bytes"));
		needed = "120+ kb";
		matcher = new MemoryMatcher( needed );
		assertTrue( matcher.matches("500 kb"));
		assertTrue( matcher.matches("500kb"));
		assertTrue( matcher.matches("5 mb"));
		assertTrue( matcher.matches("5mb"));
		assertTrue( matcher.matches("120 kb"));
		assertTrue( matcher.matches("120kb"));
		assertFalse( matcher.matches("120 b"));
		assertFalse( matcher.matches("120 bytes"));
		assertFalse( matcher.matches("120bytes"));
		needed = "120 + kb";
		matcher = new MemoryMatcher( needed );
		assertTrue( matcher.matches("500 kb"));
		assertTrue( matcher.matches("500kb"));
		assertTrue( matcher.matches("5 mb"));
		assertTrue( matcher.matches("5mb"));
		assertTrue( matcher.matches("120 kb"));
		assertTrue( matcher.matches("120kb"));
		assertFalse( matcher.matches("120 b"));
		assertFalse( matcher.matches("120 bytes"));
		assertFalse( matcher.matches("120bytes"));
		assertTrue( matcher.matches("5 gb"));
		assertTrue( matcher.matches("5gb"));
		try {
			needed = "120+";
			matcher = new MemoryMatcher( needed );
			fail("MemoryMatcher should not accept the value [" + needed + "].");
		} catch (BuildException e) {
			// expected behaviour
		}
		try {
			needed = "120+kk";
			matcher = new MemoryMatcher( needed );
			fail("MemoryMatcher should not accept the value [" + needed + "].");
		} catch (BuildException e) {
			// expected behaviour
		}
	}
	
	public void testGetBytes() {
		assertEquals( 1L, MemoryMatcher.getBytes("1b"));
		assertEquals( 1L, MemoryMatcher.getBytes("1 b"));
		assertEquals( 1L, MemoryMatcher.getBytes("1+ b"));
		assertEquals( 1L, MemoryMatcher.getBytes("1bytes"));
		assertEquals( 1L, MemoryMatcher.getBytes("1 bytes"));
		assertEquals( 1L, MemoryMatcher.getBytes("1+ bytes"));
		assertEquals( 23L, MemoryMatcher.getBytes("23b"));
		assertEquals( 23L, MemoryMatcher.getBytes("23 b"));
		assertEquals( 23L, MemoryMatcher.getBytes("23+ b"));
		assertEquals( 1024L, MemoryMatcher.getBytes("1kb"));
		assertEquals( 1024L, MemoryMatcher.getBytes("1 kb"));
		assertEquals( 1024L, MemoryMatcher.getBytes("1+ kb"));
		assertEquals( 23 * 1024L, MemoryMatcher.getBytes("23kb"));
		assertEquals( 23 * 1024L, MemoryMatcher.getBytes("23 kb"));
		assertEquals( 23 * 1024L, MemoryMatcher.getBytes("23+ kb"));
		assertEquals( 1024L * 1024L, MemoryMatcher.getBytes("1mb"));
		assertEquals( 1024L * 1024L, MemoryMatcher.getBytes("1 mb"));
		assertEquals( 1024L * 1024L, MemoryMatcher.getBytes("1+ mb"));
		assertEquals( 23 * 1024L * 1024L, MemoryMatcher.getBytes("23mb"));
		assertEquals( 23 * 1024L * 1024L, MemoryMatcher.getBytes("23 mb"));
		assertEquals( 23 * 1024L * 1024L, MemoryMatcher.getBytes("23+ mb"));
		assertEquals( 1024L * 1024L * 1024L, MemoryMatcher.getBytes("1gb"));
		assertEquals( 1024L * 1024L * 1024L, MemoryMatcher.getBytes("1 gb"));
		assertEquals( 1024L * 1024L * 1024L, MemoryMatcher.getBytes("1+ gb"));
		assertEquals( 23 * 1024L * 1024L * 1024L, MemoryMatcher.getBytes("23gb"));
		assertEquals( 23 * 1024L * 1024L * 1024L, MemoryMatcher.getBytes("23 gb"));
		assertEquals( 23 * 1024L * 1024L * 1024L, MemoryMatcher.getBytes("23+ gb"));
	}

}
