/*
 * Created on 16-Feb-2004 at 14:51:26.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.requirements;

import junit.framework.TestCase;

/**
 * <p>Tests the JavaPlatformRequirement.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        16-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class JavaPlatformRequirementTest extends TestCase {
	
	public JavaPlatformRequirementTest(String name) {
		super(name);
	}
	
	public void testIsMet() {
		JavaPlatformRequirement req = new JavaPlatformRequirement("MIDP/1.0+");
		assertTrue( req.isMet(null, "MIDP/1.0"));
		assertTrue( req.isMet(null, "MIDP/1.1"));
		assertTrue( req.isMet(null, "MIDP/1.9"));
		assertFalse( req.isMet(null, "MIDP/2.0"));
		
		req = new JavaPlatformRequirement("MIDP/1+.0+");
		assertTrue( req.isMet(null, "MIDP/1.0"));
		assertTrue( req.isMet(null, "MIDP/1.1"));
		assertTrue( req.isMet(null, "MIDP/1.9"));
		assertTrue( req.isMet(null, "MIDP/2.0"));
		
		req = new JavaPlatformRequirement("MIDP/2.0+");
		assertFalse( req.isMet(null, "MIDP/1.0"));
		assertFalse( req.isMet(null, "MIDP/1.9"));
		assertTrue( req.isMet(null, "MIDP/2.0"));
		assertTrue( req.isMet(null, "MIDP/2.1"));
		
		req = new JavaPlatformRequirement("MIDP/2.0");
		assertFalse( req.isMet(null, "MIDP/1.0"));
		assertFalse( req.isMet(null, "MIDP/1.9"));
		assertTrue( req.isMet(null, "MIDP/2.0"));
		assertFalse( req.isMet(null, "MIDP/2.1"));
	}
}
