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
public class FontConverterTest extends TestCase {
	
	public FontConverterTest(String name) {
		super(name);
	}
	
	public void testFace() {
		FontConverter font = new FontConverter();
		String code = font.createNewStatement();
		assertEquals( "Font.getFont( Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM )", code);
		font.setFace( "monospace" );
		code = font.createNewStatement();
		assertEquals( "Font.getFont( Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_MEDIUM )", code);
		font.setFace( "proportional" );
		code = font.createNewStatement();
		assertEquals( "Font.getFont( Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_MEDIUM )", code);
		font.setFace( "system" );
		code = font.createNewStatement();
		assertEquals( "Font.getFont( Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM )", code);
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
		FontConverter font = new FontConverter();
		String code = font.createNewStatement();
		assertEquals( "Font.getFont( Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM )", code);
		font.setStyle( "bold" );
		code = font.createNewStatement();
		assertEquals( "Font.getFont( Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM )", code);
		font.setStyle( "italic" );
		code = font.createNewStatement();
		assertEquals( "Font.getFont( Font.FACE_SYSTEM, Font.STYLE_ITALIC, Font.SIZE_MEDIUM )", code);
		font.setStyle( "underlined" );
		code = font.createNewStatement();
		assertEquals( "Font.getFont( Font.FACE_SYSTEM, Font.STYLE_UNDERLINED, Font.SIZE_MEDIUM )", code);
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
		FontConverter font = new FontConverter();
		String code = font.createNewStatement();
		assertEquals( "Font.getFont( Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM )", code);
		font.setSize( "small" );
		code = font.createNewStatement();
		assertEquals( "Font.getFont( Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL )", code);
		font.setSize( "medium" );
		code = font.createNewStatement();
		assertEquals( "Font.getFont( Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM )", code);
		font.setSize( "large" );
		code = font.createNewStatement();
		assertEquals( "Font.getFont( Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_LARGE )", code);
		// try illegal style:
		try {
			font.setSize("snupio");
			fail("a font should not accept the invalid size [snupio].");
		} catch (BuildException e) {
			//System.out.println( "SIZE:   " + e.getMessage() );
			// expected behaviour
		}
	}
	
	public void testMix() {
		FontConverter font = new FontConverter();
		font.setStyle( "bold" );
		font.setSize("medium");
		font.setFace("system");
		String code = font.createNewStatement();
		assertEquals( "Font.getFont( Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM )", code);
	}

}
