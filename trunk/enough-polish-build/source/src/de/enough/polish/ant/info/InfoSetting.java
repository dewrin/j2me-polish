/*
 * Created on 23-Jan-2003 at 08:09:52.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.info;

import org.apache.tools.ant.BuildException;

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
	
	private String name;
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
	
	/**
	 * Creates a new InfoSetting
	 */
	public InfoSetting() {
		// initialisation is done via the setter methods
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
		this.installNotify = installNotify;
	}

	/**
	 * @return Returns the jarUrl.
	 */
	public String getJarUrl() {
		return this.jarUrl;
	}

	/**
	 * @param jarUrl The jarUrl to set.
	 */
	public void setJarUrl(String jarUrl) {
		this.jarUrl = jarUrl;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
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
		this.vendorInfo = info;
	}
	
	public VendorInfo getVendorInfo() {
		return this.vendorInfo;
	}

}
