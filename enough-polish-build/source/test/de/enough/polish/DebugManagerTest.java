/*
 * Created on 19-Jan-2003 at 22:04:53.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish;

import de.enough.polish.preprocess.PreprocessException;

import junit.framework.TestCase;

/**
 * <p>Tests the debug manager class.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        19-Jan-2003 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class DebugManagerTest extends TestCase {

	public DebugManagerTest(String name) {
		super(name);
	}
	
	public void testAdding() throws PreprocessException {
		DebugManager manager = new DebugManager( "error", false );
		manager.addDebugSetting( "com.company.package.*", "info" );
		manager.addDebugSetting( "com.company.package.MyClass", "debug" );
		// add a user-defined setting:
		manager.addDebugSetting( "com.company.package.MyOtherClass", "visual" );
		
		assertEquals( DebugManager.DEBUG, manager.getLevelOrder("debug"));
		assertEquals( DebugManager.INFO, manager.getLevelOrder("info"));
		assertEquals( DebugManager.WARN, manager.getLevelOrder("warn"));
		assertEquals( DebugManager.WARN, manager.getLevelOrder("warning"));
		assertEquals( DebugManager.ERROR, manager.getLevelOrder("error"));
		assertEquals( DebugManager.FATAL, manager.getLevelOrder("fatal"));
		assertEquals( DebugManager.USER_DEFINED, manager.getLevelOrder("visual"));
		assertEquals( DebugManager.UNDEFINED, manager.getLevelOrder("notdefined"));
		
		assertEquals( DebugManager.INFO, manager.getClassLevel("com.company.package.SomeClass"));
		assertTrue( manager.isDebugEnabled("com.company.package.SomeClass", "info"));
		assertTrue( manager.isDebugEnabled("com.company.package.SomeClass", "warn"));
		assertTrue( manager.isDebugEnabled("com.company.package.SomeClass", "warning"));
		assertTrue( manager.isDebugEnabled("com.company.package.SomeClass", "error"));
		assertTrue( manager.isDebugEnabled("com.company.package.SomeClass", "fatal"));
		assertTrue( manager.isDebugEnabled("com.company.package.SomeClass", "visual"));
		assertFalse( manager.isDebugEnabled("com.company.package.SomeClass", "debug"));
		assertFalse( manager.isDebugEnabled("com.company.package.SomeClass", "notdefined"));
		assertFalse( manager.isDebugEnabled("com.company.package.SomeClass", "infoX"));

		assertEquals( DebugManager.DEBUG, manager.getClassLevel("com.company.package.MyClass"));
		assertTrue( manager.isDebugEnabled("com.company.package.MyClass", "info"));
		assertTrue( manager.isDebugEnabled("com.company.package.MyClass", "warn"));
		assertTrue( manager.isDebugEnabled("com.company.package.MyClass", "warning"));
		assertTrue( manager.isDebugEnabled("com.company.package.MyClass", "error"));
		assertTrue( manager.isDebugEnabled("com.company.package.MyClass", "fatal"));
		assertTrue( manager.isDebugEnabled("com.company.package.MyClass", "visual"));
		assertTrue( manager.isDebugEnabled("com.company.package.MyClass", "debug"));
		assertFalse( manager.isDebugEnabled("com.company.package.MyClass", "notdefined"));
		assertFalse( manager.isDebugEnabled("com.company.package.MyClass", "infoX"));
		
		assertEquals( DebugManager.USER_DEFINED, manager.getClassLevel("com.company.package.MyOtherClass"));
		assertTrue( manager.isDebugEnabled("com.company.package.MyOtherClass", "visual"));
		assertFalse( manager.isDebugEnabled("com.company.package.MyOtherClass", "info"));
		assertFalse( manager.isDebugEnabled("com.company.package.MyOtherClass", "warn"));
		assertFalse( manager.isDebugEnabled("com.company.package.MyOtherClass", "warning"));
		assertFalse( manager.isDebugEnabled("com.company.package.MyOtherClass", "error"));
		assertFalse( manager.isDebugEnabled("com.company.package.MyOtherClass", "fatal"));
		assertFalse( manager.isDebugEnabled("com.company.package.MyOtherClass", "debug"));
		assertFalse( manager.isDebugEnabled("com.company.package.MyOtherClass", "notdefined"));
		assertFalse( manager.isDebugEnabled("com.company.package.MyOtherClass", "infoX"));
		
		assertEquals( DebugManager.ERROR, manager.getClassLevel("com.company.util.MyOtherClass"));
		assertTrue( manager.isDebugEnabled("com.company.util.MyOtherClass", "error"));
		assertTrue( manager.isDebugEnabled("com.company.util.MyOtherClass", "fatal"));
		assertTrue( manager.isDebugEnabled("com.company.util.MyOtherClass", "visual"));
		assertFalse( manager.isDebugEnabled("com.company.util.MyOtherClass", "info"));
		assertFalse( manager.isDebugEnabled("com.company.util.MyOtherClass", "warn"));
		assertFalse( manager.isDebugEnabled("com.company.util.MyOtherClass", "warning"));
		assertFalse( manager.isDebugEnabled("com.company.util.MyOtherClass", "debug"));
		assertFalse( manager.isDebugEnabled("com.company.util.MyOtherClass", "notdefined"));
		assertFalse( manager.isDebugEnabled("com.company.util.MyOtherClass", "infoX"));
		
		// invalid setting:
		try {
			manager.addDebugSetting("com.company.MyClass*", "debug");
			fail("DebugManager should not accept invalid class-pattern.");
		} catch (PreprocessException e) {
			// expected behaviour!
		}
	}

}
