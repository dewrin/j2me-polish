/*
 * Created on 16-Feb-2004 at 13:33:54.
 *
 * Copyright (c) 2004 Robert Virkus / enough software
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
 * www.enough.de/j2mepolish for details.
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
