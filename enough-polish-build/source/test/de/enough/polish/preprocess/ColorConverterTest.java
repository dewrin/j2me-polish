/*
 * Created on 01-Mar-2004 at 21:41:58.
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
 * <p>Tests the ColorConverter class.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        01-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class ColorConverterTest extends TestCase {
	
	public ColorConverterTest(String name) {
		super(name);
	}
	
	public void testHexColors() {
		ColorConverter colors = new ColorConverter();
		String definition = "#FF0000";
		String value = colors.parseColor(definition);
		assertEquals("0xFF0000", value );
		
		definition = "#F00";
		value = colors.parseColor(definition);
		assertEquals("0xFF0000", value );
		
		definition = "#99FF0055";
		value = colors.parseColor(definition);
		assertEquals("0x99FF0055", value );
		
		definition = "#DD99FF0055"; // too long
		try {
			colors.parseColor(definition);
			fail("colors.parseColor(" + definition + ") should fail." ); 
		} catch (BuildException e) {
			//System.out.println( e.getMessage() );
			// expected behaviour
		}
		
		definition = "#F0055"; // too short
		try {
			colors.parseColor(definition);
			fail("colors.parseColor(" + definition + ") should fail." ); 
		} catch (BuildException e) {
			//System.out.println( e.getMessage() );
			// expected behaviour
		}
		
		definition = "#FF00G5"; // invalid number
		try {
			colors.parseColor(definition);
			fail("colors.parseColor(" + definition + ") should fail." ); 
		} catch (BuildException e) {
			//System.out.println( e.getMessage() );
			// expected behaviour
		}
	}
	
	public void testVgaColors() {
		ColorConverter colors = new ColorConverter();
		String definition = "black";
		String value = colors.parseColor(definition);
		assertEquals("0x000000", value );
		
		definition = "red";
		value = colors.parseColor(definition);
		assertEquals("0xFF0000", value );
		
		definition = "green";
		value = colors.parseColor(definition);
		assertEquals("0x008000", value );
		
		definition = "lime";
		value = colors.parseColor(definition);
		assertEquals("0x00FF00", value );
		
		definition = "blue";
		value = colors.parseColor(definition);
		assertEquals("0x0000FF", value );
		
		definition = "limes";
		try {
			colors.parseColor(definition);
			fail("colors.parseColor(" + definition + ") should fail." ); 
		} catch (BuildException e) {
			//System.out.println( e.getMessage() );
			// expected behaviour
		}
		
	}

	public void testRgbColors() {
		ColorConverter colors = new ColorConverter();
		String definition = "rgb( 0, 0, 0)";
		String value = colors.parseColor(definition);
		assertEquals("0x000000", value );
		
		definition = "rgb  (  255, 0, 0 )";
		value = colors.parseColor(definition);
		assertEquals("0xff0000", value );
		
		definition = "rgb( 0, 128,    0   ) ";
		value = colors.parseColor(definition);
		assertEquals("0x008000", value );
		
		definition = "rgb  (0,255,0)";
		value = colors.parseColor(definition);
		assertEquals("0x00ff00", value );
		
		definition = "rgb(0,  0,  255   )  ";
		value = colors.parseColor(definition);
		assertEquals("0x0000ff", value );
		
		definition = "rgb(0%,  0%,  100%   )  ";
		value = colors.parseColor(definition);
		assertEquals("0x0000ff", value );

		definition = "rgb(0.00%,  0.00%,  100.00%   )  ";
		value = colors.parseColor(definition);
		assertEquals("0x0000ff", value );

		
		definition = "rgb(0)";		
		try {
			colors.parseColor(definition);
			fail("colors.parseColor(" + definition + ") should fail." ); 
		} catch (BuildException e) {
			//System.out.println( e.getMessage() );
			// expected behaviour
		}
		
		definition = "rgb(0,10)";
		try {
			colors.parseColor(definition);
			fail("colors.parseColor(" + definition + ") should fail." ); 
		} catch (BuildException e) {
			//System.out.println( e.getMessage() );
			// expected behaviour
		}
		
		definition = "rgb(0,10,.9)";
		try {
			colors.parseColor(definition);
			fail("colors.parseColor(" + definition + ") should fail." ); 
		} catch (BuildException e) {
			//System.out.println( e.getMessage() );
			// expected behaviour
		}
		
		definition = "rgb(0,10,256)";
		try {
			colors.parseColor(definition);
			fail("colors.parseColor(" + definition + ") should fail." ); 
		} catch (BuildException e) {
			//System.out.println( e.getMessage() );
			// expected behaviour
		}
		
		definition = "rgb(0,-10,255)";
		try {
			colors.parseColor(definition);
			fail("colors.parseColor(" + definition + ") should fail." ); 
		} catch (BuildException e) {
			//System.out.println( e.getMessage() );
			// expected behaviour
		}
	}

	public void testArgbColors() {
		ColorConverter colors = new ColorConverter();
		String definition = "argb( 0, 0, 0, 0)";
		String value = colors.parseColor(definition);
		assertEquals("0x00000000", value );
		
		definition = "argb  (  128, 255, 0, 0 )";
		value = colors.parseColor(definition);
		assertEquals("0x80ff0000", value );
		
		definition = "argb( 255, 0, 128,    0   ) ";
		value = colors.parseColor(definition);
		assertEquals("0xff008000", value );
				
		definition = "argb(50%, 0%,  0%,  100%   )  ";
		value = colors.parseColor(definition);
		assertEquals("0x7f0000ff", value );

		definition = "argb(50.00%, 0.00%,  0.00%,  100.00%   )  ";
		value = colors.parseColor(definition);
		assertEquals("0x7f0000ff", value );

		
		definition = "argb(0)";		
		try {
			colors.parseColor(definition);
			fail("colors.parseColor(" + definition + ") should fail." ); 
		} catch (BuildException e) {
			//System.out.println( e.getMessage() );
			// expected behaviour
		}
		
		definition = "argb(0,10)";
		try {
			colors.parseColor(definition);
			fail("colors.parseColor(" + definition + ") should fail." ); 
		} catch (BuildException e) {
			//System.out.println( e.getMessage() );
			// expected behaviour
		}
		
		definition = "argb(0,10,9)";
		try {
			colors.parseColor(definition);
			fail("colors.parseColor(" + definition + ") should fail." ); 
		} catch (BuildException e) {
			//System.out.println( e.getMessage() );
			// expected behaviour
		}
		
		definition = "argb(0,10,256)";
		try {
			colors.parseColor(definition);
			fail("colors.parseColor(" + definition + ") should fail." ); 
		} catch (BuildException e) {
			//System.out.println( e.getMessage() );
			// expected behaviour
		}
		
		definition = "argb(10, 0,-10,255)";
		try {
			colors.parseColor(definition);
			fail("colors.parseColor(" + definition + ") should fail." ); 
		} catch (BuildException e) {
			//System.out.println( e.getMessage() );
			// expected behaviour
		}
	}
	
}
