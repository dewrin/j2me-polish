/*
 * Created on 26-Feb-2004 at 15:53:42.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.preprocess;

import de.enough.polish.util.StringList;

import java.util.Calendar;

import junit.framework.TestCase;

/**
 * <p>Tests the ImportManager class</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        26-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class ImportManagerTest extends TestCase {
	
	public ImportManagerTest(String name) {
		super(name);
	}
	
	public void testProcessImports() {
		ImportManager manager = new ImportManager();
		StringList code = new StringList( new String[] {
			"/*",
			"* Created on 20-Feb-2004 at 20:09:42.",
			"* This source code is published under the GNU General Public Licence and",
			"* the enough-software-licence for commercial use.",
			"* Please refer to accompanying LICENSE.txt or visit www.enough.de for details.",
			"*/",
			"package de.enough.polish.dict;",
			"",
			"import javax.microedition.lcdui.*;",
			"import javax.microedition.lcdui.ChoiceGroup;",
			"import javax.microedition.midlet.MIDlet;",
			"import javax.microedition.midlet.MIDletStateChangeException;",
			"",
			"/**",
			"* <p>A simple dictionary.</p>",
			"*",
			"* <p>copyright enough software 2004</p>",
			"* <pre>",
			"* history",
			"*        20-Feb-2004 - rob creation",
			"* </pre>",
			"* @author Robert Virkus, robert@enough.de",
			"*/",
			"public class DictMidlet extends MIDlet implements CommandListener {",
			"",
			"	private Screen startScreen;",
			"	private Display display;"
		});
		assertFalse( manager.processImports(false, true, code) );
		assertEquals("public class DictMidlet extends MIDlet implements CommandListener {", code.getCurrent() );
		code.reset();
		assertTrue( manager.processImports(true, true, code) );
		assertEquals("public class DictMidlet extends MIDlet implements CommandListener {", code.getCurrent() );
		code = new StringList( new String[] {
				"/*",
				"* Created on 20-Feb-2004 at 20:09:42.",
				"* This source code is published under the GNU General Public Licence and",
				"* the enough-software-licence for commercial use.",
				"* Please refer to accompanying LICENSE.txt or visit www.enough.de for details.",
				"*/",
				"package de.enough.polish.dict;",
				"",
				"import javax.microedition.lcdui.game.*;",
				"import javax.microedition.midlet.MIDlet;",
				"import javax.microedition.midlet.MIDletStateChangeException;",
				"",
				"/**",
				"* <p>A simple dictionary.</p>",
				"*",
				"* <p>copyright enough software 2004</p>",
				"* <pre>",
				"* history",
				"*        20-Feb-2004 - rob creation",
				"* </pre>",
				"* @author Robert Virkus, robert@enough.de",
				"*/",
				"public class DictMidlet extends MIDlet implements CommandListener {",
				"",
				"	private Screen startScreen;",
				"	private Display display;"
			});
			assertFalse( manager.processImports(true, false, code) );
			assertEquals("public class DictMidlet extends MIDlet implements CommandListener {", code.getCurrent() );
			code.reset();
			assertTrue( manager.processImports(true, true, code) );
			assertEquals("public class DictMidlet extends MIDlet implements CommandListener {", code.getCurrent() );
		/* 
		String[] lines = code.getArray();
		for (int i = 0; i < lines.length; i++) {
			System.out.println( lines[i] );
		}
		*/
		
		System.out.println( "MAX_VALUE=" + Thread.MAX_PRIORITY );
		System.out.println( "MIN_VALUE=" + Thread.MIN_PRIORITY );
		System.out.println("norm=" + Thread.NORM_PRIORITY );
		System.out.println( Calendar.AM );
		System.out.println( Calendar.PM );
		System.out.println( Calendar.FEBRUARY );
		System.out.println( Calendar.DECEMBER );
		System.out.println( Calendar.WEDNESDAY );
	}
}
