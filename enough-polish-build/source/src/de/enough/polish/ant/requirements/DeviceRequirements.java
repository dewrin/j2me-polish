/*
 * Created on 24-Jan-2004 at 00:21:06.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.requirements;

import de.enough.polish.Device;
import de.enough.polish.ant.build.Variable;

import org.apache.tools.ant.BuildException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
public class DeviceRequirements {
	

	private ArrayList requirements;
	

	/**
	 * Creates a new device requirements list.
	 */
	public DeviceRequirements() {
		this.requirements = new ArrayList();
	}
	
	public void addConfiguredRequirement( Variable req ) {
		String name = req.getName(); 
		String value = req.getValue();
		if (name == null) {
			throw new BuildException("A device requirement needs to have the attribute [name] defined.");
		}
		if (value == null) {
			throw new BuildException("The device requirement [" + name + "] needs to have the attribute [value] defined.");
		}
		
		Class reqClass = null;
		try {
			reqClass = Class.forName( "de.enough.polish.ant.requirements." + name + "Requirement");
		} catch (ClassNotFoundException e) {
			if (name.startsWith("SoftwarePlatform.") || name.startsWith("HardwarePlatform.")) {
				name = name.substring( 18 );
				try {
					reqClass = Class.forName( "de.enough.polish.ant.requirements." + name + "Requirement");
				} catch (ClassNotFoundException e1) {
					throw new BuildException("The device requirelemt [" + name + "] is not supported.");
				}
			} else {
				// could be an external requirement with full path:
				try {
					reqClass = Class.forName( "de.enough.polish.ant.requirements." + name + "Requirement");
				} catch (ClassNotFoundException e1) {
					throw new BuildException("The device requirelemt [" + name + "] is not supported.");
				}
			}
		}
		try {
			Constructor reqConstructor = reqClass.getConstructor( new Class[]{ String.class } );
			Requirement requirement = (Requirement) reqConstructor.newInstance( new Object[] { value } );
			this.requirements.add( requirement );
		} catch (SecurityException e) {
			throw new BuildException( e );
		} catch (NoSuchMethodException e) {
			throw new BuildException( "The requirement-class ["+ reqClass.getName() + "] does not have the necessary constructor with the single String-parameter defined: " + e.getMessage() );
		} catch (IllegalArgumentException e) {
			throw new BuildException( e );
		} catch (InstantiationException e) {
			throw new BuildException( e );
		} catch (IllegalAccessException e) {
			throw new BuildException( e );
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof BuildException) {
				throw (BuildException) e.getTargetException();
			}
			throw new BuildException( e );
		}
	}
	
	public Requirement[] getRequirements() {
		return (Requirement[]) this.requirements.toArray( new Requirement[this.requirements.size()] );	
	}
	
	/**
	 * Filters the available devices and only returns those which satisfy all requirements.
	 * 
	 * @param availableDevices array of all available devices.
	 * @return All devices which satisfy the requirements.
	 */
	public Device[] filterDevices( Device[] availableDevices ) {
		Requirement[] reqs = getRequirements();
		ArrayList deviceList = new ArrayList();
		for (int i = 0; i < availableDevices.length; i++) {
			Device device = availableDevices[i];
			// now check all requirements:
			boolean requirementMet = true;
			for (int j = 0; j < reqs.length; j++) {
				Requirement requirement = reqs[i];
				if (! requirement.isMet( device)) {
					requirementMet = false;
					break;
				}
			}
			if (requirementMet) {
				// all requirements are met by this device:
				deviceList.add( device );
			}
		}
		return (Device[]) deviceList.toArray( new Device[ deviceList.size() ] );
	}

}
