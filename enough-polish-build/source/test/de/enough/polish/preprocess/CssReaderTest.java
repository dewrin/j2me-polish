/*
 * Created on 01-Mar-2004 at 23:41:07.
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
 * <p>Tests the CssReader class.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        01-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class CssReaderTest extends TestCase {

	public CssReaderTest(String name) {
		super(name);
	}
	
	public void testReadBuffer() {
		StringBuffer buffer = new StringBuffer();
		buffer.append( ".myStyle {" )
		  	  .append( "	/* defines my unique style */" )
			  .append( "	font-face: system; /* almost equivalent to font-family */" )
			  .append( "	font-size: medium;" )
			  .append( "	font-style: italic;" )
			  .append( "	font-color: blue;" )
			  .append( "	background-color: white;" )
			  .append( "}" )
			  .append( "default {" )
		  	  .append( "	/* defines my unique style */" )
			  .append( "	background {" )
			  .append( "		type: simple;" )
			  .append( "		color: fuchsia;" )
			  .append( "	}" )
			  .append( "	font-face: monospace; /* almost equivalent to font-family */" )
			  .append( "	font-size: medium;" )
			  .append( "	font-style: bold;" )
			  .append( "	font-color: black;" )
			  .append( "}" )
			  .append( ".someStyle {" )
		  	  .append( "	/* defines my unique style */" )
			  .append( "	background {" )
			  .append( "		type: simple;" )
			  .append( "		color: purple;" )
			  .append( "	}" )
			  .append( "	font: myStandardFont;" )
			  .append( "}" );
		CssReader reader = new CssReader();
		reader.add(buffer);
		StyleSheet sheet = reader.getStyleSheet();
		assertTrue( sheet.isDefined("myStyle"));
		assertTrue( sheet.isDefined("default"));
		assertTrue( sheet.isDefined("someStyle"));
		Style style = sheet.getStyle("myStyle");
		HashMap group = style.getGroup( "font"); 
		assertEquals( 4, group.size() );
		assertEquals("system", group.get("face"));
		assertEquals("medium", group.get("size"));
		assertEquals("italic", group.get("style"));
		assertEquals("blue", group.get("color"));
		StyleSheet copy = reader.getStyleSheetCopy();
		style = copy.getStyle("myStyle");
		group = style.getGroup( "font"); 
		assertEquals( 4, group.size() );
		assertEquals("system", group.get("face"));
		assertEquals("medium", group.get("size"));
		assertEquals("italic", group.get("style"));
		assertEquals("blue", group.get("color"));

		style = sheet.getStyle("someStyle");
		group = style.getGroup( "font"); 
		assertEquals( 1, group.size() );
		assertEquals("myStandardFont", group.get("font"));
		
		// add some other CSS declarations:
		buffer = new StringBuffer();
		buffer.append( ".myStyle {" )
		  	  .append( "	/* defines my unique style */" )
			  .append( "	font-face: proportional; /* almost equivalent to font-family */" )
			  .append( "	font-color: navy;" )
			  .append( "}" )
			  .append( "default {" )
		  	  .append( "	/* defines my default unique style */" )
			  .append( "	background {" )
			  .append( "		color: white;" )
			  .append( "	}" )
			  .append( "	font-color: olive;" )
			  .append( "}" );
		reader.add(buffer);
		sheet = reader.getStyleSheet();
		assertTrue( sheet.isDefined("myStyle"));
		assertTrue( sheet.isDefined("default"));
		style = sheet.getStyle("myStyle");
		group = style.getGroup( "font"); 
		assertEquals( 4, group.size() );
		assertEquals("proportional", group.get("face"));
		assertEquals("medium", group.get("size"));
		assertEquals("italic", group.get("style"));
		assertEquals("navy", group.get("color"));
		// check copy:
		style = copy.getStyle("myStyle");
		group = style.getGroup( "font"); 
		assertEquals( 4, group.size() );
		assertEquals("system", group.get("face"));
		assertEquals("medium", group.get("size"));
		assertEquals("italic", group.get("style"));
		assertEquals("blue", group.get("color"));
		
		// check invalid inheritance:
		buffer = new StringBuffer();
		buffer.append( ".myStyle {" )
		  	  .append( "	/* defines my unique style */" )
			  .append( "	font-face: proportional; /* almost equivalent to font-family */" )
			  .append( "	font-color: navy;" )
			  .append( "}" )
			  .append( "default extends myStyle {" )
		  	  .append( "	/* defines my default unique style */" )
			  .append( "	background {" )
			  .append( "		color: white;" )
			  .append( "	}" )
			  .append( "	font-color: olive;" )
			  .append( "}" );
		try {
			reader.add(buffer);
			fail("CssReader should not allow that the default style extends another style.");
		} catch (BuildException e) {
			//expected behaviour
			//e.printStackTrace();
		}
		
	}
	
	public void testRemoveComments() {
		StringBuffer buffer = new StringBuffer();
		buffer.append( "myStyle {" )
		  	  .append( "	/* defines my unique style */" )
			  .append( "	font-face: system; /* almost equivalent to font-family */" )
			  .append( "	font-size: medium;" )
			  .append( "	font-style: italic;" )
			  .append( "	font-color: blue;" )
			  .append( "}" );
		StringBuffer compare = new StringBuffer();
		compare.append( "myStyle {" )
		  	   .append( "	" )
			   .append( "	font-face: system; " )
			   .append( "	font-size: medium;" )
			   .append( "	font-style: italic;" )
			   .append( "	font-color: blue;" )
			   .append( "}" );
		StringBuffer clean = CssReader.removeCssComments(buffer);
		assertEquals( compare.toString(), clean.toString() );
		
		buffer = new StringBuffer();
		buffer.append( "/* a comment at the start  */myStyle {" )
		  	  .append( "	/* defines my unique style */" )
			  .append( "	font-face: system; /* almost equivalent to font-family */" )
			  .append( "	font-size: medium;" )
			  .append( "/*this comment will also be removed */	font-style: italic;" )
			  .append( "	font-color: blue;" )
			  .append( "}/**** some other / comment dude!!! ***/" );
		compare = new StringBuffer();
		compare.append( "myStyle {" )
		  	   .append( "	" )
			   .append( "	font-face: system; " )
			   .append( "	font-size: medium;" )
			   .append( "	font-style: italic;" )
			   .append( "	font-color: blue;" )
			   .append( "}" );
		clean = CssReader.removeCssComments(buffer);
		assertEquals( compare.toString(), clean.toString() );
		
		// check illegal comments:
		// comment is not closed at the end:
		buffer = new StringBuffer();
		buffer.append( "/* a comment at the start  */myStyle {" )
	  	  .append( "	/* defines my unique style */" )
		  .append( "	font-face: system; /* almost equivalent to font-family */" )
		  .append( "	font-size: medium;" )
		  .append( "/*this comment will also be removed */	font-style: italic;" )
		  .append( "	font-color: blue;" )
		  .append( "}/**** some other / comment dude!!! ***" );
		try {
			CssReader.removeCssComments(buffer);
			fail("CssReader should fail while removing invalid comments in CSS-code: " + buffer.toString() );
		} catch (BuildException e) {
			//System.out.println( e.getMessage() );
			// expected behaviour
		}
		
		// comment is not closed at the start:
		buffer = new StringBuffer();
		buffer.append( "/* a comment at the start / myStyle {" )
	  	  .append( "	/* defines my unique style */" )
		  .append( "	font-face: system; /* almost equivalent to font-family */" )
		  .append( "	font-size: medium;" )
		  .append( "/*this comment will also be removed */	font-style: italic;" )
		  .append( "	font-color: blue;" )
		  .append( "}/**** some other / comment dude!!! ***/" );
		try {
			CssReader.removeCssComments(buffer);
			fail("CssReader should fail while removing invalid comments in CSS-code: " + buffer.toString() );
		} catch (BuildException e) {
			//System.out.println( e.getMessage() );
			// expected behaviour
		}
		
		// comment is not opened but only closed:
		buffer = new StringBuffer();
		buffer.append( "a comment at the start */ myStyle {" )
	  	  .append( "	/* defines my unique style */" )
		  .append( "	font-face: system; /* almost equivalent to font-family */" )
		  .append( "	font-size: medium;" )
		  .append( "/*this comment will also be removed */	font-style: italic;" )
		  .append( "	font-color: blue;" )
		  .append( "}/**** some other / comment dude!!! ***/" );
		try {
			CssReader.removeCssComments(buffer);
			fail("CssReader should fail while removing invalid comments in CSS-code: " + buffer.toString() );
		} catch (BuildException e) {
			//System.out.println( e.getMessage() );
			// expected behaviour
		}
	}
	
	public void testSplitCss() {
		StringBuffer buffer = new StringBuffer();
		buffer.append( "myStyle {" )
			   .append( "	font-face: system; " )
			   .append( "	font-size: medium;" )
			   .append( "	font-style: italic;" )
			   .append( "	font-color: blue;" )
			   .append( "}" );
		String[] chunks = CssReader.split(buffer);
		assertEquals( 1, chunks.length );
		assertEquals("myStyle {	font-face: system; 	font-size: medium;	font-style: italic;	font-color: blue;}", chunks[0]);
		
		buffer.append( "default {" )
		   .append( "	font {"  )
		   .append( "		face: system; " )
		   .append( "		size: medium;" )
		   .append( "		style: bold;" )
		   .append( "		color: blue;" )
		   .append( "	}" )
		   .append( "	background-color: white;" )
		   .append( "}" );
		chunks = CssReader.split(buffer);
		assertEquals( 2, chunks.length );
		assertEquals("myStyle {	font-face: system; 	font-size: medium;	font-style: italic;	font-color: blue;}", chunks[0]);
		assertEquals("default {	font {		face: system; 		size: medium;		style: bold;		color: blue;	}	background-color: white;}", chunks[1]);
		
		// check invalid CSS code:
		buffer = new StringBuffer();
		buffer.append( "myStyle {" )
			   .append( "	font-face: system; " )
			   .append( "	font-size: medium;" )
			   .append( "	font-style: italic;" )
			   .append( "	font-color: blue;" )
			   .append( "" );
		try {
			CssReader.split(buffer);
			fail("CssReader.split should fail for invalid code: " + buffer.toString() );
		} catch (BuildException e ) {
			//System.out.println( e.getMessage() );
			// expected behaviour
		}
		
		buffer = new StringBuffer();
		buffer.append( "myStyle {" )
			   .append( "	font-face: system; " )
			   .append( "	font-size: medium;" )
			   .append( "	font-style: italic;" )
			   .append( "	font-color: blue;" )
			   .append( "}}" );
		try {
			CssReader.split(buffer);
			fail("CssReader.split should fail for invalid code: " + buffer.toString() );
		} catch (BuildException e ) {
			//System.out.println( e.getMessage() );
			// expected behaviour
		}
		
	}
}
