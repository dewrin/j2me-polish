/*
 * Created on 11-Feb-2004 at 23:30:01.
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
