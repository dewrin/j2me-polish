/*
 * Created on 02-Mar-2004 at 20:35:37.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.preprocess;

import org.apache.tools.ant.BuildException;

import java.util.HashMap;

import junit.framework.TestCase;

/**
 * <p>Tests the StyleSheet class.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        02-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class StyleSheetTest extends TestCase {
	
	public StyleSheetTest(String name) {
		super(name);
	}
	
	public void testOneStyle() {
		StringBuffer buffer = new StringBuffer();
		buffer.append( ".myStyle {" )
			   .append( "	font-face: system; " )
			   .append( "	font-size: medium;" )
			   .append( "	font-style: italic;" )
			   .append( "	font-color: blue;" )
			   .append( "}" );
		CssBlock block = new CssBlock( buffer.toString() );
		StyleSheet sheet = new StyleSheet();
		sheet.addCssBlock(block);
		Style style = sheet.getStyle( "myStyle" );
		HashMap group = style.getGroup( "font"); 
		assertEquals( 4, group.size() );
		assertEquals("system", group.get("face"));
		assertEquals("medium", group.get("size"));
		assertEquals("italic", group.get("style"));
		assertEquals("blue", group.get("color"));
		
		buffer = new StringBuffer();
		buffer.append( ".myStyle {" )
			   .append( "	font-face: proportional; " )
			   .append( "}" );
		block = new CssBlock( buffer.toString() );
		sheet.addCssBlock(block);
		buffer = new StringBuffer();
		buffer.append( "default {" )
		   .append( "	font-face: monospace; " )
		   .append( "	font-color: black;" )
		   .append( "}" );
		block = new CssBlock( buffer.toString() );
		sheet.addCssBlock(block);
		
		style = sheet.getStyle( "myStyle" );
		group = style.getGroup( "font"); 
		assertEquals( 4, group.size() );
		assertEquals("proportional", group.get("face"));
		assertEquals("medium", group.get("size"));
		assertEquals("italic", group.get("style"));
		assertEquals("blue", group.get("color"));
		
		style = sheet.getStyle( "default" );
		group = style.getGroup( "font"); 
		assertEquals( 2, group.size() );
		assertEquals("monospace", group.get("face"));
		assertEquals("black", group.get("color"));

	}
	
	public void testInherit() {
		StringBuffer buffer = new StringBuffer();
		buffer.append( ".myStyle {" )
			   .append( "	font-face: system; " )
			   .append( "	font-size: medium;" )
			   .append( "	font-style: italic;" )
			   .append( "}" );
		CssBlock block = new CssBlock( buffer.toString() );
		StyleSheet sheet = new StyleSheet();
		sheet.addCssBlock(block);
		Style style = sheet.getStyle( "myStyle" );
		HashMap group = style.getGroup( "font"); 
		assertEquals( 3, group.size() );
		assertEquals("system", group.get("face"));
		assertEquals("medium", group.get("size"));
		assertEquals("italic", group.get("style"));
		
		buffer = new StringBuffer();
		buffer.append( "myStyle {" )
			   .append( "	font-face: proportional; " )
			   .append( "	font-size: large;" )
			   .append( "}" );
		block = new CssBlock( buffer.toString() );
		sheet.addCssBlock(block);
		style = sheet.getStyle( "myStyle" );
		group = style.getGroup( "font"); 
		assertEquals( 3, group.size() );
		assertEquals("proportional", group.get("face"));
		assertEquals("large", group.get("size"));
		assertEquals("italic", group.get("style"));
		
		buffer = new StringBuffer();
		buffer.append( "default {" )
			  .append( "	font-face: monospace; " )
			  .append( "	font-color: black;" )
			  .append( "	border-color: yellow;" )
			  .append( "}" );
		block = new CssBlock( buffer.toString() );
		sheet.addCssBlock(block);
		// myStyle should not have changed yet:
		style = sheet.getStyle( "myStyle" );
		group = style.getGroup( "font"); 
		assertEquals( 3, group.size() );
		assertEquals("proportional", group.get("face"));
		assertEquals("large", group.get("size"));
		assertEquals("italic", group.get("style"));
		
		// now set the default style as the parent:
		sheet.inherit();
		
		style = sheet.getStyle( "myStyle" );
		group = style.getGroup( "font"); 
		assertEquals( 4, group.size() );
		assertEquals("proportional", group.get("face"));
		assertEquals("large", group.get("size"));
		assertEquals("italic", group.get("style"));
		assertEquals("black", group.get("color"));

		// now try invalid declarations:
		buffer = new StringBuffer();
		buffer.append( "someStyle extends otherStyle {" )
			  .append( "	font-face: monospace; " )
			  .append( "	font-color: black;" )
			  .append( "	border-color: yellow;" )
			  .append( "}" );
		block = new CssBlock( buffer.toString() );
		sheet.addCssBlock(block);
		buffer = new StringBuffer();
		buffer.append( "otherStyle extends thirdStyle {" )
			  .append( "	font-face: monospace; " )
			  .append( "	font-color: black;" )
			  .append( "	border-color: yellow;" )
			  .append( "}" );
		block = new CssBlock( buffer.toString() );
		sheet.addCssBlock(block);
		buffer = new StringBuffer();
		buffer.append( "thirdStyle extends  someStyle {" )
		  .append( "	font-face: monospace; " )
		  .append( "	font-color: black;" )
		  .append( "	border-color: yellow;" )
		  .append( "}" );
		block = new CssBlock( buffer.toString() );
		sheet.addCssBlock(block);
		try {
			sheet.inherit();
			fail( "inherit() should fail when circle inheritance is encountered.");
		} catch (BuildException e) {
			//e.printStackTrace();
			// expected behaviour 
		}
		
		// extending a style that does not exist:
		sheet = new StyleSheet();
		buffer = new StringBuffer();
		buffer.append( "myStyle extends otherStyle {" )
			   .append( "	font-face: proportional; " )
			   .append( "	font-size: large;" )
			   .append( "}" );
		block = new CssBlock( buffer.toString() );
		sheet.addCssBlock(block);
		try {
			sheet.inherit();
			fail( "inherit() should fail when non-existing parent is encountered.");
		} catch (BuildException e) {
			//e.printStackTrace();
			// expected behaviour 
		}
		
	}
}