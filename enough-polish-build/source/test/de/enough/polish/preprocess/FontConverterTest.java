/*
 * Created on 01-Mar-2004 at 19:59:31.
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

import org.apache.tools.ant.BuildException;

import junit.framework.TestCase;

/**
 * <p>Tests the font class.</p>
 *
 * <p>copyright Enough Software 2004</p>
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
