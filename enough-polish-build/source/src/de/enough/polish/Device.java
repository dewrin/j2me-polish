/*
 * Created on 15-Jan-2004 at 16:10:59.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish;

import de.enough.polish.ant.requirements.MemoryMatcher;
import de.enough.polish.exceptions.InvalidComponentException;
import de.enough.polish.util.TextUtil;

import org.jdom.Element;


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
	

	/**
	 * Creates a new device.
	 * 
	 * @param definition the xml definition of this device.
	 * @param vendorManager The manager of the device-manufacturers
	 * @param groupManager The manager for device-groups.
	 * @throws InvalidComponentException when the given definition has errors
	 */
	public Device(Element  definition,  VendorManager vendorManager, DeviceGroupManager groupManager ) 
	throws InvalidComponentException {
		this.identifier = definition.getChildTextTrim( "identifier");
		if (this.identifier == null) {
			throw new InvalidComponentException("Unable to initialise device. Every device needs to define either its [identifier] or its [name] and [vendor]. Check your [devices.xml].");
		}
		//System.out.println("\ninitialising device " + this.identifier);
		String[] chunks = TextUtil.split( this.identifier, '/');
		if (chunks.length != 2) {
			//TODO there could be several device definitions in one xml-block
			throw new InvalidComponentException("The device [" + this.identifier + "] has an invalid [identifier] - every identifier needs to consists of the vendor and the name, e.g. \"Nokia/6600\". Please check you [devices.xml].");
		}
		this.vendorName = chunks[0];
		this.name = chunks[1];
		addCapability( NAME, this.name );
		addCapability( VENDOR, this.vendorName );
		addCapability( IDENTIFIER, this.identifier );
		Vendor vendor = vendorManager.getVendor( this.vendorName );
		if (vendor == null) {
			throw new InvalidComponentException("The device [" + this.name + "] defines the vendor [" + this.vendorName + "] which is not defined within [vendors.xml] - please check your settings.");
		}
		addComponent(vendor);
		
		// load capabilities and features:
		loadCapabilities( definition, this.identifier, "devices.xml" );
		
		//add groups:
		String groupsDefinition = definition.getChildTextTrim( "groups");
		if (groupsDefinition != null) {
			String[] groups = TextUtil.splitAndTrim(groupsDefinition, ',');
			for (int i = 0; i < groups.length; i++) {
				DeviceGroup group = groupManager.getGroup( groups[i] );
				if (group == null) {
					throw new InvalidComponentException("The device [" + this.identifier + "] contains the undefined group [" + groups[i] + "] - please check either [devices.xml] or [groups.xml].");
				}
				addComponent(group);
			}
		}
		
		// set specific features:
		// set api-support:
		String supportedApisStr = getCapability( JAVA_PACKAGE );
		if (supportedApisStr != null) {
			System.out.println(this.identifier +  " found apis: [" + supportedApisStr + "].");
			String[] apis = TextUtil.splitAndTrim( supportedApisStr, ',' );
			for (int i = 0; i < apis.length; i++) {
				String api = apis[i].toLowerCase();
				addFeature( "api." + api );
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
		} else if (midp.startsWith("MIDP/2.")) {
			addFeature( "midp2");
			this.midpVersion = MIDP_2;
		}
		String supportsPolishGuiText = definition.getAttributeValue("supportsPolishGui");
		if (supportsPolishGuiText != null) {
			supportsPolishGuiText = supportsPolishGuiText.toLowerCase();
			this.supportsPolishGui = "true".equals( supportsPolishGuiText ) 
										   || "yes".equals( supportsPolishGuiText );
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

}
