/*
 * Created on 09-Feb-2004 at 13:40:12.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.requirements;

import de.enough.polish.Device;
import de.enough.polish.util.TextUtil;

/**
 * <p>Selects a device by one of several of its features.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        09-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class FeatureRequirement extends Requirement {
	
	private String[] features;

	/**
	 * Creates a new requirement for a device feature.
	 * 
	 * @param value the feature(s) which needs to be defined, e.g. "hardware.camera".
	 *              When there are several features needed, they need to be seperated by commas.
	 */
	public FeatureRequirement(String value ) {
		super(value, "Features");
		this.features = TextUtil.splitAndTrim( value, ',');
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ant.requirements.Requirement#isMet(de.enough.polish.Device, java.lang.String)
	 */
	protected boolean isMet(Device device, String property) {
		// this is not needed, since we overried isMet(Device ) already.
		return false;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ant.requirements.Requirement#isMet(de.enough.polish.Device)
	 */
	public boolean isMet(Device device) {
		for (int i = 0; i < this.features.length; i++) {
			String feature = this.features[i];
			if (!device.hasFeature(feature)) {
				return false;
			}
		}
		return true;
	}

}
