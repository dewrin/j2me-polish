/*
 * Created on 23-May-2004 at 22:20:13.
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

import org.apache.tools.ant.BuildException;
import org.jdom.*;
import org.jdom.input.SAXBuilder;


import java.io.*;
import java.util.*;

/**
 * <p>Manages the libraries of devices.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        23-May-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class LibraryManager {
	
	private String wtkLibPath;
	private String projectLibPath;
	private HashMap libraries = new HashMap();
	private HashMap resolvedClassPaths = new HashMap();
	private HashMap resolvedLibraryPaths = new HashMap();
	private Hashtable antProperties;

	/**
	 * Creates a new ApiManager.
	 * 
	 * @param antProperties all properties which have been defined in Ant
	 * @param projectLibPath the default path to libraries. The default is "import".
	 * @param wtkHomePath the path to the wireless toolkit (if known)
	 * @param preverifyPath the path to the preverify executable
	 * @param is input stream for reading the apis.xml file
	 * @throws JDOMException when there are syntax errors in apis.xml
	 * @throws IOException when apis.xml could not be read
	 * @throws InvalidComponentException when an api definition has errors
	 */
	public LibraryManager( Hashtable antProperties, String projectLibPath, String wtkHomePath, String preverifyPath, InputStream is) 
	throws JDOMException, IOException, InvalidComponentException 
	{
		if (wtkHomePath == null) {
			int pos = preverifyPath.lastIndexOf( File.separatorChar );
			wtkHomePath = preverifyPath.substring(0, pos );
			pos = preverifyPath.lastIndexOf( File.separatorChar );
			wtkHomePath = preverifyPath.substring(0, pos );
		} else if (wtkHomePath.endsWith( File.separator )) {
			wtkHomePath = wtkHomePath.substring(0, wtkHomePath.length() -1 );
		}
		if (!projectLibPath.endsWith( File.separator )) {
			projectLibPath += File.separator;
		}
		this.antProperties = antProperties;
		this.projectLibPath = projectLibPath;
		this.wtkLibPath = wtkHomePath + File.separatorChar + "lib" + File.separatorChar;
		loadLibraries( is );
	}

	/**
	 * Loads the apis.xml file.
	 * 
	 * @param is input stream for reading the apis.xml file
	 * @throws JDOMException when there are syntax errors in apis.xml
	 * @throws IOException when apis.xml could not be read
	 * @throws InvalidComponentException when an api definition has errors
	 */
	private void loadLibraries(InputStream is) 
	throws JDOMException, IOException, InvalidComponentException 
	{
		if (is == null) {
			throw new BuildException("Unable to load apis.xml, no file found.");
		}
		SAXBuilder builder = new SAXBuilder( false );
		Document document = builder.build( is );
		List xmlList = document.getRootElement().getChildren();
		for (Iterator iter = xmlList.iterator(); iter.hasNext();) {
			Element definition = (Element) iter.next();
			Library lib = new Library( this.antProperties, this.wtkLibPath, this.projectLibPath, definition );
			Library existingLib = (Library) this.libraries.get( lib.getSymbol() ); 
			if ( existingLib != null ) {
				throw new InvalidComponentException("The library [" + lib.getFullName() 
						+ "] uses the symbol [" + lib.getSymbol() + "], which is already used by the "
						+ "library [" + existingLib.getFullName() 
						+ "]. Please adjust your settings in [apis.xml].");
			}
			String[] names = lib.getNames();
			for (int i = 0; i < names.length; i++) {
				String name = names[i];
				existingLib = (Library) this.libraries.get( name ); 
				if ( existingLib != null ) {
					throw new InvalidComponentException("The library [" + lib.getFullName() 
							+ "] uses the name [" + name + "], which is already used by the "
							+ "library [" + existingLib.getFullName() 
							+ "]. Please adjust your settings in [apis.xml].");
				}
				this.libraries.put( name, lib );
			}
			this.libraries.put( lib.getSymbol(), lib );
		}		
	}
	
	/**
	 * Retrieves the classpath for the given device.
	 * 
	 * @param device the device for which the path should be retrieved
	 * @return the classpath for this device. When a supported library
	 *         could not be found, a warning will be printed to System.out.
	 */
	public String[] getClassPaths( Device device ) {
		if (device.getSupportedApisAsString() == null) {
			return new String[0];
		}
		// first check if the device's libraries have been resolved already:
		String[] resolvedPaths = (String[]) this.resolvedClassPaths.get( device.getSupportedApisAsString() );
		if (resolvedPaths != null) {
			return resolvedPaths;
		}
		// now resolve the path to each supported library:
		String[] libNames = device.getSupportedApis();
		ArrayList libPaths = new ArrayList();
		for (int i = 0; i < libNames.length; i++) {
			String libName = libNames[i];
			Library lib = (Library) this.libraries.get( libName );
			String libPath = null;
			if (lib != null ) {
				libPath = lib.getPath();
			} else {
				libPath = (String) this.resolvedLibraryPaths.get( libName );
				if (libPath == null) {
					// try to resolve the lib-path by the name:
					// try a jar file:
					String fileName = this.projectLibPath + libName + ".jar";
					File file = new File( fileName );
					if ( file.exists() ) {
						libPath = file.getAbsolutePath();
					} else {
						// try a zip-file:
						fileName = this.projectLibPath + libName + ".zip";
						file = new File( fileName );
						if ( file.exists() ) {
							libPath = file.getAbsolutePath();
						} else {
							// now check if an property has been defined for this api:
							String path = (String) antProperties.get( "polish.api." + libName );
							if (path != null) {
								File libFile = new File( path );
								if (libFile.exists()) {
									libPath = libFile.getAbsolutePath();
								} else {
									System.out.println("Warning: the Ant-property [polish.api." + libName + "] points to a non-existing file. When this leads to problems, please register this API in [apis.xml].");
								}
							} else {
								System.out.println("Warning: unable to resolve path to API [" + libName + "]. When this leads to problems, please register this API in [apis.xml].");
							}
						}
					}
				}
			} // if libPath == null
			if (libPath != null) {
				libPaths.add( libPath );
				this.resolvedLibraryPaths.put( libName, libPath );
			}
		}
		resolvedPaths = (String[]) libPaths.toArray( new String[ libPaths.size() ] );
		this.resolvedClassPaths.put( device.getSupportedApisAsString(), resolvedPaths );
		return resolvedPaths;
	}

	/**
	 * Retrieves the symbol for the specified library.
	 * 
	 * @param libName the name of the library
	 * @return the symbol for this library. When the library is not known, null is returned.
	 */
	public String getSymbol(String libName) {
		Library lib = (Library) this.libraries.get( libName );
		if (lib == null) { 
			return null;
		} else {
			return lib.getSymbol();
		}
	}
}
