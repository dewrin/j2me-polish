/*
 * Created on 16-Jan-2004 at 15:00:06.
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
package de.enough.polish.preprocess;

import de.enough.polish.PolishProject;
import de.enough.polish.util.StringList;

import org.apache.tools.ant.BuildException;

import java.io.File;
import java.util.HashMap;

import junit.framework.TestCase;

/**
 * <p>Tests the Preprocessor.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        16-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class PreprocessorTest extends TestCase {
	
	private Preprocessor preprocessor;

	/**
	 * Creates a new test
	 */
	public PreprocessorTest() {
		super();
	}

	/**
	 * Creates a new test.
	 * @param name the name of the test
	 */
	public PreprocessorTest(String name) {
		super(name);
	}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		File currentDir = new File( ".");
		
		DebugManager manager = new DebugManager( "error", true );
		manager.addDebugSetting( "com.company.package.*", "info" );
		manager.addDebugSetting( "com.company.package.MyClass", "debug" );
		manager.addDebugSetting( "com.company.package.MyOtherClass", "visual" );
		
		PolishProject project = new PolishProject( true, true, manager );
		
		HashMap symbols = new HashMap();
		symbols.put( "test1", Boolean.TRUE );
		symbols.put( "test2", Boolean.TRUE );
		symbols.put( "test3", Boolean.TRUE );
		
		HashMap variables = new HashMap();
		variables.put( "polish.source", currentDir.getAbsolutePath() + "/source/src");
		variables.put( "not.there", "/home/XX/YY/ZZ/src");
		variables.put( "fullscreen.class", "de.enough.polish.nokia.FullMenuCanvas");
		variables.put( "polish.message", "Hello world!");
		variables.put( "polish.DateFormat", "de");
		
		this.preprocessor = new Preprocessor( project, currentDir, variables, symbols, false, true, null );
		
		//prepare styles:
		StyleSheet sheet = new StyleSheet();
		StringBuffer buffer = new StringBuffer();
		buffer.append( ".funny {" )
			   .append( "	font-face: system; " )
			   .append( "	font-size: medium;" )
			   .append( "	font-style: italic;" )
			   .append( "	font-color: blue;" )
			   .append( "}" );
		CssBlock block = new CssBlock( buffer.toString() );
		sheet.addCssBlock(block);
		buffer = new StringBuffer();
		buffer.append( ".weird {" )
			   .append( "	font-style: italic;" )
			   .append( "	font-color: blue;" )
			   .append( "}" );
		block = new CssBlock( buffer.toString() );
		sheet.addCssBlock(block);
		buffer = new StringBuffer();
		buffer.append( ".crazy {" )
			   .append( "	font-style: italic;" )
			   .append( "	font-color: blue;" )
			   .append( "	background-color: #808080;" )
			   .append( "}" );
		block = new CssBlock( buffer.toString() );
		sheet.addCssBlock(block);
		assertTrue( sheet.isDefined("weird"));
		assertTrue( sheet.isDefined("funny"));
		assertTrue( sheet.isDefined("crazy"));
		this.preprocessor.setSyleSheet( sheet );
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		this.preprocessor = null;
	}

	public void testInvalidDirectives() {
		String[] sourceLines = new String[] {
				"	//#ifdef test1 ",
				"	String hello =  \"test1 is defined\";",
				"	//#elsek",
				"	//# String hello =  \"test1 is not defined\";",
				"	//#endif",
				"	System.out.println( hello );"
		};
		StringList lines = new StringList( sourceLines );
		try {
			this.preprocessor.preprocess( "MyClass.java", lines );
			fail("PreprocessException should be thrown when #else is misspelled.");
		} catch (BuildException e) {
			// expected behaviour!
		}
		sourceLines = new String[] {
				"	//#else ",
				"	String hello =  \"test1 is defined\";",
				"	//#ifdef test1",
				"	//# String hello =  \"test1 is not defined\";",
				"	//#endif",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		try {
			this.preprocessor.preprocess( "MyClass.java", lines );
			fail("PreprocessException should be thrown when #else is first directive.");
		} catch (BuildException e) {
			// expected behaviour!
		}
		
	}

	/**
	 * Tests the #ifdef directive 
	 * @throws BuildException when the preprocessing fails
	 */
	public void testIfdef() throws BuildException {
		String[] sourceLines = new String[] {
			"	//#ifdef test1 ",
			"	String hello =  \"test1 is defined\";",
			"	//#else",
			"	//# String hello =  \"test1 is not defined\";",
			"	//#endif",
			"	System.out.println( hello );"
		};
		StringList lines = new StringList( sourceLines );
		int result  = this.preprocessor.preprocess( "MyClass.java", lines );
		assertEquals( Preprocessor.NOT_CHANGED,  result );
		/*
		String[] processedLines = lines.getArray();
		for (int i = 0; i < processedLines.length; i++) {
			System.out.println( processedLines[i] );
		}
		System.out.println("=========================");
		*/
		sourceLines = new String[] {
				"	//#ifdef XXtest1 ",
				"	String hello =  \"XXtest1 is defined\";",
				"	//#else",
				"	//# String hello =  \"XXtest1 is not defined\";",
				"	//#endif",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "MyClass.java", lines );
		assertEquals( Preprocessor.CHANGED, result );
		/*
		processedLines = lines.getArray();
		System.out.println("#ifdef test");
		for (int i = 0; i < processedLines.length; i++) {
			System.out.println( processedLines[i] );
		}
		*/
		sourceLines = new String[] {
				"	//#ifdef XXtest1 ",
				"	String hello =  \"XXtest1 is defined\";",
				"	//#else",
				"	//# String hello =  \"XXtest1 is not defined\";",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		try {
			this.preprocessor.preprocess( "MyClass.java", lines );
			fail("PreprocessException should be thrown when #endif is missing.");
		} catch (BuildException e) {
			// expected behaviour!
		}

		// check inner #ifdef    (1):
		sourceLines = new String[] {
				"	//#ifdef test1 ",
				"	String hello =  \"test1 is defined\";",
				"		//#ifdef test2 ",
				"		hello =  \"test2 is defined\";",
				"		//#endif",
				"	//#else",
				"	//# String hello =  \"test1 is not defined\";",
				"		//#ifdef test2 ",
				"		//# hello +=  \" - but test2 is defined\";",
				"		//#endif",
				"	//#endif",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "MyClass.java", lines );
		assertEquals( Preprocessor.NOT_CHANGED, result );
		
		// check inner #ifdef    (2):
		sourceLines = new String[] {
				"	//#ifdef XXtest1 ",
				"	String hello =  \"XXtest1 is defined\";",
				"		//#ifdef test2 ",
				"		hello =  \"test2 is defined\";",
				"		//#endif",
				"	//#else",
				"	//# String hello =  \"XXtest1 is not defined\";",
				"		//#ifdef test2 ",
				"		//# hello +=  \" - but test2 is defined\";",
				"		//#endif",
				"	//#endif",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "MyClass.java", lines );
		assertEquals( Preprocessor.CHANGED, result );
		
		// check inner #ifdef    (3 - with syntax error):
		sourceLines = new String[] {
				"	//#ifdef test1 ",
				"	String hello =  \"test1 is defined\";",
				"		//#ifdef test2 ",
				"		hello =  \"test2 is defined\";",
				"		//#endif",
				"	//#else",
				"	//# String hello =  \"test1 is not defined\";",
				"		//#ifdefk test2 ",
				"		//# hello +=  \" - but test2 is defined\";",
				"		//#endif",
				"	//#endif",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		try {
			result = this.preprocessor.preprocess( "MyClass.java", lines );
			fail( "preprocessing should fail when encountering syntax error within inactive if-block." );
		} catch (BuildException e) {
			// expected behaviour
		}
		
	}
	
	public void testIfndef() throws BuildException {
		String[] sourceLines = new String[] {
				"	//#ifndef test1 ",
				"	//# String hello =  \"test1 is not defined\";",
				"	//#else",
				"	String hello =  \"test1 is defined\";",
				"	//#endif",
				"	System.out.println( hello );"
		};
		StringList lines = new StringList( sourceLines );
		int result = this.preprocessor.preprocess( "MyClass.java", lines );
		assertEquals( Preprocessor.NOT_CHANGED, result );
		/*
		String[] processedLines = lines.getArray();
		System.out.println("#ifndef test");
		for (int i = 0; i < processedLines.length; i++) {
			System.out.println( processedLines[i] );
		}
		System.out.println("=========================");
		*/
		sourceLines = new String[] {
				"	//#ifndef test1 ",
				"	String hello =  \"test1 is defined\";",
				"	//#else",
				"	//# String hello =  \"test1 is not defined\";",
				"	//#endif",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "MyClass.java", lines );
		assertEquals( Preprocessor.CHANGED, result );

		sourceLines = new String[] {		
			"	//#ifndef polish.skipArgumentCheck",
			"		if (listType != Choice.EXCLUSIVE && listType != Choice.MULTIPLE && listType != Choice.IMPLICIT ) {",
			"			//#ifdef polish.debugVerbose",
			"				throw new IllegalArgumentException(\"invalid list-type: \" + listType );",
			"			//#else",
			"				//# throw new IllegalArgumentException();",
			"			//#endif",
			"		}",		
			"	//#endif"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "MyClass.java", lines );
		assertEquals( Preprocessor.CHANGED, result );
		
		/*
		processedLines = lines.getArray();
		for (int i = 0; i < processedLines.length; i++) {
			System.out.println( processedLines[i] );
		}
		*/
		
		sourceLines = new String[] {
				"	//#ifndef XXtest1 ",
				"	String hello =  \"XXtest1 is defined\";",
				"	//#else",
				"	//# String hello =  \"XXtest1 is not defined\";",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		try {
			this.preprocessor.preprocess( "MyClass.java", lines );
			fail("PreprocessException should be thrown when #endif is missing.");
		} catch (BuildException e) {
			// expected behaviour!
		}
		
	}
	
	public void testIf() throws BuildException {
		String[] sourceLines = new String[] {
				"	//#if test1 ",
				"	String hello =  \"test1 is defined\";",
				"	//#else",
				"	//# String hello =  \"test1 is not defined\";",
				"	//#endif",
				"	System.out.println( hello );"
		};
		StringList lines = new StringList( sourceLines );
		int result = this.preprocessor.preprocess( "MyClass.java", lines );
		assertEquals( Preprocessor.NOT_CHANGED, result );
		/*
		 String[] processedLines = lines.getArray();
		 for (int i = 0; i < processedLines.length; i++) {
		 System.out.println( processedLines[i] );
		 }
		 System.out.println("=========================");
		 */
		sourceLines = new String[] {
				"	//#if XXtest1 ",
				"	String hello =  \"XXtest1 is defined\";",
				"	//#else",
				"	//# String hello =  \"XXtest1 is not defined\";",
				"	//#endif",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "MyClass.java", lines );
		assertEquals( Preprocessor.CHANGED, result );
		/*
		 processedLines = lines.getArray();
		 System.out.println("#ifdef test");
		 for (int i = 0; i < processedLines.length; i++) {
		 System.out.println( processedLines[i] );
		 }
		 */
		sourceLines = new String[] {
				"	//#if XXtest1 ",
				"	String hello =  \"XXtest1 is defined\";",
				"	//#else",
				"	//# String hello =  \"XXtest1 is not defined\";",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		try {
			this.preprocessor.preprocess( "MyClass.java", lines );
			fail("PreprocessException should be thrown when #endif is missing.");
		} catch (BuildException e) {
			// expected behaviour!
		}

		// check inner #if    (1):
		sourceLines = new String[] {
				"	//#if (test1 ^ ! test2) ^ false",
				"	String hello =  \"test1 is defined\";",
				"		//#if (test2 || false) && test3",
				"		hello =  \"test2 is defined\";",
				"		//#endif",
				"	//#else",
				"	//# String hello =  \"test1 is not defined\";",
				"		//#ifdef test2 ",
				"		//# hello +=  \" - but test2 is defined\";",
				"		//#endif",
				"	//#endif",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "MyClass.java", lines );
		assertEquals( Preprocessor.NOT_CHANGED, result );
		
		// check inner #if    (2):
		sourceLines = new String[] {
				"	//#ifdef XXtest1 ",
				"	String hello =  \"XXtest1 is defined\";",
				"		//#ifdef test2 ",
				"		hello =  \"test2 is defined\";",
				"		//#endif",
				"	//#elif (test1 && test3) || (test4 ^ test5)",
				"	//# String hello =  \"XXtest1 is not defined\";",
				"		//#ifdef test2 ",
				"		//# hello +=  \" - but test2 is defined\";",
				"		//#endif",
				"	//#endif",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "MyClass.java", lines );
		assertEquals( Preprocessor.CHANGED, result );
		
		// check inner #if    (3 - with syntax error):
		sourceLines = new String[] {
				"	//#ifdef test1 ",
				"	String hello =  \"test1 is defined\";",
				"		//#ifdef test2 ",
				"		hello =  \"test2 is defined\";",
				"		//#endif",
				"	//#else",
				"	//# String hello =  \"test1 is not defined\";",
				"		//#ifk (test2 && test3)",
				"		//# hello +=  \" - but test2 is defined\";",
				"		//#endif",
				"	//#endif",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		try {
			result = this.preprocessor.preprocess( "MyClass.java", lines );
			fail( "preprocessing should fail when encountering syntax error within inactive if-block." );
		} catch (BuildException e) {
			// expected behaviour
		}
		
		// check inner #if    (4 - with subtle syntax error in active part):
		sourceLines = new String[] {
				"	//#if ! test1 ",
				"	String hello =  \"test1 is defined\";",
				"		//#ifdef test2 ",
				"		hello =  \"test2 is defined\";",
				"		//#endif",
				"	//#else",
				"	//# String hello =  \"test1 is not defined\";",
				"		//#if (test2 && test3",
				"		//# hello +=  \" - but test2 is defined\";",
				"		//#endif",
				"	//#endif",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		try {
			result = this.preprocessor.preprocess( "MyClass.java", lines );
			fail( "preprocessing should fail when encountering syntax error within inactive if-block." );
		} catch (BuildException e) {
			// expected behaviour
		}
				
	}
	
	public void testElIf() throws BuildException {
		String[] sourceLines = new String[] {
				"	//#if polish.DateFormat == us ",
				"	String hello =  \"us is defined\";",
				"	//#elif polish.DateFormat == de ",
				"	//# String hello =  \"de is used\";",
				"	//#else",
				"	//# String hello =  \"iso is used\";",
				"	//#endif",
				"	System.out.println( hello );"
		};
		StringList lines = new StringList( sourceLines );
		int result = this.preprocessor.preprocess( "MyClass.java", lines );
		assertEquals( Preprocessor.CHANGED, result );
		/*
		 String[] processedLines = lines.getArray();
		 for (int i = 0; i < processedLines.length; i++) {
		 System.out.println( processedLines[i] );
		 }
		 System.out.println("=========================");
		 */
		sourceLines = new String[] {
				"	//#if true ",
				"		String hello =  \"true is defined\";",
				"	//#elif false",
				"		//# String hello =  \"false is defined\";",
				"	//#elif true",
				"		//# String hello =  \"true is defined-again\";",
				"	//#else",
				"		//# String hello =  \"should not be seen\";",
				"	//#endif",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "MyClass.java", lines );
		assertEquals( Preprocessor.NOT_CHANGED, result );
		/*
		 String[] processedLines = lines.getArray();
		 for (int i = 0; i < processedLines.length; i++) {
		 System.out.println( processedLines[i] );
		 }
		 System.out.println("=========================");
		 */

		sourceLines = new String[] {
				"	//#if false ",
				"		String hello =  \"true is defined\";",
				"	//#elif false",
				"		//# String hello =  \"false is defined\";",
				"	//#elif true",
				"		//# String hello =  \"true is defined-again\";",
				"		//#ifdef false",
				"			//# //false IS DEFINED",
				"		//#elifdef false",
				"			//# //false false SHOULD NOT BE DEFINED",
				"		//#else",
				"			//# //BINGO...",
				"		//#endif",				
				"	//#else",
				"		//# String hello =  \"should not be seen\";",
				"	//#endif",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "MyClass.java", lines );
		assertEquals( Preprocessor.CHANGED, result );

		sourceLines = new String[] {
				"	//#if false ",
				"		String hello =  \"true is defined\";",
				"	//#elif false",
				"		//# String hello =  \"false is defined\";",
				"	//#elif false",
				"		//# String hello =  \"true is defined-again\";",
				"	//#else",
				"		//# String hello =  \"should not be seen\";",
				"		//#ifdef false",
				"			//# //false IS DEFINED",
				"		//#elifdef false",
				"			//# //false false SHOULD NOT BE DEFINED",
				"		//#else",
				"			//# //BINGO...",
				"		//#endif",				
				"	//#endif",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "MyClass.java", lines );
		assertEquals( Preprocessor.CHANGED, result );
	}
	
	
	public void testDefine() throws BuildException {
		String[] sourceLines = new String[] {
				"	//#ifdef test1 ",
				"	String hello =  \"test1 is defined\";",
				"	//#else",
				"	//# String hello =  \"test1 is not defined\";",
				"	//#define KNUDDEL",
				"	//#endif",
				"	System.out.println( hello );"
		};
		StringList lines = new StringList( sourceLines );
		int result = this.preprocessor.preprocess( "MyClass.java", lines );
		assertEquals( Preprocessor.NOT_CHANGED, result );
		assertTrue(  this.preprocessor.symbols.get("KNUDDEL") == null);
		
		sourceLines = new String[] {
				"	//#ifdef test1XX ",
				"	String hello =  \"test1 is defined\";",
				"	//#else",
				"	//# String hello =  \"test1 is not defined\";",
				"	//#define KNUDDEL",
				"	//#endif",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "MyClass.java", lines );
		assertEquals( Preprocessor.CHANGED, result );
		assertFalse(  this.preprocessor.symbols.get("KNUDDEL") == null);
	}
	
	public void testUndefine() throws BuildException {
		String[] sourceLines = new String[] {
				"	//#ifdef test1 ",
				"	String hello =  \"test1 is defined\";",
				"	//#else",
				"	//# String hello =  \"test1 is not defined\";",
				"	//#undefine test2",
				"	//#endif",
				"	System.out.println( hello );"
		};
		StringList lines = new StringList( sourceLines );
		int result = this.preprocessor.preprocess( "MyClass.java", lines );
		assertEquals( Preprocessor.NOT_CHANGED, result );
		assertTrue(  this.preprocessor.symbols.get("test2") != null);
		
		sourceLines = new String[] {
				"	//#ifdef test1XX ",
				"	String hello =  \"test1 is defined\";",
				"	//#else",
				"	//# String hello =  \"test1 is not defined\";",
				"	//#undefine test2",
				"	//#endif",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "MyClass.java", lines );
		assertEquals( Preprocessor.CHANGED, result );
		assertFalse(  this.preprocessor.symbols.get("test2") != null);
	}
	
	
	public void testInclude() throws BuildException {
		String[] sourceLines = new String[] {
				"	//#ifdef test1 ",
				"	String hello =  \"test1 is defined\";",
				"	//#else",
				"	//# String hello =  \"test1 is not defined\";",
				"	//#include ${polish.source }/CVS/properties",
				"	//#endif",
				"	System.out.println( hello );"
		};
		StringList lines = new StringList( sourceLines );
		int result = this.preprocessor.preprocess( "MyClass.java", lines );
		assertEquals( Preprocessor.NOT_CHANGED, result );
		
		sourceLines = new String[] {
				"	//#ifdef XXtest1 ",
				"	String hello =  \"XXtest1 is defined\";",
				"	//#else",
				"	//# String hello =  \"XXtest1 is not defined\";",
				"	//#include ${polish.source }/CVS/Entries",
				"	//# hello +=  \"include succeeded\";",
				"	//#endif",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "MyClass.java", lines );
		assertEquals( Preprocessor.CHANGED, result );

		// use invalid property
		sourceLines = new String[] {
				"	//#ifdef XXtest1 ",
				"	String hello =  \"XXtest1 is defined\";",
				"	//#else",
				"	//# String hello =  \"XXtest1 is not defined\";",
				"	//#include ${polish.source.undefined }/CVS/Entries",
				"	//# hello +=  \"include succeeded\";",
				"	//#endif",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		try {
			this.preprocessor.preprocess( "MyClass.java", lines );
			fail( "#include directive with invalid property should result in PreprocessException." );
		} catch (BuildException e ) {
			// expected behaviour
		}

		// use file which is not there
		sourceLines = new String[] {
				"	//#ifdef XXtest1 ",
				"	String hello =  \"XXtest1 is defined\";",
				"	//#else",
				"	//# String hello =  \"XXtest1 is not defined\";",
				"	//#include ${  not.there }/CVS/Entries",
				"	//# hello +=  \"include succeeded\";",
				"	//#endif",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		try {
			this.preprocessor.preprocess( "MyClass.java", lines );
			fail( "#include directive with invalid file-property should result in PreprocessException." );
		} catch (BuildException e ) {
			// expected behaviour
		}
	}
	
	public void testVariables() throws BuildException {
		String[] sourceLines = new String[] {
				"	//#ifdef test1 ",
				"	String hello =  \"test1 is defined\";",
				"	//#else",
				"	//# String hello =  \"test1 is not defined\";",
				"	//#= hello += ${ polish.message }; ",
				"	//#endif",
				"	System.out.println( hello );"
		};
		StringList lines = new StringList( sourceLines );
		int result = this.preprocessor.preprocess( "MyClass.java", lines );
		assertEquals( Preprocessor.NOT_CHANGED, result );
		
		sourceLines = new String[] {
				"	//#ifdef XXtest1 ",
				"	String hello =  \"XXtest1 is defined\";",
				"	//#else",
				"	//# String hello =  \"XXtest1 is not defined\";",
				"	//#= hello += \"${ polish.message }\"; ",
				"	//#endif",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "MyClass.java", lines );
		assertEquals( Preprocessor.CHANGED, result );
	}

	public void testStyle() throws BuildException {
		String[] sourceLines = new String[] {
				"	//#ifdef test1 ",
				"	String hello =  \"test1 is defined\";",
				"	//#else",
				"	//# String hello =  \"test1 is not defined\";",
				"	//#style weird",
				"	//#endif",
				"	System.out.println( hello );"
		};
		StringList lines = new StringList( sourceLines );
		int result = this.preprocessor.preprocess( "MyClass.java", lines );
		assertEquals( Preprocessor.NOT_CHANGED, result );
		StyleSheet styleSheet = this.preprocessor.getStyleSheet();
		assertFalse( styleSheet.isUsed("weird"));
		
		// this should not work, since no new operator or method call is
		// done in the next line:
		sourceLines = new String[] {
				"	//#ifdef XXtest1 ",
				"	String hello =  \"XXtest1 is defined\";",
				"	//#else",
				"	//# String hello =  \"XXtest1 is not defined\";",
				"	//#style weird",
				"	//#endif",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		try {
			result = this.preprocessor.preprocess( "MyClass.java", lines );
			fail("#style directive should fail when no method call or new operator follows the directive.");
		} catch (BuildException e) {
			//expected behaviour
		}
		
		sourceLines = new String[] {
				"	//#ifdef XXtest1 ",
				"	String hello =  \"XXtest1 is defined\";",
				"	//#else",
				"	//# String hello =  \"XXtest1 is not defined\";",
				"	//#style crazy",
				"	//# Item myItem = new StringItem(\"label\", \"text\"); // create StringItem... ",
				"	//#endif",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "MyClass.java", lines );
		assertEquals( Preprocessor.CHANGED, result );
		styleSheet = this.preprocessor.getStyleSheet();
		assertTrue( styleSheet.isUsed("crazy"));
		
		sourceLines = new String[] {
				"	//#ifdef XXtest1 ",
				"	String hello =  \"XXtest1 is defined\";",
				"	//#else",
				"	//# String hello =  \"XXtest1 is not defined\";",
				"	//#style funny",
				"	//# Item myItem = new StringItem(",
				"	//#		\"label\", ",
				"	//#		\"text\"); // create StringItem... ",
				"	//#endif",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "MyClass.java", lines );
		assertEquals( Preprocessor.CHANGED, result );
		styleSheet = this.preprocessor.getStyleSheet();
		assertTrue( styleSheet.isUsed("funny"));
		
		// without closing semicolon:
		sourceLines = new String[] {
				"	//#ifdef XXtest1 ",
				"	String hello =  \"XXtest1 is defined\";",
				"	//#else",
				"	//# String hello =  \"XXtest1 is not defined\";",
				"	//#style funny",
				"	//# Item myItem = new StringItem(",
				"	//#		\"label\", ",
				"	//#		\"text\") // create StringItem... ",
				"	//#endif",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		try {
			result = this.preprocessor.preprocess( "MyClass.java", lines );
			fail("preprocess should fail when encountering #style directive with syntax errors.");
		} catch (BuildException e) {
			// expected behaviour!
		}
		// with single semicolon:
		sourceLines = new String[] {
				"	//#ifdef XXtest1 ",
				"	String hello =  \"XXtest1 is defined\";",
				"	//#else",
				"	//# String hello =  \"XXtest1 is not defined\";",
				"	//#style funny",
				"	//# Item myItem = new StringItem(",
				"	//#		\"label\", ",
				"	//#		\"text\") // create StringItem... ",
				";",
				"	//#endif",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		try {
			result = this.preprocessor.preprocess( "MyClass.java", lines );
			fail("preprocess should fail when encountering #style directive with syntax errors.");
		} catch (BuildException e) {
			// expected behaviour!
		}
		
		// test undefined style:
		sourceLines = new String[] {
				"	//#ifdef XXtest1 ",
				"	String hello =  \"XXtest1 is defined\";",
				"	//#else",
				"	//# String hello =  \"XXtest1 is not defined\";",
				"	//#style styleWhichHasNeverBeenDefined, anotherInvalidStyle",
				"	//# Item myItem = new StringItem(",
				"	//#		\"label\", ",
				"	//#		\"text\"); // create StringItem... ",
				"	//#endif",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		try {
			result = this.preprocessor.preprocess( "MyClass.java", lines );
			fail("preprocess should fail when encountering #style directive with syntax errors.");
		} catch (BuildException e) {
			//e.printStackTrace();
			// expected behaviour!
		}
		
	}	
	
	public void testIncludesPattern() {
		assertTrue( Preprocessor.containsDirective("		//#ifdef test2"));
		assertTrue( Preprocessor.containsDirective("		//#ifndef test2"));
		assertTrue( Preprocessor.containsDirective("		//#if (t1 && t2)|| t3"));
		assertTrue( Preprocessor.containsDirective("		//#elif (t1 && t2)|| t3"));
		assertTrue( Preprocessor.containsDirective("		//#elifdef t3"));
		assertTrue( Preprocessor.containsDirective("		//#elifndef t3"));
		assertTrue( Preprocessor.containsDirective("		//#endif"));
		assertTrue( Preprocessor.containsDirective("		//#style x"));
		assertTrue( Preprocessor.containsDirective("		//#= some = ${my.var};"));
		assertTrue( Preprocessor.containsDirective("		//#include test.java"));
		assertTrue( Preprocessor.containsDirective("		//#endinclude"));
		assertTrue( Preprocessor.containsDirective("		//#define something"));
		assertTrue( Preprocessor.containsDirective("		//#undefine something"));
		assertTrue( Preprocessor.containsDirective("		//#endif"));
		assertTrue( Preprocessor.containsDirective("		//#debug"));
		assertTrue( Preprocessor.containsDirective("		//#debug info"));
		assertTrue( Preprocessor.containsDirective("		//#mdebug"));
		assertTrue( Preprocessor.containsDirective("		//#enddebug"));
	}
	
	public void testDebug() throws BuildException {
		String[] sourceLines = new String[] {
				"	//#debug",
				"	System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";",
				"	System.out.println( hello );"
		};
		StringList lines = new StringList( sourceLines );
		int result = this.preprocessor.preprocess( "com.company.package.MyClass", lines );
		assertEquals( Preprocessor.CHANGED, result );
		// test the same with a class for which debugging is disabled:
		lines.reset();
		result = this.preprocessor.preprocess( "com.company.package.MyOtherClass", lines );
		
		// debug-level = info
		sourceLines = new String[] {
				"	//#debug",
				"	System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "com.company.package.MyUndefinedClass", lines );
		assertEquals( Preprocessor.CHANGED, result );
		sourceLines = new String[] {
				"	//#debug info",
				"	System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "com.company.package.MyUndefinedClass", lines );
		assertEquals( Preprocessor.CHANGED, result );
		sourceLines = new String[] {
				"	//#debug warn",
				"	System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "com.company.package.MyUndefinedClass", lines );
		assertEquals( Preprocessor.CHANGED, result );
		sourceLines = new String[] {
				"	//#debug error",
				"	System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "com.company.package.MyUndefinedClass", lines );
		assertEquals( Preprocessor.CHANGED, result );
		sourceLines = new String[] {
				"	//#debug info",
				"	//# System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "com.company.package.MyUndefinedClass", lines );
		assertEquals( Preprocessor.CHANGED, result );
		
		// debug-level = visual
		sourceLines = new String[] {
				"	//#debug",
				"	System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "com.company.package.MyOtherClass", lines );
		assertEquals( Preprocessor.CHANGED, result );
		sourceLines = new String[] {
				"	//#debug info",
				"	System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "com.company.package.MyOtherClass", lines );
		assertEquals( Preprocessor.CHANGED, result );
		sourceLines = new String[] {
				"	//#debug fatal",
				"	System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "com.company.package.MyOtherClass", lines );
		assertEquals( Preprocessor.CHANGED, result );
		sourceLines = new String[] {
				"	//#debug visual",
				"	System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "com.company.package.MyOtherClass", lines );
		assertEquals( Preprocessor.CHANGED, result );
		sourceLines = new String[] {
				"	//#debug notdefined",
				"	System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "com.company.package.MyOtherClass", lines );
		assertEquals( Preprocessor.CHANGED, result );

		// debug-level = error
		sourceLines = new String[] {
				"	//#debug",
				"	System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "com.company.util.MyOtherClass", lines );
		assertEquals( Preprocessor.CHANGED, result );
		sourceLines = new String[] {
				"	//#debug info",
				"	System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "com.company.util.MyOtherClass", lines );
		assertEquals( Preprocessor.CHANGED, result );
		sourceLines = new String[] {
				"	//#debug warn",
				"	System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "com.company.util.MyOtherClass", lines );
		assertEquals( Preprocessor.CHANGED, result );
		sourceLines = new String[] {
				"	//#debug error",
				"	System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "com.company.util.MyOtherClass", lines );
		assertEquals( Preprocessor.CHANGED, result );
		sourceLines = new String[] {
				"	//#debug fatal",
				"	System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "com.company.util.MyOtherClass", lines );
		assertEquals( Preprocessor.CHANGED, result );
		sourceLines = new String[] {
				"	//#debug visual",
				"	System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "com.company.util.MyOtherClass", lines );
		assertEquals( Preprocessor.CHANGED, result );
		sourceLines = new String[] {
				"	//#debug undefined",
				"	System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "com.company.util.MyOtherClass", lines );
		assertEquals( Preprocessor.CHANGED, result );
	}

	public void testMdebug() throws BuildException {
		String[] sourceLines = new String[] {
				"	//#mdebug",
				"	System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";",
				"	//#enddebug ",
				"	System.out.println( hello );"
		};
		StringList lines = new StringList( sourceLines );
		int result = this.preprocessor.preprocess( "com.company.package.MyClass", lines );
		assertEquals( Preprocessor.CHANGED, result );
		// test the same with a class for which debugging is disabled:
		lines.reset();
		result = this.preprocessor.preprocess( "com.company.package.MyOtherClass", lines );
		
		// debug-level = info
		sourceLines = new String[] {
				"	//#mdebug",
				"	System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";",
				"	//#enddebug ",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "com.company.package.MyUndefinedClass", lines );
		assertEquals( Preprocessor.CHANGED, result );
		sourceLines = new String[] {
				"	//#mdebug info",
				"	System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";",
				"	//#enddebug ",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "com.company.package.MyUndefinedClass", lines );
		assertEquals( Preprocessor.CHANGED, result );
		sourceLines = new String[] {
				"	//#mdebug warn",
				"	System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";",
				"	System.out.println( hello );",
				"	//#enddebug "
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "com.company.package.MyUndefinedClass", lines );
		assertEquals( Preprocessor.CHANGED, result );
		sourceLines = new String[] {
				"	//#mdebug error",
				"	System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";",
				"	//#enddebug ",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "com.company.package.MyUndefinedClass", lines );
		assertEquals( Preprocessor.CHANGED, result );
		sourceLines = new String[] {
				"	//#mdebug info",
				"	//# System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";",
				"	//#enddebug ",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "com.company.package.MyUndefinedClass", lines );
		assertEquals( Preprocessor.CHANGED, result );
		
		// debug-level = visual
		sourceLines = new String[] {
				"	//#mdebug",
				"	System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";",
				"	//#enddebug ",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "com.company.package.MyOtherClass", lines );
		assertEquals( Preprocessor.CHANGED, result );
		sourceLines = new String[] {
				"	//#mdebug info",
				"	System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";",
				"	//#enddebug ",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "com.company.package.MyOtherClass", lines );
		assertEquals( Preprocessor.CHANGED, result );
		sourceLines = new String[] {
				"	//#mdebug fatal",
				"	System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";",
				"	//#enddebug ",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "com.company.package.MyOtherClass", lines );
		assertEquals( Preprocessor.CHANGED, result );
		sourceLines = new String[] {
				"	//#mdebug visual",
				"	System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";",
				"	//#enddebug ",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "com.company.package.MyOtherClass", lines );
		assertEquals( Preprocessor.CHANGED, result );
		sourceLines = new String[] {
				"	//#mdebug notdefined",
				"	System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";",
				"	//#enddebug ",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "com.company.package.MyOtherClass", lines );
		assertEquals( Preprocessor.CHANGED, result );

		// debug-level = error
		sourceLines = new String[] {
				"	//#mdebug",
				"	System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";",
				"	//#enddebug ",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "com.company.util.MyOtherClass", lines );
		assertEquals( Preprocessor.CHANGED, result );
		sourceLines = new String[] {
				"	//#mdebug info",
				"	System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";",
				"	//#enddebug ",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "com.company.util.MyOtherClass", lines );
		assertEquals( Preprocessor.CHANGED, result );
		sourceLines = new String[] {
				"	//#mdebug warn",
				"	System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";",
				"	//#enddebug ",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "com.company.util.MyOtherClass", lines );
		assertEquals( Preprocessor.CHANGED, result );
		
		sourceLines = new String[] {
				"	//#mdebug warn",
				"	//#enddebug ",
				"	System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "com.company.util.MyOtherClass", lines );
		assertEquals( Preprocessor.NOT_CHANGED, result );
		
		sourceLines = new String[] {
				"	//#mdebug error",
				"	System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";",
				"	//#enddebug ",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "com.company.util.MyOtherClass", lines );
		assertEquals( Preprocessor.CHANGED, result );
		sourceLines = new String[] {
				"	//#mdebug fatal",
				"	System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";",
				"	//#enddebug ",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "com.company.util.MyOtherClass", lines );
		assertEquals( Preprocessor.CHANGED, result );
		sourceLines = new String[] {
				"	//#mdebug visual",
				"	System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";",
				"	//#enddebug ",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "com.company.util.MyOtherClass", lines );
		assertEquals( Preprocessor.CHANGED, result );
		sourceLines = new String[] {
				"	//#mdebug undefined",
				"	System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";",
				"	//#enddebug ",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "com.company.util.MyOtherClass", lines );
		assertEquals( Preprocessor.CHANGED, result );
		
		
		// test invalid declarations:
		sourceLines = new String[] {
				"	//#mdebug",
				"	System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";",
				"	System.out.println( hello );"
		};
		lines = new StringList( sourceLines );
		try {
			this.preprocessor.preprocess( "com.company.util.MyOtherClass", lines );
			fail( "#mdebug needs #enddebug, when #enddebug is not present, it should fail.");
		} catch (BuildException e) {
			/// expected behaviour!
		}
	}
	
	public void testCondition() throws BuildException {
		String[] sourceLines = new String[] {
				"	//#condition ! polish.midp2",
				"	System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";"
		};
		StringList lines = new StringList( sourceLines );
		this.preprocessor.addSymbol("polish.midp2");
		int result = this.preprocessor.preprocess( "com.company.package.MyClass", lines );
		assertEquals( Preprocessor.SKIP_FILE, result );
		
		sourceLines = new String[] {
				"	//#condition !polish.midp2",
				"	System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "com.company.package.MyClass", lines );
		assertEquals( Preprocessor.SKIP_FILE, result );
		
		sourceLines = new String[] {
				"	//#condition polish.midp2",
				"	System.out.println( \"test1 is defined\" );",
				"	String hello =  \"hello world\";"
		};
		lines = new StringList( sourceLines );
		result = this.preprocessor.preprocess( "com.company.package.MyClass", lines );
		assertEquals( Preprocessor.NOT_CHANGED, result );
		
	}
	
}
