/*
 * Created on 11-Feb-2004 at 22:43:10.
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

import junit.framework.TestCase;

/**
 * <p>Tests the integer matcher.</p>
 *
 * <p>copyright Enough Software 2004</p>
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
