/*
 * Created on 18-Jan-2003 at 19:41:59.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
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
		lines = new String[] { "a", "b", "c" };
		list.insert( lines );
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
