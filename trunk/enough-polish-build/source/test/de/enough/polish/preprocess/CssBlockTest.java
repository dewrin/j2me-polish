/*
 * Created on 02-Mar-2004 at 18:19:55.
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

import java.util.HashMap;

import junit.framework.TestCase;

/**
 * <p>Tests the CssBlock class</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        02-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class CssBlockTest extends TestCase {

	public CssBlockTest(String name) {
		super(name);
	}
	
	public void testCssBlock() {
		StringBuffer buffer = new StringBuffer();
		buffer.append( "myStyle {" )
			   .append( "	font-face: system; " )
			   .append( "	font-size: medium;" )
			   .append( "	font-style: italic;" )
			   .append( "	font-color: blue;" )
			   .append( "}" );
		CssBlock block = new CssBlock( buffer.toString() );
		assertEquals( "myStyle", block.getSelector() );
		HashMap declarations = block.getDeclarationsMap();
		assertEquals( 4, declarations.size() );
		assertEquals("system", declarations.get("font-face"));
		assertEquals("medium", declarations.get("font-size"));
		assertEquals("italic", declarations.get("font-style"));
		assertEquals("blue", declarations.get("font-color"));
		String[] groupNames = block.getGroupNames();
		assertEquals( 1, groupNames.length );
		assertEquals( "font", groupNames[0]);
		HashMap group = block.getGroupDeclarations("font");
		assertEquals( 4, group.size() );
		assertEquals("system", group.get("face"));
		assertEquals("medium", group.get("size"));
		assertEquals("italic", group.get("style"));
		assertEquals("blue", group.get("color"));
		
		
		buffer = new StringBuffer();
		buffer.append( "default {" )
		   .append( "	font {"  )
		   .append( "		face: system;; " )
		   .append( "		size: medium;" )
		   .append( "		style: bold;" )
		   .append( "		color: blue;" )
		   .append( "	}" )
		   .append( "	background-color: white;" )
		   .append( "	border {"  )
		   .append( "		type: simple; " )
		   .append( "		color: red;" )
		   .append( "	}" )
		   .append( "}" );
		block = new CssBlock( buffer.toString() );
		assertEquals( "default", block.getSelector() );
		declarations = block.getDeclarationsMap();
		assertEquals( 7, declarations.size() );
		assertEquals("system", declarations.get("font-face"));
		assertEquals("medium", declarations.get("font-size"));
		assertEquals("bold", declarations.get("font-style"));
		assertEquals("blue", declarations.get("font-color"));
		assertEquals("white", declarations.get("background-color"));
		assertEquals("simple", declarations.get("border-type"));
		assertEquals("red", declarations.get("border-color"));
		
		groupNames = block.getGroupNames();
		assertEquals( 3, groupNames.length );
		group = block.getGroupDeclarations("font");
		assertEquals( 4, group.size() );
		assertEquals("system", group.get("face"));
		assertEquals("medium", group.get("size"));
		assertEquals("bold", group.get("style"));
		assertEquals("blue", group.get("color"));
		group = block.getGroupDeclarations("background");
		assertEquals( 1, group.size() );
		assertEquals("white", group.get("color"));
		group = block.getGroupDeclarations("border");
		assertEquals( 2, group.size() );
		assertEquals("simple", group.get("type"));
		assertEquals("red", group.get("color"));
		
		// try invalid block:
		buffer = new StringBuffer();
		buffer.append( "default {" )
		   .append( "	font {"  )
		   .append( "		face: system; " )
		   .append( "		size medium;" )
		   .append( "		style: bold;" )
		   .append( "		color: blue;" )
		   .append( "	}" )
		   .append( "	background-color: white;" )
		   .append( "}" );
		try {
			block = new CssBlock( buffer.toString() );
			fail("CssBlock should fail with invalid code [" + buffer.toString() + "].");
		} catch (BuildException e) {
			//System.out.println( e.getMessage() );
			//e.printStackTrace();
			// expected behaviour
		}
		
		buffer = new StringBuffer();
		buffer.append( "default {" )
		   .append( "	font {"  )
		   .append( "		face: system;; " )
		   .append( "		size: medium;" )
		   .append( "		style: bold;" )
		   .append( "		color: blue;" )
		   .append( "	}" )
		   .append( "	background-color white;" )
		   .append( "}" );
		try {
			block = new CssBlock( buffer.toString() );
			fail("CssBlock should fail with invalid code [" + buffer.toString() + "].");
		} catch (BuildException e) {
			//System.out.println( e.getMessage() );
			//e.printStackTrace();
			// expected behaviour
		}
		
	}
}
