/*
 * Created on 14-Jan-2004 at 16:07:22.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.build.util;

import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * <p>Provides some often used methods for handling files.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        14-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public final class FileUtil {
	
	/**
	 * Reads a text file.
	 *  
	 * @param file the text file
	 * @return the lines of the text file
	 * @throws FileNotFoundException when the file was not found
	 * @throws IOException when file could not be read.
	 */
	public static String[] readTextFile( File file ) 
	throws FileNotFoundException, IOException 
	{
		ArrayList lines = new ArrayList();
		BufferedReader in = new BufferedReader(new FileReader(file));
		String line;
		while ((line = in.readLine()) != null) {
			lines.add( line );
		}
		in.close();
		return (String[]) lines.toArray( new String[ lines.size() ]);
	}


}
