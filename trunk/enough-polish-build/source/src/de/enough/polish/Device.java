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
	
	public static final String IDENTIFIER = "Identifier";
	public static final String VENDOR = "Vendor";
	public static final String NAME = "Name";
	public static final String SCREEN_SIZE = "ScreenSize";
	public static final String SCREEN_WIDTH = "ScreenWidth";
	public static final String SCREEN_HEIGHT = "ScreenHeigth";
	public static final String CANVAS_SIZE = "CanvasSize";
	public static final String CANVAS_WIDTH = "CanvasWidth";
	public static final String CANVAS_HEIGHT = "CanvasHeigth";
	public static final String BITS_PER_PIXEL = "BitsPerPixel";
	public static final String JAVA_PLATFORM = "JavaPlatform";
	public static final String JAVA_PROTOCOL = "JavaProtocol";
	public static final String JAVA_PACKAGE= "JavaPackage";
	public static final String HEAP_SIZE = "HeapSize";
	public static final String USER_AGENT = "UserAgent";
	public static final String SUPPORTS_POLISH_GUI = "supportsPolishGui";
	
	private static final int POLISH_GUI_MIN_BITS_PER_PIXEL = 8;
	private static final MemoryMatcher POLISH_GUI_MIN_HEAP_SIZE = new MemoryMatcher("500+kb");
	
	private boolean supportsPolishGui;
	private String name;
	private String vendorName;
	private String identifier;
	

	/**
	 * Creates a new device.
	 * 
	 * @param parent the manufacturer of this device.
	 */
	public Device(Vendor parent) {
		super("polish.device", parent);
	}

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
		super( "device.polish", null );
		this.identifier = definition.getChildTextTrim( "identifier");
		if (this.identifier == null) {
			throw new InvalidComponentException("Unable to initialise device. Every device needs to define either its [identifier] or its [name] and [vendor]. Check your [devices.xml].");
		}
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
		String supportedApisStr = getCapability( "polish." + JAVA_PACKAGE );
		if (supportedApisStr != null) {
			String[] supportedApis = TextUtil.splitAndTrim( supportedApisStr, ',' );
			for (int i = 0; i < supportedApis.length; i++) {
				String api = supportedApis[i].toLowerCase();
				addFeature( "api." + api );
			}
		}
		// set midp-version:
		String midp = getCapability( "polish." + JAVA_PLATFORM );
		if (midp == null) {
			throw new InvalidComponentException("The device [" + this.identifier + "] does not define the needed element [" + JAVA_PLATFORM + "].");
		}
		midp = midp.toUpperCase();
		if (midp.startsWith("MIDP/1.")) {
			addFeature( "midp1");
		} else if (midp.startsWith("MIDP/2.")) {
			addFeature( "midp2");
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

}