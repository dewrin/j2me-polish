/*
 * Created on 15-Apr-2004 at 11:01:05.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.util;

import java.io.*;

/**
 * <p></p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        15-Apr-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public final class ResourceUtil {
	
	private final static ClassLoader CLASS_LOADER = (new Object()).getClass().getClassLoader();
	
	
	/**
	 * Opens the specified resource.
	 * 
	 * @param url the url to the resource, a '/'-separated path
	 * @return the InputStream for the specified resource, 
	 * 		   or null when the resource could not be found.
	 */
	public InputStream open( String url ) {
		// check if url points to an existing file:
		File file = new File( url );
		if (file.exists()) {
			try {
				return new FileInputStream( file );
			} catch (FileNotFoundException e) {
				// should not be thrown, since we checked file.exists()
				// now try to get the specified resource from the class loader...
			}
		}
		return CLASS_LOADER.getResourceAsStream(url);
	}
}
