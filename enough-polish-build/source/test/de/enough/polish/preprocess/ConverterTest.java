/*
 * Created on 14-Mar-2004 at 22:15:12.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.preprocess;

import org.apache.tools.ant.BuildException;

import junit.framework.TestCase;

/**
 * <p>Tests the Converter class.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        14-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class ConverterTest extends TestCase {
	public ConverterTest(String name) {
		super(name);
	}
	
	public void testGetUrl() {
		String url = "url ( test.png )";
		assertEquals("/test.png", Converter.getUrl(url));
		url = "url ( /test.png )";
		assertEquals("/test.png", Converter.getUrl(url));
		url = "test.png";
		assertEquals("/test.png", Converter.getUrl(url));
		url = "/test.png";
		assertEquals("/test.png", Converter.getUrl(url));
	}
	
	public void testGetInt() {
		String number = "23";
		assertEquals( 23, Converter.parseInt("style", "background", "color", number));
		try {
			number = "23.1";
			Converter.parseInt("style", "background", "color", number);
			fail( "Converter should not parse the invalid integer [" + number + "].");
		} catch (BuildException e) {
			// expected behaviour
		}
		try {
			number = "23,1";
			Converter.parseInt("style", "background", "color", number);
			fail( "Converter should not parse the invalid integer [" + number + "].");
		} catch (BuildException e) {
			// expected behaviour
		}
		try {
			number = "asd";
			Converter.parseInt("style", "background", "color", number);
			fail( "Converter should not parse the invalid integer [" + number + "].");
		} catch (BuildException e) {
			// expected behaviour
		}
	}
}
