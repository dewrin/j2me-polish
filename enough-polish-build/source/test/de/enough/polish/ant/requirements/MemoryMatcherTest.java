/*
 * Created on 11-Feb-2004 at 21:42:17.
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
package de.enough.polish.ant.requirements;

import org.apache.tools.ant.BuildException;

import junit.framework.TestCase;

/**
 * <p>Tests the memory matcher.</p>
 *
 * <p>copyright Enough Software 2004</p>
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
		needed = "0.5+ mb";
		matcher = new MemoryMatcher( needed );
		assertTrue( matcher.matches("512 kb"));
		assertTrue( matcher.matches("512kb"));
		assertTrue( matcher.matches("0.5 mb"));
		assertTrue( matcher.matches("0.5mb"));
		assertTrue( matcher.matches("1.2 mb"));
		assertTrue( matcher.matches("1.2mb"));
		assertFalse( matcher.matches("2048 b"));
		assertFalse( matcher.matches("2048 bytes"));
		assertFalse( matcher.matches("2048bytes"));
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
