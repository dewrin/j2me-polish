/*
 * Created on 24-Jan-2004 at 00:23:02.
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * <p>A requirement which a supported device needs to satisfy.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        24-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public abstract class Requirement
implements DeviceFilter
{
	
	private String value;
	protected String propertyName;
	

	/**
	 * Creates a new requirement.
	 * 
	 * @param value the value of this requirement.
	 * @param propertyName the property on which this requirement operates.
	 */
	public Requirement( String value, String propertyName ) {
		this.value = value;
		this.propertyName = "polish." + propertyName;
	}
	
	/**
	 * Retrieves the requirement-property of the given device.
	 *  
	 * @param device the device for which the property should be retrieved
	 * @return the property-value or null when no such property is defined.
	 */
	public String getProperty( Device device ) {
		return device.getCapability( this.propertyName );
	}
	
	/**
	 * Checks if this requirement is met by the given device.
	 * 
	 * @param device the device which should be tested against this requirement.
	 * @return true when this requirement is satisfied by the given device.
	 */
	public boolean isMet( Device device ) {
		String property = getProperty(device);
		if (property == null) {
			return false;
		} else {
			return isMet( device, property );
		}
	}

	/**
	 * Checks if this requirement is met by the given device.
	 * 
	 * @param device the device which should be tested against this requirement.
	 * @param property the property-value of the given device. Property is never null.
	 * @return true when this requirement is satisfied by the given device.
	 */
	protected abstract boolean isMet( Device device, String property );
	
	public static final Requirement getInstance( String name, String value, String type ) {
		if (name == null) {
			throw new BuildException("A device requirement needs to have the attribute [name] defined.");
		}
		if (value == null) {
			throw new BuildException("The device requirement [" + name + "] needs to have the attribute [value] defined.");
		}
		
		Class reqClass = null;
		if (type != null) {
			if (type.equalsIgnoreCase("Size")) {
				return new SizeRequirement( name, value );
			} else if (type.equalsIgnoreCase("Int")) {
				return new IntRequirement( name, value );
			} else if (type.equalsIgnoreCase("String")) {
				return new StringRequirement( name, value );
			} else if (type.equalsIgnoreCase("Version")) {
				return new VersionRequirement( name, value );
			} else if (type.equalsIgnoreCase("Memory")) {
				return new MemoryRequirement( name, value );
			} else {
				try {
					reqClass = Class.forName( type );
				} catch (ClassNotFoundException e) {
					throw new BuildException("The device requirement [" + name + "] could not b eloaded - the type [" + type + "] could not be found: " + e.getMessage());
				}
			}
		}
		if (reqClass == null) {
			try {
				reqClass = Class.forName( "de.enough.polish.ant.requirements." + name + "Requirement");
			} catch (ClassNotFoundException e) {
				if (name.startsWith("SoftwarePlatform.") || name.startsWith("HardwarePlatform.")) {
					name = name.substring( 18 );
					try {
						reqClass = Class.forName( "de.enough.polish.ant.requirements." + name + "Requirement");
					} catch (ClassNotFoundException e1) {
						throw new BuildException("The device requirement [" + name + "] is not supported.");
					}
				} else {
					// could be an external requirement with full path:
					try {
						reqClass = Class.forName( name );
					} catch (ClassNotFoundException e1) {
						throw new BuildException("The device requirement [" + name + "] is not supported.");
					}
				}
			}
		} // if reqClass == null / loading class dynamically
		try {
			Constructor reqConstructor = reqClass.getConstructor( new Class[]{ String.class } );
			return (Requirement) reqConstructor.newInstance( new Object[] { value } );
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
	
	
}
