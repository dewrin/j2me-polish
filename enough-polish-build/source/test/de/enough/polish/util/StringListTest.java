/*
 * Created on 18-Jan-2003 at 19:41:59.
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
package de.enough.polish.util;

import junit.framework.TestCase;

/**
 * <p>Tests the StringList class.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        18-Jan-2003 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class StringListTest extends TestCase {


	public StringListTest(String name) {
		super(name);
	}
	
	public void testInsert() {
		String[] lines = new String[]{
				"one", "two", "three", "four"
		};
		StringList list = new StringList( lines );
		assertTrue( list.next() );
		assertTrue( list.next() );
		assertEquals( "two", list.getCurrent() );
		assertEquals( 0, list.getNumberOfInsertedLines() );
		lines = new String[] { "a", "b", "c" };
		list.insert( lines );
		assertEquals( 3, list.getNumberOfInsertedLines() );
		assertTrue( list.next() );
		assertEquals( "a", list.getCurrent() );
		assertTrue( list.next() );
		assertEquals( "b", list.getCurrent() );
		assertTrue( list.next() );
		assertEquals( "c", list.getCurrent() );
		assertTrue( list.next() );
		assertEquals( "three", list.getCurrent() );
		assertTrue( list.next() );
		assertEquals( "four", list.getCurrent() );
		assertFalse( list.next() );
		
		lines = list.getArray();
		assertEquals( 7, lines.length );
		assertEquals( "one", lines[0]);
		assertEquals( "two", lines[1]);
		assertEquals( "a", lines[2]);
		assertEquals( "b", lines[3]);
		assertEquals( "c", lines[4]);
		assertEquals( "three", lines[5]);
		assertEquals( "four", lines[6]);
	}

}
