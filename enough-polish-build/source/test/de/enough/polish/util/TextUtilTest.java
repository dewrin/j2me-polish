package de.enough.polish.util;
import junit.framework.TestCase;

/*
 * Created on 14-Jan-2004 at 14:24:09.
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

/**
 * <p>Tests the TextUtil class.</p>
 *
 * <p>copyright Enough Software 2004</p>
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
	
	public void testReplaceChar() {
		String original = "123456";
		String replacement = TextUtil.replace( original, '7', '9' );
		assertEquals( "123456", replacement );
		original = "de.enough.polish.util.TextUtil";
		replacement = TextUtil.replace( original, '.', '/');
		assertEquals("de/enough/polish/util/TextUtil", replacement );
	}
	
	public void testReplaceFirst() {
		String input = "abc defg hijkl defg abc";
		String replacement = TextUtil.replaceFirst( input, "defg", "1234" );
		assertEquals( "abc 1234 hijkl defg abc", replacement );		
		replacement = TextUtil.replaceFirst( input, "defg", "1" );
		assertEquals( "abc 1 hijkl defg abc", replacement );		
		replacement = TextUtil.replaceFirst( input, "defg", "1234567" );
		assertEquals( "abc 1234567 hijkl defg abc", replacement );
		
		replacement = TextUtil.replaceFirst( input, "abc", "123" );
		assertEquals( "123 defg hijkl defg abc", replacement );		
		replacement = TextUtil.replaceFirst( input, "abc", "1" );
		assertEquals( "1 defg hijkl defg abc", replacement );		
		replacement = TextUtil.replaceFirst( input, "abc", "1234567" );
		assertEquals( "1234567 defg hijkl defg abc", replacement );
		
		replacement = TextUtil.replaceFirst( input, "XXX", "123" );
		assertEquals( "abc defg hijkl defg abc", replacement );		
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
