/*
 * Created on 26-Feb-2004 at 15:53:42.
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
package de.enough.polish.preprocess;

import de.enough.polish.util.StringList;

import java.util.Calendar;

import junit.framework.TestCase;

/**
 * <p>Tests the ImportConverter class</p>
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
		ImportConverter manager = new ImportConverter();
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
