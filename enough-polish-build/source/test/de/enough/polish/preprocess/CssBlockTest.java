/*
 * Created on 02-Mar-2004 at 18:19:55.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.preprocess;

import org.apache.tools.ant.BuildException;

import java.util.HashMap;

import junit.framework.TestCase;

/**
 * <p>Tests the CssBlock class</p>
 *
 * <p>copyright enough software 2004</p>
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
