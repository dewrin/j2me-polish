/*
 * Created on 23-Jan-2003 at 08:09:52.
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
package de.enough.polish.ant.info;

import de.enough.polish.Variable;

import org.apache.tools.ant.BuildException;

import java.util.ArrayList;

/**
 * <p>Represents the info-section of a polish project.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        23-Jan-2003 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class InfoSetting {
	
	// REQUIRED ATTRIBUTES
	// J=Jad, M=Manifest
	public static final String MIDLET_NAME = "MIDlet-Name"; // J & M
	public static final String MIDLET_VERSION = "MIDlet-Version"; // J & M
	public static final String MIDLET_VENDOR = "MIDlet-Vendor"; // J & M
	public static final String MIDLET_JAR_URL = "MIDlet-Jar-URL"; // J
	public static final String MIDLET_JAR_SIZE = "MIDlet-Jar-Size"; // J
	public static final String MICRO_EDITION_PROFILE = "MicroEdition-Profile"; // M
	public static final String MICRO_EDITION_CONFIGURATION = "MicroEdition-Configuration"; // M
	
	// OPTIONAL ATTRIBUTES
	public static final String MIDLET_ICON = "MIDlet-Icon"; // J & M
	public static final String MIDLET_DESCRIPTION = "MIDlet-Description"; // J & M
	public static final String MIDLET_INFO_URL = "MIDlet-Info-URL"; // J & M
	public static final String MIDLET_DATA_SIZE = "MIDlet-Data-Size"; // J & M
	public static final String MIDLET_DELETE_CONFIRM = "MIDlet-Delete-Confirm"; // M?
	public static final String MIDLET_DELETE_NOTIFY = "MIDlet-Delete-Notify"; // M?
	public static final String MIDLET_INSTALL_NOTIFY = "MIDlet-Install-Notify"; // M?
	
	// For a list of Midlet-N attributes see 
	// http://java.sun.com/j2me/docs/wtk2.0/user_html/Ap_Attributes.html#wp21956
	public static final String NMIDLET = "MIDlet-";
	
	public static final String CLDC1 = "CLDC-1.0";
	public static final String MIDP1 = "MIDP-1.0";
	public static final String MIDP2 = "MIDP-2.0";
	private  static final String GPL_LICENCE = "GPL";
	
	private String name;
	private String version;
	private String description;
	private String infoUrl;
	private String icon;
	private String jarUrl;
	private String copyright;
	private String deleteConfirm;
	private String deleteNotify;
	private String installNotify;
	private Author[] authors;
	private VendorInfo vendorInfo;
	private String dataSize;
	private ArrayList manifestAttributes;
	private ArrayList jadAttributes;
	private String vendorName;
	private String jarName;
	private String licence;
	
	/**
	 * Creates a new InfoSetting
	 */
	public InfoSetting() {
		this.manifestAttributes = new ArrayList();
		this.manifestAttributes.add( new Variable( MICRO_EDITION_CONFIGURATION, CLDC1 ));
		this.jadAttributes = new ArrayList();
	}

	/**
	 * @return Returns the copyright.
	 */
	public String getCopyright() {
		return this.copyright;
	}

	/**
	 * @param copyright The copyright to set.
	 */
	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	/**
	 * @return Returns the deleteConfirm.
	 */
	public String getDeleteConfirm() {
		return this.deleteConfirm;
	}

	/**
	 * @param deleteConfirm The deleteConfirm to set.
	 */
	public void setDeleteConfirm(String deleteConfirm) {
		this.manifestAttributes.add( new Variable( MIDLET_DELETE_CONFIRM, deleteConfirm ));
		this.deleteConfirm = deleteConfirm;
	}

	/**
	 * @return Returns the deleteNotify.
	 */
	public String getDeleteNotify() {
		return this.deleteNotify;
	}

	/**
	 * @param deleteNotify The deleteNotify to set.
	 */
	public void setDeleteNotify(String deleteNotify) {
		Variable var = new Variable( MIDLET_DELETE_NOTIFY, deleteNotify );
		this.manifestAttributes.add( var );
		this.deleteNotify = deleteNotify;
	}

	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		Variable var = new Variable( MIDLET_DESCRIPTION, description );
		this.manifestAttributes.add( var );
		this.jadAttributes.add( var );
		this.description = description;
	}

	/**
	 * @return Returns the icon.
	 */
	public String getIcon() {
		return this.icon;
	}

	/**
	 * @param icon The icon to set.
	 */
	public void setIcon(String icon) {
		if (!icon.startsWith("/")) {
			icon = "/" + icon;
		}
		Variable var = new Variable( MIDLET_ICON, icon );
		this.manifestAttributes.add( var );
		this.jadAttributes.add( var );
		this.icon = icon;
	}

	/**
	 * @return Returns the infoUrl.
	 */
	public String getInfoUrl() {
		return this.infoUrl;
	}

	/**
	 * @param infoUrl The infoUrl to set.
	 */
	public void setInfoUrl(String infoUrl) {
		Variable var = new Variable( MIDLET_INFO_URL, infoUrl );
		this.manifestAttributes.add( var );
		this.jadAttributes.add( var );
		this.infoUrl = infoUrl;
	}

	/**
	 * @return Returns the installNotify.
	 */
	public String getInstallNotify() {
		return this.installNotify;
	}

	/**
	 * @param installNotify The installNotify to set.
	 */
	public void setInstallNotify(String installNotify) {
		Variable var = new Variable( MIDLET_INSTALL_NOTIFY, installNotify );
		this.manifestAttributes.add( var );
		this.installNotify = installNotify;
	}

	/**
	 * Retrieves the URL of the Jar-File.
	 *  
	 * The URL can contain several properties. Following properties are allowed:
	 * <ul>
	 * 	<li><b>polish.identifier</b>: The vendor and name of the current device, 
	 * 		   e.g. "Nokia/3650". 
	 * 		   Note that the identifier contains a slash, which might result 
	 * 		   in undesired behaviour (since it might point to a different folder).</li>
	 * 	<li><b>polish.name</b>: The name of the current device, e.g. "3650".</li>
	 * 	<li><b>polish.vendor</b>: The name of the vendor of the current device, e.g. "Nokia".</li>
	 * 	<li><b>polish.version</b>: The version of the project as defined in the attribute [version].</li>
	 * 	<li><b>polish.jarName</b>: The name of the jar-file as defined in the attribute [jarName].</li>
	 * </ul>
	 * 
	 * @return Returns the URL of the jar-file.
	 */
	public String getJarUrl() {
		return this.jarUrl;
	}

	/**
	 * Sets the URL of the Jar-File.
	 *  
	 * The URL can contain several properties. Following properties are allowed:
	 * <ul>
	 * 	<li><b>polish.identifier</b>: The vendor and name of the current device, 
	 * 		   e.g. "Nokia/3650". 
	 * 		   Note that the identifier contains a slash, which might result 
	 * 		   in undesired behaviour (since it might point to a different folder).</li>
	 * 	<li><b>polish.name</b>: The name of the current device, e.g. "3650".</li>
	 * 	<li><b>polish.vendor</b>: The name of the vendor of the current device, e.g. "Nokia".</li>
	 * 	<li><b>polish.version</b>: The version of the project as defined in the attribute [version].</li>
	 * 	<li><b>polish.jarName</b>: The name of the jar-file as defined in the attribute [jarName].</li>
	 * </ul>
	 * 
	 * @param jarUrl The jarUrl to set.
	 */
	public void setJarUrl(String jarUrl) {
		Variable var = new Variable( MIDLET_JAR_URL, jarUrl);
		this.jadAttributes.add( var );
		this.jarUrl = jarUrl;
	}

	/**
	 * @return Returns the name of this project.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name The name of this project.
	 */
	public void setName(String name) {
		Variable var = new Variable( MIDLET_NAME, name );
		this.manifestAttributes.add( var );
		this.jadAttributes.add( var );
		this.name = name;
	}
	
	public void addConfiguredAuthors( Authors authorsList ) {
		this.authors = authorsList.getAuthors();
		if (this.authors.length == 0) {
			throw new BuildException("The [authors] elements needs to have at least one nested [author] element.");
		}
	}
	
	public Author[] getAuthors() {
		return this.authors;
	}
	
	public void addConfiguredVendorInfo( VendorInfo info ) {
		if (info.getName() == null) {
			throw new BuildException("The <vendorInfo> Element needs to define the attribute [name].");
		}
		Variable var = new Variable( MIDLET_VENDOR, info.getName() );
		this.manifestAttributes.add( var );
		this.jadAttributes.add( var );
		
		this.vendorInfo = info;
	}
	
	public VendorInfo getVendorInfo() {
		return this.vendorInfo;
	}
	
	public void setVendorName( String vendorName ) {
		Variable var = new Variable( MIDLET_VENDOR, vendorName );
		this.manifestAttributes.add( var );
		this.jadAttributes.add( var );
		this.vendorName = vendorName;
	}
	
	public String getVendorName() {
		return this.vendorName;
	}

	/**
	 * @return the size which this midlet needs for the storage of data
	 */
	public String getDataSize() {
		return this.dataSize;
	}

	/**
	 * @param dataSize the size which this midlet needs for the storage of data
	 */
	public void setDataSize(String dataSize) {
		Variable var = new Variable( MIDLET_DATA_SIZE, dataSize );
		this.manifestAttributes.add( var );
		this.jadAttributes.add( var );
		this.dataSize = dataSize;
	}
	
	/**
	 * Retrieves all manifest attributes for the JAR file.
	 * 
	 * @return a Variable array containing all defined attributes for the JAR-Manifest.
	 */
	public Variable[] getManifestAttributes() {
		return (Variable[]) this.manifestAttributes.toArray( new Variable[ this.manifestAttributes.size() ] );
	}

	/**
	 * Retrieves all attributes for the JAD file.
	 * 
	 * @return a Variable array containing all defined attributes for the JAD-file.
	 */
	public Variable[] getJadAttributes() {
		return (Variable[]) this.jadAttributes.toArray( new Variable[ this.jadAttributes.size() ] );
	}
	
	/**
	 * @return Returns the midlet-version.
	 */
	public String getVersion() {
		return this.version;
	}
	
	/**
	 * @param version The version to set.
	 */
	public void setVersion(String version) {
		Variable var = new Variable( MIDLET_VERSION, version );
		this.manifestAttributes.add( var );
		this.jadAttributes.add( var );
		this.version = version;
	}

	/**
	 * Gets the name of the jar file.
	 * The name can contain several properties. Following properties are allowed:
	 * <ul>
	 * 	<li><b>polish.identifier</b>: The vendor and name of the current device, 
	 * 		   e.g. "Nokia/3650". 
	 * 		   Note that the identifier contains a slash, which might result 
	 * 		   in undesired behaviour (since it might point to a different folder).</li>
	 * 	<li><b>polish.name</b>: The name of the current device, e.g. "3650".</li>
	 * 	<li><b>polish.vendor</b>: The name of the vendor of the current device, e.g. "Nokia".</li>
	 * 	<li><b>polish.version</b>: The version of the project as defined in the attribute [version].</li>
	 * 	<li><b></b>: </li>
	 * </ul>
	 * 
	 * @return The name of this Jar-File.
	 */ 
	public String getJarName() {
		return this.jarName;
	}
	
	/**
	 * Sets the name of the jar file which will be created.
	 * The name can contain several properties. Following properties are allowed:
	 * <ul>
	 * 	<li><b>polish.identifier</b>: The vendor and name of the current device, 
	 * 		   e.g. "Nokia/3650". 
	 * 		   Note that the identifier contains a slash, which might result 
	 * 		   in undesired behaviour (since it might point to a different folder).</li>
	 * 	<li><b>polish.name</b>: The name of the current device, e.g. "3650".</li>
	 * 	<li><b>polish.vendor</b>: The name of the vendor of the current device, e.g. "Nokia".</li>
	 * 	<li><b>polish.version</b>: The version of the project as defined in the attribute [version].</li>
	 * 	<li><b></b>: </li>
	 * </ul>
	 * 
	 * @param jarName The name of the jar file.
	 */
	public void setJarName( String jarName ) {
		this.jarName = jarName;
	}
	
	/**
	 * @return Returns the licence.
	 */
	public String getLicence() {
		return this.licence;
	}
	
	/**
	 * @param licence The licence of the created applications, either "GPL" or the licence-number for commercial use.
	 */
	public void setLicence(String licence) {
		if ("GPL".equalsIgnoreCase(licence)) {
			this.licence = GPL_LICENCE;
		} else {
			if (licence.length() != 7) {
				throw new BuildException("Invalid licence: [" + licence +"]. Please use either the GPL licence or optain a commercial licence from enough software at www.enough.de/j2mepolish.");
			}
			try {
				Long.parseLong(licence, 0x10);
				this.licence = licence;
			} catch (Exception e) {
				throw new BuildException("Invalid licence: [" + licence +"]. Please use either the GPL licence or optain a commercial licence from enough software at www.enough.de/j2mepolish.");
			}
		}
	}
}
