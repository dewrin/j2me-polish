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
 * <p>Tests the colors class.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        01-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class ColorsTest extends TestCase {
	
	public ColorsTest(String name) {
		super(name);
	}
	
	public void testHexColors() {
		Colors colors = new Colors();
		String definition = "#FF0000";
		String value = colors.parseColor(definition);
		assertEquals("0xFF0000", value );
		
		definition = "#F00";
		value = colors.parseColor(definition);
		assertEquals("0xFF0000", value );
		
		if (false) {
			//TODO include ARGB test
		definition = "#99FF0055";
		value = colors.parseColor(definition);
		assertEquals("0x99FF0055", value );
		}
		
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
			System.out.println( e.getMessage() );
			// expected behaviour
		}
	}
	
	public void testVgaColors() {
		Colors colors = new Colors();
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
		Colors colors = new Colors();
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
			System.out.println( e.getMessage() );
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

}
