/*
 * Created on 22-Feb-2004 at 12:16:08.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.obfuscate;

import de.enough.polish.Device;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Path;

import java.io.File;

/**
 * <p>An obfuscator is used to obfuscate and shrink the code.</p>
 * <p>This class is used to handle any external obfuscator.</p>
 * 
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        22-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public abstract class Obfuscator {
	
	/**
	 * Creates a new obfuscator.
	 */
	public Obfuscator() {
		super();
	}
	
	/**
	 * Obfuscates a jar-file for the given device.
	 * 
	 * @param device The J2ME device
	 * @param jar The jar-file containing the projects classes
	 * @param preserve All names of classes which should be preserved,
	 *                 that means not renamed or removed.
	 * @param bootClassPath A path to the library containing either the MIDP1.0 or MIDP2.0 environment.
	 * @throws BuildException when the obfuscation failed
	 */
	public abstract void obfuscate( Device device, File jar, String[] preserve, Path bootClassPath )
	throws BuildException;
	
	/**
	 * Instantiates an obfuscator.
	 * 
	 * @param preferredObfuscator The name of the preferred obfuscator.
	 *        When null is given and no className is specified, 
	 * 		  the GPL ProGuard will be used.
	 * @param className The class name of the desired obfuscator.
	 * @return The concrete obfuscator.
	 * @throws BuildException when the preferred obfuscator or none obfuscator at all
	 *         could be instantiated.
	 */
	public static final Obfuscator getInstance( String preferredObfuscator, String className )
	throws BuildException
	{
		if (className != null) {
			try {
				Class obfuscatorClass = Class.forName( className );
				return (Obfuscator) obfuscatorClass.newInstance();	
			} catch (ClassNotFoundException e) {
				throw new BuildException( "Unable to load obfuscator [" + className + "]: " + e.getMessage(), e );
			} catch (InstantiationException e) {
				throw new BuildException( "Unable to instantiate obfuscator [" + className + "]: " + e.getMessage(), e );
			} catch (IllegalAccessException e) {
				throw new BuildException( "Unable to instantiate obfuscator [" + className + "]: " + e.getMessage(), e );
			}
		}
		if (preferredObfuscator != null) {
			try {
				String obfuscatorName = preferredObfuscator.substring(0, 1).toUpperCase()
							        + preferredObfuscator.substring( 1 );
				Class obfuscatorClass = Class.forName( 
							"de.enough.polish.obfuscate." 
							+ obfuscatorName + "Obfuscator");
				return (Obfuscator) obfuscatorClass.newInstance();	
			} catch (ClassNotFoundException e) {
				throw new BuildException( "Unable to load obfuscator [" + preferredObfuscator + "]: " + e.getMessage(), e );
			} catch (InstantiationException e) {
				throw new BuildException( "Unable to instantiate obfuscator [" + preferredObfuscator + "]: " + e.getMessage(), e );
			} catch (IllegalAccessException e) {
				throw new BuildException( "Unable to instantiate obfuscator [" + preferredObfuscator + "]: " + e.getMessage(), e );
			}
		}
		// check for default obfuscators:
		try {
			//TODO does the class really need to be on the classpath?
			// or start the obfuscator complete externally?
			Class.forName("proguard.ProGuard");
			// okay, proguard found:
			return new ProGuardObfuscator();
		} catch (ClassNotFoundException e) {
			// check for next obfuscator:
		}
		return null;
	}
}
