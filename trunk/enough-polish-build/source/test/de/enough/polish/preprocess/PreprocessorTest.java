/*
 * Created on 16-Jan-2004 at 15:00:06.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.preprocess;

import de.enough.polish.PolishProject;
import de.enough.polish.util.StringList;

import java.io.File;
import java.util.HashMap;

import junit.framework.TestCase;

/**
 * <p>Tests the Preprocessor.</p>
 *
 * <p>copyright enough software 2004</p>
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
		
		this.preprocessor = new Preprocessor( project, currentDir, variables, symbols, false, false, true, null );
		this.preprocessor.setSyleSheet( new StyleSheet() );
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
		} catch (PreprocessException e) {
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
		} catch (PreprocessException e) {
			// expected behaviour!
		}
		
	}

	/**
	 * Tests the #ifdef directive 
	 * @throws PreprocessException when the preprocessing fails
	 */
	public void testIfdef() throws PreprocessException {
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
		} catch (PreprocessException e) {
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
		} catch (PreprocessException e) {
			// expected behaviour
		}
		
	}
	
	public void testIfndef() throws PreprocessException {
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
		} catch (PreprocessException e) {
			// expected behaviour!
		}
		
	}
	
	public void testIf() throws PreprocessException {
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
		} catch (PreprocessException e) {
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
		} catch (PreprocessException e) {
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
		} catch (PreprocessException e) {
			// expected behaviour
		}
				
	}
	
	public void testDefine() throws PreprocessException {
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
	
	public void testUndefine() throws PreprocessException {
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
	
	
	public void testInclude() throws PreprocessException {
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
		} catch (PreprocessException e ) {
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
		} catch (PreprocessException e ) {
			// expected behaviour
		}
	}
	
	public void testVariables() throws PreprocessException {
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

	public void testStyle() throws PreprocessException {
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
		assertFalse( styleSheet.isDefined("weird"));
		
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
		result = this.preprocessor.preprocess( "MyClass.java", lines );
		assertEquals( Preprocessor.CHANGED, result );
		styleSheet = this.preprocessor.getStyleSheet();
		assertTrue( styleSheet.isUsed("weird"));
		
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
		} catch (PreprocessException e) {
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
		} catch (PreprocessException e) {
			// expected behaviour!
		}
	}	
	
	public void testIncludesPattern() {
		assertTrue( Preprocessor.includesDirective("		//#ifdef test2"));
		assertTrue( Preprocessor.includesDirective("		//#ifndef test2"));
		assertTrue( Preprocessor.includesDirective("		//#if (t1 && t2)|| t3"));
		assertTrue( Preprocessor.includesDirective("		//#elif (t1 && t2)|| t3"));
		assertTrue( Preprocessor.includesDirective("		//#elifdef t3"));
		assertTrue( Preprocessor.includesDirective("		//#elifndef t3"));
		assertTrue( Preprocessor.includesDirective("		//#endif"));
		assertTrue( Preprocessor.includesDirective("		//#style x"));
		assertTrue( Preprocessor.includesDirective("		//#= some = ${my.var};"));
		assertTrue( Preprocessor.includesDirective("		//#include test.java"));
		assertTrue( Preprocessor.includesDirective("		//#endinclude"));
		assertTrue( Preprocessor.includesDirective("		//#define something"));
		assertTrue( Preprocessor.includesDirective("		//#undefine something"));
		assertTrue( Preprocessor.includesDirective("		//#endif"));
		assertTrue( Preprocessor.includesDirective("		//#debug"));
		assertTrue( Preprocessor.includesDirective("		//#debug info"));
		assertTrue( Preprocessor.includesDirective("		//#mdebug"));
		assertTrue( Preprocessor.includesDirective("		//#enddebug"));
	}
	
	public void testDebug() throws PreprocessException {
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

	public void testMdebug() throws PreprocessException {
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
		} catch (PreprocessException e) {
			/// expected behaviour!
		}
	}
	
	public void testCondition() throws PreprocessException {
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
