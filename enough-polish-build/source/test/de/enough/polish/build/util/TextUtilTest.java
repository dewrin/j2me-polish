package de.enough.polish.build.util;
import junit.framework.TestCase;

/*
 * Created on 14-Jan-2004 at 14:24:09.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */

/**
 * <p>Tests the TextUtil class.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        14-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class TextUtilTest extends TestCase {

	public TextUtilTest(String name) {
		super(name);
	}
	
	public void testReplace() {
		String original = "123456";
		String replacement = TextUtil.replace( original, "7", "9" );
		assertEquals( "123456", replacement );
		replacement = TextUtil.replace( original, "1234", "" );
		assertEquals( "56", replacement );
		replacement = TextUtil.replace( original, "1234", "a" );
		assertEquals( "a56", replacement );
		replacement = TextUtil.replace( original, "1234", "abcd" );
		assertEquals( "abcd56", replacement );
		replacement = TextUtil.replace( original, "1234", "abcdef" );
		assertEquals( "abcdef56", replacement );
		replacement = TextUtil.replace( original, "234", "" );
		assertEquals( "156", replacement );
		replacement= TextUtil.replace( original, "234", "a" );
		assertEquals( "1a56", replacement );
		replacement= TextUtil.replace( original, "234", "abc" );
		assertEquals( "1abc56", replacement );
		replacement= TextUtil.replace( original, "234", "abce" );
		assertEquals( "1abce56", replacement );
		replacement= TextUtil.replace( original, "6", "" );
		assertEquals( "12345", replacement );
		replacement= TextUtil.replace( original, "6", "a" );
		assertEquals( "12345a", replacement );
		replacement= TextUtil.replace( original, "6", "abc" );
		assertEquals( "12345abc", replacement );
		
		// now the same with two replacements:
		original += original;
		replacement = TextUtil.replace( original, "1234", "" );
		assertEquals( "5656", replacement );
		replacement = TextUtil.replace( original, "1234", "a" );
		assertEquals( "a56a56", replacement );
		replacement = TextUtil.replace( original, "1234", "abcd" );
		assertEquals( "abcd56abcd56", replacement );
		replacement = TextUtil.replace( original, "1234", "abcdef" );
		assertEquals( "abcdef56abcdef56", replacement );
		replacement = TextUtil.replace( original, "234", "" );
		assertEquals( "156156", replacement );
		replacement= TextUtil.replace( original, "234", "a" );
		assertEquals( "1a561a56", replacement );
		replacement= TextUtil.replace( original, "234", "abc" );
		assertEquals( "1abc561abc56", replacement );
		replacement= TextUtil.replace( original, "234", "abce" );
		assertEquals( "1abce561abce56", replacement );
		replacement= TextUtil.replace( original, "6", "" );
		assertEquals( "1234512345", replacement );
		replacement= TextUtil.replace( original, "6", "a" );
		assertEquals( "12345a12345a", replacement );
		replacement= TextUtil.replace( original, "6", "abc" );
		assertEquals( "12345abc12345abc", replacement );		
	}
	
	public void testSplitWithChar() {
		String original = "one;two;three";
		String[] array = TextUtil.split( original, ';' );
		assertEquals( 3, array.length );
		assertEquals( "one", array[0] );
		assertEquals( "two", array[1] );
		assertEquals( "three", array[2] );
		original += ";";
		array = TextUtil.split( original, ';' );
		assertEquals( 4, array.length );
		assertEquals( "one", array[0] );
		assertEquals( "two", array[1] );
		assertEquals( "three", array[2] );
		assertEquals( "", array[3] );
		original = ";" + original;
		array = TextUtil.split( original, ';' );
		assertEquals( 5, array.length );
		assertEquals( "", array[0] );
		assertEquals( "one", array[1] );
		assertEquals( "two", array[2] );
		assertEquals( "three", array[3] );
		assertEquals( "", array[4] );
		original = ";";
		array = TextUtil.split( original, ';' );
		assertEquals( 2, array.length );
		assertEquals( "", array[0] );
		assertEquals( "", array[1] );
		original = "one;two;three";
		array = TextUtil.split( original, ',' );
		assertEquals( 1, array.length );
		assertEquals( "one;two;three", array[0] );
	}

	public void testSplitWithString() {
		String original = "one;;two;;three";
		String[] array = TextUtil.split( original, ";;" );
		assertEquals( 3, array.length );
		assertEquals( "one", array[0] );
		assertEquals( "two", array[1] );
		assertEquals( "three", array[2] );
		original += ";;";
		array = TextUtil.split( original, ";;" );
		assertEquals( 4, array.length );
		assertEquals( "one", array[0] );
		assertEquals( "two", array[1] );
		assertEquals( "three", array[2] );
		assertEquals( "", array[3] );
		original = ";;" + original;
		array = TextUtil.split( original, ";;" );
		assertEquals( 5, array.length );
		assertEquals( "", array[0] );
		assertEquals( "one", array[1] );
		assertEquals( "two", array[2] );
		assertEquals( "three", array[3] );
		assertEquals( "", array[4] );
		original = ";;";
		array = TextUtil.split( original, ";;" );
		assertEquals( 2, array.length );
		assertEquals( "", array[0] );
		assertEquals( "", array[1] );
		original = "one;two;three";
		array = TextUtil.split( original, ";" );
		assertEquals( 3, array.length );
		assertEquals( "one", array[0] );
		assertEquals( "two", array[1] );
		assertEquals( "three", array[2] );
		array = TextUtil.split( original, ";;" );
		assertEquals( 1, array.length );
		assertEquals( "one;two;three", array[0] );
	}
	
}
