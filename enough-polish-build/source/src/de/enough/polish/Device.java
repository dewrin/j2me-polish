/*
 * Created on 15-Jan-2004 at 16:10:59.
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
package de.enough.polish;

import de.enough.polish.ant.requirements.MemoryMatcher;
import de.enough.polish.exceptions.InvalidComponentException;
import de.enough.polish.util.CastUtil;
import de.enough.polish.util.TextUtil;

import org.jdom.Element;

import java.io.File;
import java.util.ArrayList;


/**
 * <p>Represents a J2ME device.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        15-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class Device extends PolishComponent {
	
	public static final String IDENTIFIER = "polish.Identifier";
	public static final String VENDOR = "polish.Vendor";
	public static final String NAME = "polish.Name";
	public static final String SCREEN_SIZE = "polish.ScreenSize";
	public static final String SCREEN_WIDTH = "polish.ScreenWidth";
	public static final String SCREEN_HEIGHT = "polish.ScreenHeigth";
	public static final String CANVAS_SIZE = "polish.CanvasSize";
	public static final String CANVAS_WIDTH = "polish.CanvasWidth";
	public static final String CANVAS_HEIGHT = "polish.CanvasHeigth";
	public static final String BITS_PER_PIXEL = "polish.BitsPerPixel";
	public static final String JAVA_PLATFORM = "polish.JavaPlatform";
	public static final String JAVA_PROTOCOL = "polish.JavaProtocol";
	public static final String JAVA_PACKAGE= "polish.JavaPackage";
	public static final String HEAP_SIZE = "polish.HeapSize";
	public static final String USER_AGENT = "polish.UserAgent";
	public static final String SUPPORTS_POLISH_GUI = "polish.supportsPolishGui";
	
	public static final int MIDP_1 = 1;
	public static final int MIDP_2 = 2;
	
	private static final int POLISH_GUI_MIN_BITS_PER_PIXEL = 8;
	private static final MemoryMatcher POLISH_GUI_MIN_HEAP_SIZE = new MemoryMatcher("500+kb");
	
	private boolean supportsPolishGui;
	private String name;
	private String vendorName;
	private int midpVersion;
	private String[] supportedApis;
	private String supportedApisString;
	
	private String classPath;
	private String sourceDir;
	private String classesDir;
	private String baseDir;
	private String[] groupNames;
	private DeviceGroup[] groups;
	private File jarFile;
	private int numberOfChangedFiles;
	private String[] classPaths;
	

	/**
	 * Creates a new device.
	 * 
	 * @param definition the xml definition of this device.
	 * @param identifier The identifier of this device.
	 * @param deviceName The name of this device.
	 * @param vendor The vendor (and "parent") of this device.
	 * @param groupManager The manager for device-groups.
	 * @param libraryManager the manager for device-specific APIs
	 * @throws InvalidComponentException when the given definition has errors
	 */
	public Device(Element definition, String identifier, String deviceName, Vendor vendor, DeviceGroupManager groupManager, LibraryManager libraryManager ) 
	throws InvalidComponentException 
	{
		super( vendor );
		this.identifier = identifier;
		this.name = deviceName;
		this.vendorName = vendor.getIdentifier();
		
		addCapability( NAME, this.name );
		addCapability( VENDOR, this.vendorName );
		addCapability( IDENTIFIER, this.identifier );
		
		// load capabilities and features:
		loadCapabilities( definition, this.identifier, "devices.xml" );
		
		//add groups:
		ArrayList groupNamesList = new ArrayList();
		ArrayList groupsList = new ArrayList();
		String groupsDefinition = definition.getChildTextTrim( "groups");
		if (groupsDefinition != null) {
			String[] tempGroupNames = TextUtil.splitAndTrim(groupsDefinition, ',');			
			for (int i = 0; i < tempGroupNames.length; i++) {
				String groupName = tempGroupNames[i];
				DeviceGroup group = groupManager.getGroup( groupName );
				if (group == null) {
					throw new InvalidComponentException("The device [" + this.identifier + "] contains the undefined group [" + groupName + "] - please check either [devices.xml] or [groups.xml].");
				}
				addComponent(group);
				groupsList.add( group );
				groupNamesList.add( groupName );
			}
		}
		
		// set specific features:
		// set api-support:
		this.supportedApisString = getCapability( JAVA_PACKAGE );
		if (this.supportedApisString != null) {
			//System.out.println(this.identifier +  " found apis: [" + supportedApisStr + "].");
			String[] apis = TextUtil.splitAndTrim( this.supportedApisString, ',' );
			for (int i = 0; i < apis.length; i++) {
				String api = apis[i].toLowerCase();
				String symbol = libraryManager.getSymbol( api );
				if (symbol == null) {
					symbol = api;
				}
				apis[i] = symbol;
				addFeature( "api." + symbol );
				groupNamesList.add( symbol );
				groupsList.add( groupManager.getGroup( symbol, true ) );
			}
			this.supportedApis = apis;
		}
		// set midp-version:
		String midp = getCapability( JAVA_PLATFORM );
		if (midp == null) {
			throw new InvalidComponentException("The device [" + this.identifier + "] does not define the needed element [" + JAVA_PLATFORM + "].");
		}
		midp = midp.toUpperCase();
		if (midp.startsWith("MIDP/1.")) {
			addFeature( "midp1");
			this.midpVersion = MIDP_1;
			groupNamesList.add( "midp1" );
			groupsList.add( groupManager.getGroup( "midp1", true ) );
		} else if (midp.startsWith("MIDP/2.")) {
			addFeature( "midp2");
			this.midpVersion = MIDP_2;
			groupNamesList.add( "midp2" );
			groupsList.add( groupManager.getGroup( "midp2", true ) );
		} else {
			System.out.println("Warning: device [" + this.identifier + "] supports unknown JavaPlatform [" + midp + "].");
		}
		String supportsPolishGuiText = definition.getAttributeValue("supportsPolishGui");
		if (supportsPolishGuiText != null) {
			supportsPolishGuiText = supportsPolishGuiText.toLowerCase();
			this.supportsPolishGui = CastUtil.getBoolean(supportsPolishGuiText ); 
		} else {
			// basically assume that any device supports the polish GUI:
			this.supportsPolishGui = true;
			// when a device has the BitsPerPixel capability defined,
			// it needs to have at least 8 bits per pixel (= 2^8 == 256 colors)
			String bitsPerPixelDef = getCapability( BITS_PER_PIXEL );
			if (bitsPerPixelDef != null) {
				int bitsPerPixel = Integer.parseInt( bitsPerPixelDef );
				this.supportsPolishGui = (bitsPerPixel >= POLISH_GUI_MIN_BITS_PER_PIXEL);
			}
			// when the device has the heap size defined,
			// it needs to have a minimum heap size of 500 kb:
			String heapSizeStr = getCapability( HEAP_SIZE );
			if (heapSizeStr != null) {
				this.supportsPolishGui = POLISH_GUI_MIN_HEAP_SIZE.matches(heapSizeStr);
			}
		}
		if (this.supportsPolishGui) {
			addFeature( SUPPORTS_POLISH_GUI );
		}
		String bitsPerPixelStr = this.getCapability( BITS_PER_PIXEL );
		if (bitsPerPixelStr != null ) {
			int bitsPerPixel = Integer.parseInt(bitsPerPixelStr);
			if (bitsPerPixel >= 4) {
				groupNamesList.add( "BitsPerPixel.4+" );
				groupsList.add( groupManager.getGroup( "BitsPerPixel.4+", true ) ); 
			}
			if (bitsPerPixel >= 8) {
				groupNamesList.add( "BitsPerPixel.8+" );
				groupsList.add( groupManager.getGroup( "BitsPerPixel.8+", true ) ); 
			}
			if (bitsPerPixel >= 12) {
				groupNamesList.add( "BitsPerPixel.12+" );
				groupsList.add( groupManager.getGroup( "BitsPerPixel.12+", true ) ); 
			}
			if (bitsPerPixel >= 16) {
				groupNamesList.add( "BitsPerPixel.16+" );
				groupsList.add( groupManager.getGroup( "BitsPerPixel.16+", true ) ); 
			}
			if (bitsPerPixel >= 24) {
				groupNamesList.add( "BitsPerPixel.24+" );
				groupsList.add( groupManager.getGroup( "BitsPerPixel.24+", true ) ); 
			}
			groupNamesList.add( "BitsPerPixel." + bitsPerPixelStr );
			groupsList.add( groupManager.getGroup( "BitsPerPixel." + bitsPerPixelStr, true ) );

		}
		this.groupNames = (String[]) groupNamesList.toArray( new String[groupNamesList.size() ] );
		this.groups = (DeviceGroup[]) groupsList.toArray( new DeviceGroup[groupsList.size() ] );
	}
	
	/**
	 * Determines whether this device supports the polish-gui-framework.
	 * Usually this is the case when the device meets some capabilities like
	 * the bits per pixel and the possible size of the heap.
	 * Devices can also define this directly by setting the attribute [supportsPolishGui]. 
	 * 
	 * @return true when this device supports the polish-gui.
	 */
	public boolean supportsPolishGui() {
		return this.supportsPolishGui;
	}

	/**
	 * @return the identifier of this device in the form [vendor]/[model], e.g. Nokia/6600
	 */
	public String getIdentifier() {
		return this.identifier;
	}
	
	/**
	 * Retrieves the major version of the MIDP implementation of this device.
	 * 
	 * @return the major MIDP-version, currently either 1 (MIDP_1) or 2 (MIDP_2)
	 */
	public int getMidpVersion() {
		return this.midpVersion;
	}
	
	/**
	 * Determines whether this device supports the MIDP/1.0 API.
	 * 
	 * @return true when this device supports the MIDP/1.0 API.
	 */
	public boolean isMidp1() {
		return (this.midpVersion == MIDP_1);
	}

	/**
	 * Determines whether this device supports the MIDP/2.0 API.
	 * 
	 * @return true when this device supports the MIDP/2.0 API.
	 */
	public boolean isMidp2() {
		return (this.midpVersion == MIDP_2);
	}
	
	/**
	 * Retrieves all APIs which this device supports.
	 * 
	 * @return a String array containing all APIs which are supported by this device.
	 */
	public String[] getSupportedApis() {
		return this.supportedApis;
	}


	/**
	 * Retrieves all APIs which this device supports.
	 * 
	 * @return All APIs which are supported by this device as a String.
	 */
	public String getSupportedApisAsString() {
		return this.supportedApisString;
	}

	/**
	 * @return Returns the classesDir.
	 */
	public String getClassesDir() {
		return this.classesDir;
	}
	/**
	 * @param classesDir The classesDir to set.
	 */
	public void setClassesDir(String classesDir) {
		this.classesDir = classesDir;
	}

	/**
	 * @return Returns the classPath.
	 */
	public String getClassPath() {
		return this.classPath;
	}
	
	/**
	 * @param classPath The classPath to set.
	 */
	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}

	/**
	 * @return Returns the sourceDir.
	 */
	public String getSourceDir() {
		return this.sourceDir;
	}
	
	/**
	 * @param sourceDir The sourceDir to set.
	 */
	public void setSourceDir(String sourceDir) {
		this.sourceDir = sourceDir;
	}


	/**
	 * @param baseDir The base directory of this device.
	 */
	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}
	
	public String getBaseDir() {
		return this.baseDir;
	}


	/**
	 * @return The name of this device, e.g. "3650" for a "Nokia/3650" device.
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Retrieves the parent-vendor of this device.
	 * 
	 * @return the vendor of this device.
	 */
	public Vendor getVendor() {
		return (Vendor) this.parent;
	}


	/**
	 * @return The name of the Vendor, e.g. "Nokia" for a "Nokia/3650" device.
	 */
	public String getVendorName() {
		return this.vendorName;
	}


	/**
	 * Retrieves the names of the groups to which this device belongs to.
	 * 
	 * @return The names of all groups of this device.
	 */
	public String[] getGroupNames() {
		return this.groupNames;
	}
	
	/**
	 * Retrieves all groups to which this device belongs to.
	 *  
	 * @return all groups of this device.
	 */
	public DeviceGroup[] getGroups() {
		return this.groups;
	}


	/**
	 * Sets the jar file.
	 * This method ist used to transport the setting to other modules.
	 * 
	 * @param jarFile The JAR file of the application for this device.
	 */
	public void setJarFile(File jarFile) {
		this.jarFile = jarFile;
	}
	
	/**
	 * Retrives the jar file for this device.
	 * 
	 * @return The jar file for this device.
	 */
	public File getJarFile() {
		return this.jarFile;
	}

	/**
	 * Sets the number of source files which actually have been processed.
	 * 
	 * @param numberOfChangedFiles the number of processed source files
	 */
	public void setNumberOfChangedFiles(int numberOfChangedFiles) {
		this.numberOfChangedFiles = numberOfChangedFiles;
	}
	
	/**
	 * Gets the number of source files which actually have been processed.
	 * 
	 * @return the number of processed source files
	 */
	public int getNumberOfChangedFiles() {
		return this.numberOfChangedFiles;
	}

	/**
	 * Sets the classpaths as a string array 
	 * @param classPaths the class paths as a string array
	 */
	public void setClassPaths(String[] classPaths) {
		this.classPaths = classPaths;
	}
	
	/**
	 * Retrieves the classpaths for this device as a string array
	 * 
	 * @return an array containing the classpaths for this device.
	 */
	public String[] getClassPaths() {
		return this.classPaths;
	}
}
