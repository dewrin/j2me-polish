/*
 * Created on 22-Feb-2004 at 12:49:27.
 *
 * Copyright (c) 2004 Robert Virkus / Enough Software
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
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.obfuscate;

import de.enough.polish.Device;
import de.enough.polish.util.TextUtil;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Path;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import proguard.*;
import proguard.Configuration;
import proguard.ProGuard;
import proguard.classfile.ClassConstants;

/**
 * <p>Is used to obfuscate code with the ProGuard obfuscator.</p>
 * <p>For details of ProGuard, please refer to http://proguard.sourceforge.net/.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        22-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class ProGuardObfuscator extends Obfuscator {
	

	/**
	 * Creates a new pro guard obfuscator.
	 */
	public ProGuardObfuscator() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.obfuscate.Obfuscator#obfuscate(de.enough.polish.Device, java.io.File, java.lang.String[], org.apache.tools.ant.types.Path)
	 */
	public void obfuscate(Device device, File sourceFile, File targetFile, String[] preserve, Path bootClassPath) 
	throws BuildException 
	{
		Configuration cfg = new Configuration();
		// set libraries:
		
		cfg.libraryJars  = getPath( bootClassPath.toString() );
		String[] apiPaths = device.getClassPaths();
		for (int i = 0; i < apiPaths.length; i++) {
			cfg.libraryJars.addAll( getPath( apiPaths[i] ) );
		}
		
        cfg.inJars = getPath( sourceFile );
        cfg.outJars = getPath( targetFile );

        // do not obfuscate the <keep> classes and the defined midlets:
        cfg.keepClassFileOptions = new ArrayList( preserve.length );
        for (int i = 0; i < preserve.length; i++) {
			String className = TextUtil.replace( preserve[i], '.', '/');
			//System.out.println("\npreservering: " + className );
	        cfg.keepClassFileOptions.add(
	        		new KeepClassFileOption(
        				ClassConstants.INTERNAL_ACC_PUBLIC,
	                    0,
	                    className,
	                    null,
	                    null,
	                    true,
	                    false,
	                    false));
		}
        // The preverify tool seems to unpack the resulting class files,
        // so we must not use mixed-case class names on Windows.
        if (File.pathSeparatorChar == '\\') {
        	cfg.useMixedCaseClassNames = false;
        } else {
        	cfg.useMixedCaseClassNames = true;
        }

        // overload names with different return types:
        cfg.overloadAggressively = true;

        // move all classes to the root package:
        cfg.defaultPackage = "";		
        
        ProGuard proGuard = new ProGuard( cfg );
		try {
			proGuard.execute();
		} catch (IOException e) {
			throw new BuildException("ProGuard was unable to obfuscate: " + e.getMessage(), e );
		}
	}
	
	/**
	 * Converts the path of the given file to a proguard.ClassPath
	 * 
	 * @param file The file for which a class path should be retrieved.
	 * @return The classpath of the given file
	 */
	private ClassPath getPath(File file ) {
		return getPath( file.getAbsolutePath() );
	}

	/**
	 * Converts the given file path to a proguard.ClassPath
     * 
	 * @param path The path as a String
	 * @return The path as a proguard-ClassPath
     */
    private ClassPath getPath(String path)
    {
        ClassPath classPath = new ClassPath();
        String[] elements = TextUtil.split( path, File.pathSeparatorChar );
        for (int i = 0; i < elements.length; i++) {
        	ClassPathEntry entry =
                new ClassPathEntry( elements[i]);

            classPath.add(entry);
		}
        return classPath;
    }

}
