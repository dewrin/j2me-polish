/*
 * Created on 15-Apr-2004 at 11:01:05.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.util;

import java.io.*;
import java.util.ArrayList;

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
	
	private ClassLoader classLoader;
	
	public ResourceUtil( ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
	
	
	/**
	 * Opens the specified resource.
	 * The caller needs to ensure that the resource is closed.
	 * 
	 * @param url the url to the resource, a '/'-separated path
	 * @return the InputStream for the specified resource.
	 * @throws FileNotFoundException when the specified resource could not be found
	 */
	public final InputStream open( String url ) 
	throws FileNotFoundException 
	{
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
		InputStream in = this.classLoader.getResourceAsStream(url);
		if (in == null) {
			throw new FileNotFoundException("unable to open resource [" + url + "]: resource not found.");
		}
		return in;
	}


	/**
	 * Reads the specified text file and returns its content.
	 * 
	 * @param url the URL to the text file.
	 * @return a String array with the content of the specified file.
	 * @throws FileNotFoundException when the specified resource could not be found
	 * @throws IOException when the resource could not be read
	 */
	public String[] readTextFile(String url ) 
	throws FileNotFoundException, IOException 
	{
		InputStream is = open( url );
		ArrayList lines = new ArrayList();
		BufferedReader in = new BufferedReader( new InputStreamReader(is));
		String line;
		while ((line = in.readLine()) != null) {
			lines.add( line );
		}
		in.close();
		is.close();
		return (String[]) lines.toArray( new String[ lines.size() ]);
	}
}
