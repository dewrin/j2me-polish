/*
 * Created on 01-Mar-2004 at 19:59:31.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.preprocess;

import org.apache.tools.ant.BuildException;

import junit.framework.TestCase;

/**
 * <p>Tests the font class.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        01-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class FontTest extends TestCase {
	
	public FontTest(String name) {
		super(name);
	}
	
	public void testFace() {
		Font font = new Font( null );
		String[] code = font.getSourceCode();
		assertTrue( code.length > 0);
		assertEquals( "Font.getFont( Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM )", code[0]);
		font.setFace( Font.FACE_MONOSPACE );
		code = font.getSourceCode();
		assertEquals( "Font.getFont( Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_MEDIUM )", code[0]);
		font.setFace( Font.FACE_PROPORTIONAL );
		code = font.getSourceCode();
		assertEquals( "Font.getFont( Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_MEDIUM )", code[0]);
		font.setFace( Font.FACE_SYSTEM );
		code = font.getSourceCode();
		assertEquals( "Font.getFont( Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM )", code[0]);
		// try illegal face:
		try {
			font.setFace("snupio");
			fail("a font should not accept the invalid face [snupio].");
		} catch (BuildException e) {
			//System.out.println( "FACE:  " + e.getMessage() );
			// expected behaviour
		}
	}
	
	public void testStyle() {
		Font font = new Font( null );
		String[] code = font.getSourceCode();
		assertTrue( code.length > 0);
		assertEquals( "Font.getFont( Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM )", code[0]);
		font.setStyle( Font.STYLE_BOLD );
		code = font.getSourceCode();
		assertEquals( "Font.getFont( Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM )", code[0]);
		font.setStyle( Font.STYLE_ITALIC );
		code = font.getSourceCode();
		assertEquals( "Font.getFont( Font.FACE_SYSTEM, Font.STYLE_ITALIC, Font.SIZE_MEDIUM )", code[0]);
		font.setStyle( Font.STYLE_UNDERLINED );
		code = font.getSourceCode();
		assertEquals( "Font.getFont( Font.FACE_SYSTEM, Font.STYLE_UNDERLINED, Font.SIZE_MEDIUM )", code[0]);
		// try illegal style:
		try {
			font.setStyle("snupio");
			fail("a font should not accept the invalid style [snupio].");
		} catch (BuildException e) {
			//System.out.println( "STYLE:   " + e.getMessage() );
			// expected behaviour
		}
	}

	public void testSize() {
		Font font = new Font( null );
		String[] code = font.getSourceCode();
		assertTrue( code.length > 0);
		assertEquals( "Font.getFont( Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM )", code[0]);
		font.setSize( Font.SIZE_SMALL );
		code = font.getSourceCode();
		assertEquals( "Font.getFont( Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL )", code[0]);
		font.setSize( Font.SIZE_MEDIUM );
		code = font.getSourceCode();
		assertEquals( "Font.getFont( Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM )", code[0]);
		font.setSize( Font.SIZE_LARGE );
		code = font.getSourceCode();
		assertEquals( "Font.getFont( Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_LARGE )", code[0]);
		// try illegal style:
		try {
			font.setSize("snupio");
			fail("a font should not accept the invalid size [snupio].");
		} catch (BuildException e) {
			//System.out.println( "SIZE:   " + e.getMessage() );
			// expected behaviour
		}
	}

}
