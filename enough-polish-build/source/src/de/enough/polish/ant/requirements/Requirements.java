/*
 * Created on 24-Jan-2004 at 00:21:06.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.requirements;

import de.enough.polish.Device;
import de.enough.polish.ant.ConditionalElement;

import org.apache.tools.ant.Project;

import java.util.ArrayList;

/**
 * <p>A list of requirements which each supported device needs to satisfy.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        24-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class Requirements
extends AndRelation
{
	
	ConditionalElement condition;

	/**
	 * Creates a new device requirements list.
	 */
	public Requirements() {
		super();
		this.condition = new ConditionalElement();
	}
	
	//TODO rob addConfiguredGroup( OrRelation orRelation )
	// when requirements should be given in groups
	
	
	/**
	 * Filters the available devices and only returns those which satisfy all requirements.
	 * 
	 * @param availableDevices array of all available devices.
	 * @return All devices which satisfy the requirements.
	 */
	public Device[] filterDevices( Device[] availableDevices ) {
		ArrayList deviceList = new ArrayList();
		for (int i = 0; i < availableDevices.length; i++) {
			Device device = availableDevices[i];
			if (isMet( device )) {
				// all requirements are met by this device:
				deviceList.add( device );
			}
		}
		return (Device[]) deviceList.toArray( new Device[ deviceList.size() ] );
	}

	/**
	 * Sets the ant-property which needs to be defined to allow the execution of this task.
	 *  
	 * @param ifExpr the ant-property which needs to be defined 
	 */
	public void setIf(String ifExpr) {
		this.condition.setIf( ifExpr );
	}
	
	/**
	 * Sets the ant-property which must not be defined to allow the execution of this task.
	 * 
	 * @param unlessExpr the ant-property which must not be defined 
	 */
	public void setUnless(String unlessExpr) {
		this.condition.setUnless( unlessExpr );
	}

	/**
	 * Checks if this element should be used.
	 * 
	 * @param project The project to which this nested element belongs to.
	 * @return true when this element is valid
	 */
	public boolean isActive( Project project ) {
		return this.condition.isActive( project );
	}

	
}
