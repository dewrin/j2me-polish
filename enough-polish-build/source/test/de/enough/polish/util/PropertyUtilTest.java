/*
 * Created on 18-Jan-2003 at 21:19:54.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.util;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

/**
 * <p>Tests the PropertyUtil class.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        18-Jan-2003 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class PropertyUtilTest extends TestCase {

	public PropertyUtilTest(String name) {
		super(name);
	}

	public void testPropertyPattern() {
		Pattern pattern = PropertyUtil.PROPERTY_PATTERN;
		Matcher matcher = pattern.matcher("//#include ${ polish.source-new  }/includes/sample.txt");
		assertTrue( matcher.find() );
		assertEquals( "${ polish.source-new  }", matcher.group() );
		matcher = pattern.matcher("//#include ${ /polish.source-new  }/includes/sample.txt");
		assertFalse( matcher.find() );
		matcher = pattern.matcher("//#include ${  }/includes/sample.txt");
		assertFalse( matcher.find() );
	}
	
	public void testWriteProperties1() {
		HashMap properties = new HashMap();
		properties.put( "source", "/home/user/workspace/project/src");
		properties.put( "polish.source", "/home/user/workspace/polish/source/src");
		properties.put( "polish.test-source", "/home/user/workspace/polish/source/test");
		properties.put( "test1", "T1");
		properties.put( "test2", "T2");
		properties.put( "test3", "T3");
		
		String result = PropertyUtil.writeProperties( "${source}", properties );
		assertEquals( "/home/user/workspace/project/src", result );
		
		result = PropertyUtil.writeProperties( "${polish.source}", properties );
		assertEquals( "/home/user/workspace/polish/source/src", result );
		
		result = PropertyUtil.writeProperties( "${polish.test-source}", properties );
		assertEquals( "/home/user/workspace/polish/source/test", result );
		
		result = PropertyUtil.writeProperties( "12${polish.test-source}345", properties );
		assertEquals( "12/home/user/workspace/polish/source/test345", result );
		
		result = PropertyUtil.writeProperties( "12${test1}345${ test2  }67${      test3}8", properties );
		assertEquals( "12T1345T267T38", result );
		
		result = PropertyUtil.writeProperties( "12${test1}34${  test4 }5${ test2  }67${      test3}8", properties );
		assertEquals( "12T134${  test4 }5T267T38", result );
	}

	public void testWriteProperties2() {
		HashMap properties = new HashMap();
		properties.put( "source", "/home/user/workspace/project/src");
		properties.put( "polish.source", "/home/user/workspace/polish/source/src");
		properties.put( "polish.test-source", "/home/user/workspace/polish/source/test");
		properties.put( "test1", "T1");
		properties.put( "test2", "T2");
		properties.put( "test3", "T3");
		
		String result = PropertyUtil.writeProperties( "${source}", properties, true );
		assertEquals( "/home/user/workspace/project/src", result );
		
		result = PropertyUtil.writeProperties( "${polish.source}", properties, true );
		assertEquals( "/home/user/workspace/polish/source/src", result );
		
		result = PropertyUtil.writeProperties( "${polish.test-source}", properties, true );
		assertEquals( "/home/user/workspace/polish/source/test", result );
		
		result = PropertyUtil.writeProperties( "12${polish.test-source}345", properties, true );
		assertEquals( "12/home/user/workspace/polish/source/test345", result );
		
		result = PropertyUtil.writeProperties( "12${test1}345${ test2  }67${      test3}8", properties, true );
		assertEquals( "12T1345T267T38", result );
		
		// with tabs:
		result = PropertyUtil.writeProperties( "		${ test1};", properties, true );
		assertEquals( "		T1;", result );
		
		// with space:
		result = PropertyUtil.writeProperties( "   ${ test1};", properties, true );
		assertEquals( "   T1;", result );
		
		try {
			result = PropertyUtil.writeProperties( "12${test1}34${  test4 }5${ test2  }67${      test3}8", properties, true );
			fail("writeProperties() should fail when a property is not defined and argument needsToBeDefined==true.");
		} catch (IllegalArgumentException e) {
			// expected behaviour!
		}
	}
	
}
