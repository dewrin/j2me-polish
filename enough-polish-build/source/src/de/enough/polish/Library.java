/*
 * Created on 23-May-2004 at 23:04:35.
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

import de.enough.polish.exceptions.InvalidComponentException;
import de.enough.polish.util.TextUtil;

import org.jdom.Element;

import java.io.File;
import java.util.Hashtable;

/**
 * <p>Represents a single library.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        23-May-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class Library {
	
	private String fullName;
	private String description;
	private String symbol;
	private String[] names;
	private String[] fileNames;
	private String defaultPath;
	private String wtkLibPath;
	private String projectLibPath;
	private String path;
	private boolean isInitialised;
	private Hashtable antProperties;

	/**
	 * Creates a new library.
	 * 
	 * @param antProperties all properties which have been defined in Ant
	 * @param wtkLibPath the path to the lib-folder of the wireless toolkit
	 * @param projectLibPath the path to the lib-folder of the current project
	 * @param definition the xml definition of this library
	 * @throws InvalidComponentException when the given api definition has errors
	 */
	public Library( Hashtable antProperties, String wtkLibPath, String projectLibPath, Element definition) 
	throws InvalidComponentException 
	{
		this.wtkLibPath = wtkLibPath;
		this.projectLibPath = projectLibPath;
		this.fullName = definition.getChildTextTrim( "name");
		if (this.fullName == null) {
			throw new InvalidComponentException("An api listed in apis.xml does not define its name. Please insert the <name> element into the file [apis.xml] for this library.");
		}
		this.description = definition.getChildTextTrim( "description");
		String namesString = definition.getChildTextTrim( "names");
		if (namesString == null) {
			throw new InvalidComponentException("The api [" + this.fullName + "] does not define the possible names of this library. Please insert the <names> element into the file [apis.xml] for this library.");
		}
		this.names = TextUtil.splitAndTrim( namesString, ',' );
		this.symbol = definition.getChildTextTrim( "symbol");
		if (this.symbol == null) {
			throw new InvalidComponentException("The api [" + this.fullName + "] does not define the preprocessing symbol for this library. Please insert the <symbol> element into the file [apis.xml] for this library.");
		}
		String fileNamesString = definition.getChildTextTrim( "files");
		if (fileNamesString != null) {
			this.fileNames = TextUtil.splitAndTrim( fileNamesString, ',' );
		}
		this.defaultPath = definition.getChildTextTrim( "path");
		this.antProperties = antProperties;
		// try and find this library only when it is used
		// (compare method findPath())
	}
	
	/**
	 * Retrieves the path for this library
	 * @return either the path for this library or null when it could
	 * 			not be resolved
	 */
	public String getPath() {
		if (!this.isInitialised) {
			findPath();
			this.isInitialised = true;
		}
		return this.path;
	}
	
	/**
	 * Tries to find the path for this library
	 */
	private void findPath() {
		// 1. try default path:
		if (this.defaultPath != null) {
			File libFile = new File( this.defaultPath );
			if (libFile.exists()) {
				this.path = this.defaultPath;
				return;
			}
		}
		
		// 2. now check if an property has been defined for this api:
		String path = (String) antProperties.get( "polish.api." + this.symbol );
		if (path != null) {
			File libFile = new File( path );
			if (libFile.exists()) {
				this.path = libFile.getAbsolutePath();
				return;
			}
		}
		
		// 3. now check all file-names:
		if (this.fileNames != null) {
			for (int i = 0; i < this.fileNames.length; i++) {
				String fileName = this.fileNames[i];
				// look in the project:
				File libFile = new File( this.projectLibPath + fileName );
				if (libFile.exists()) {
					this.path = libFile.getAbsolutePath();
					return;
				}
				// look in the wtk:
				libFile = new File( this.wtkLibPath + fileName );
				if (libFile.exists()) {
					this.path = libFile.getAbsolutePath();
					return;
				}
			}
		}
		
		// 4. now try the library names:
		for (int i = 0; i < this.names.length; i++) {
			
			// first look for jar files:
			String name = this.names[i] + ".jar";
			// look in the project:
			File libFile = new File( this.projectLibPath + name );
			if (libFile.exists()) {
				this.path = libFile.getAbsolutePath();
				return;
			}
			// look in the wtk:
			libFile = new File( this.wtkLibPath + name );
			if (libFile.exists()) {
				this.path = libFile.getAbsolutePath();
				return;
			}
			
			// now try zip-files:
			name = this.names[i] + ".zip";
			// look in the project:
			libFile = new File( this.projectLibPath + name );
			if (libFile.exists()) {
				this.path = libFile.getAbsolutePath();
				return;
			}
			// look in the wtk:
			libFile = new File( this.wtkLibPath + name );
			if (libFile.exists()) {
				this.path = libFile.getAbsolutePath();
				return;
			}
		}
		
		
		System.out.println("Warning: unable to find the library [" + this.fullName + "] on the path. If this leads to problems, please adjust the settings for this library in the file [apis.xml].");
	}
	
	/**
	 * @return Returns the defaultPath.
	 */
	public String getDefaultPath() {
		return this.defaultPath;
	}
	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return this.description;
	}
	/**
	 * @return Returns the fileNames.
	 */
	public String[] getFileNames() {
		return this.fileNames;
	}
	/**
	 * @return Returns the fullName.
	 */
	public String getFullName() {
		return this.fullName;
	}
	/**
	 * @return Returns the names.
	 */
	public String[] getNames() {
		return this.names;
	}
	/**
	 * @return Returns the symbol.
	 */
	public String getSymbol() {
		return this.symbol;
	}
}
