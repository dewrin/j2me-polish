/*
 * Created on 01-Mar-2004 at 21:41:58.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.preprocess;

import org.apache.tools.ant.BuildException;

import junit.framework.TestCase;

/**
 * <p>Tests the ColorConverter class.</p>
 *
 * <p>copyright enough software 2004</p>
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
