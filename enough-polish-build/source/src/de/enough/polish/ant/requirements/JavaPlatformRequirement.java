/*
 * Created on 16-Feb-2004 at 13:33:54.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.requirements;

import de.enough.polish.Device;

import org.apache.tools.ant.BuildException;

/**
 * <p>Selects a device by the supported platform. A platform is for example "MIDP/1.0"</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        16-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class JavaPlatformRequirement extends Requirement {
	
	private String platformName;
	private VersionMatcher platformVersion;

	/**
	 * Creates a new requirement for the java platform of a device.
	 * 
	 * @param value The value of the platform
	 */
	public JavaPlatformRequirement(String value ) {
		super(value, Device.JAVA_PLATFORM );
		int splitPos = value.indexOf('/');
		if (splitPos == -1) {
			throw new BuildException("The JavaPlatform requirement needs to specify the " +
					"name and the version of the platform in the following form: " +
					"\"[name]/[version]\" - e.g. \"MIDP/1.0+\".");
		}
		this.platformName = value.substring(0, splitPos ).trim();
		this.platformVersion = new VersionMatcher( value.substring(splitPos + 1).trim() );
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ant.requirements.Requirement#isMet(de.enough.polish.Device, java.lang.String)
	 */
	protected boolean isMet(Device device, String property) {
		int splitPos = property.indexOf('/');
		if (splitPos == -1) {
			return false;
		}
		String devPlatformName = property.substring(0, splitPos ).trim();
		String devPlatformVersion = property.substring(splitPos +1 ).trim();
		return this.platformName.equals(devPlatformName)
			&& this.platformVersion.matches(devPlatformVersion);
	}
}
