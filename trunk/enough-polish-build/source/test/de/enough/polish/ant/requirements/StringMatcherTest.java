/*
 * Created on 11-Feb-2004 at 22:49:15.
 *
 * Copyright (c) 2004 Robert Virkus / enough software
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
 * www.enough.de/j2mepolish for details.
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
