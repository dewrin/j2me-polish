/*
 * Created on 03-Jan-2004 at 18:12:09.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.util;

import junit.framework.TestCase;

/**
 * <p>Tests the ArrayList</p>
 *
 * @author Robert Virkus, robert@enough.de
 * <pre>
 * history
 *        03-Jan-2004 - rob creation
 * </pre>
 */
public class ArrayListTest extends TestCase {
	
	Object o1 = "1"; Object o2 = "2"; Object o3 = "3"; Object o4 = "4"; Object o5 = "5"; Object o6 = "6";
	Object[] array1 = new Object[] { this.o1, this.o2, this.o3, this.o4, this.o5, this.o6 };
	
	public ArrayListTest() {
		super();
	}
	

	public ArrayListTest(String name) {
		super(name);
	}
	
	public void testAdd() {
		ArrayList list = new ArrayList();
		list.add( this.o1 );
		list.add( this.o2 );
		list.add( this.o3 );
		Object[] store = list.toArray();
		for (int i = 0; i < store.length; i++) {
			assertEquals( store[i], this.array1[i] );
		}
	}
	
	public void testRemove() {
		ArrayList list = new ArrayList( 3 );
		list.add( this.o1 );
		list.add( this.o2 );
		list.add( this.o3 );
		// illegal remove:
		try {
			list.remove( 3 );
			fail("ArrayList.remove(3) should fail when the list has only [" + list.size() +"] entries.");
		} catch (IndexOutOfBoundsException e) {
			// okay, expected behaviour!
		}
		try {
			list.remove( -1 );
			fail("ArrayList.remove(-1) should fail.");
		} catch (IndexOutOfBoundsException e) {
			// okay, expected behaviour!
		}
		// now remove the third element:
		Object o = list.remove( 2 );
		assertEquals( this.o3, o );
		list.add(this.o3);
		boolean success = list.remove( this.o3 );
		assertEquals( true, success );
		list.add( this.o3 );
		Object[] store = list.toArray();
		for (int i = 0; i < store.length; i++) {
			assertEquals( store[i], this.array1[i] );
		}
	}

}
