/*
 * Created on 16-Feb-2004 at 14:41:21.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.requirements;

import de.enough.polish.Device;

/**
 * <p>Selects a device by the available memory for the heap.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        16-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class HeapSizeRequirement extends MemoryRequirement {
	
	/**
	 * Creates a new requirement for the heap size.
	 * 
	 * @param value The needed heap size, e.g. "200+ kb"
	 */
	public HeapSizeRequirement(String value) {
		super(value, Device.HEAP_SIZE );
	}
}
