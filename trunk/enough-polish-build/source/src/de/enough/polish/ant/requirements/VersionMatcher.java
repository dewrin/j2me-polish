/*
 * Created on 11-Feb-2004 at 23:19:52.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.requirements;

import de.enough.polish.util.TextUtil;

/**
 * <p>Checks capabilities for a specific version.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        11-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class VersionMatcher implements Matcher {
	
	private int[] versionNumbers;
	private boolean[] equalsOrGreater;

	/**
	 * Creates a new version matcher.
	 * 
	 * @param value the needed version, e.g. "1.3+" or "1.3+.2+"
	 */
	public VersionMatcher( String value ) {
		String[] chunks = TextUtil.splitAndTrim( value, '.' );
		this.versionNumbers = new int[ chunks.length ];
		this.equalsOrGreater = new boolean[ chunks.length ];
		for (int i = 0; i < chunks.length; i++) {
			IntegerMatcher matcher = new IntegerMatcher( chunks[i] );
			this.versionNumbers[i] = matcher.number;
			this.equalsOrGreater[i] = matcher.equalsOrGreater;
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ant.requirements.Matcher#matches(java.lang.String)
	 */
	public boolean matches(String deviceValue) {
		String[] chunks = TextUtil.splitAndTrim( deviceValue, '.' );
		int min = Math.min( chunks.length, this.versionNumbers.length );
		int diff = this.versionNumbers.length - chunks.length; 
		boolean carryFlag = false;
		for (int i = 0; i < min; i++) {
			int deviceNumber = Integer.parseInt( chunks[i] );
			int neededNumber = this.versionNumbers[i];
			boolean eqOrGreater = this.equalsOrGreater[i];
			if (deviceNumber < neededNumber && !carryFlag ) {
				return false;
			} else if (deviceNumber > neededNumber) {
				if (eqOrGreater) {
					carryFlag = true;
				} else {
					return false;
				}
			}
		}
		if (diff > 0) {
			// when there are more needed version chunks than the device specifies,
			// the version only matches, if the last compared version-chunk was greater
			// than the needed one.
			// example 1: needed="1.2+.3+" given="1.2" would return false
			// example 2: needed="1.2+.3+" given="1.3" would return true
			return carryFlag;
		} else {
			return true;
		}
	}

}
