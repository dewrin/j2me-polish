/*
 * Created on 11-Feb-2004 at 20:27:56.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.requirements;

import de.enough.polish.Device;

/**
 * <p>Selects a device by a memory capability.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        11-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class MemoryRequirement extends Requirement {

	private MemoryMatcher matcher;

	/**
	 * Creates a new memory requirement.
	 * 
	 * @param value the needed memory, e.g. "120+ kb"
	 * @param propertyName the name of the memory-capability, e.g. "HeapSize"
	 */
	public MemoryRequirement(String value, String propertyName) {
		super(value, propertyName);
		this.matcher = new MemoryMatcher( value ); 
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ant.requirements.Requirement#isMet(de.enough.polish.Device, java.lang.String)
	 */
	protected boolean isMet(Device device, String property) {
		return this.matcher.matches( property );
	}

}
