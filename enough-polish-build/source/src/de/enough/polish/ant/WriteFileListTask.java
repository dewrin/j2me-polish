/*
 * Created on 15-Apr-2004 at 16:01:30.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant;

import de.enough.polish.util.FileUtil;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.FileSet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * <p>Writes the list of included files to the specified text-file.</p>
 * <p>This tasks accepts an arbitrary number of nested file names.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        15-Apr-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class WriteFileListTask extends Task {
	
	private File target;
	private ArrayList fileSets;
	
	/**
	 * Creates a new empty task
	 */
	public WriteFileListTask() {
		super();
		this.target = new File("index.txt");
		this.fileSets = new ArrayList();
	}
	
	/**
	 * Sets the target to which the index-list should be written.
	 * Default value is "index.txt".
	 * 
	 * @param target the target
	 */
	public void setTarget( File target ) {
		this.target = target;
	}
	
	public void addConfiguredFileset( FileSet set ) {
		this.fileSets.add(set );
	}
	
	public void execute(){
		if (this.fileSets.size() == 0) {
			throw new BuildException("The WriteFileListTask needs at least one nested <fileset> element.");
		}
		ArrayList fileNamesList = new ArrayList();
		FileSet[] sets = (FileSet[]) this.fileSets.toArray( new FileSet[ this.fileSets.size()] );
		for (int i = 0; i < sets.length; i++) {
			FileSet set = sets[i];
			DirectoryScanner scanner = set.getDirectoryScanner(this.project);
			String[] fileNames = scanner.getIncludedFiles();
			for (int j = 0; j < fileNames.length; j++) {
				String name = fileNames[j];
				fileNamesList.add( name );
			}
		}
		String[] fileNames = (String[]) fileNamesList.toArray( new String[ fileNamesList.size()] );
		try {
			FileUtil.writeTextFile( this.target, fileNames );
		} catch (IOException e) {
			throw new BuildException("Unable to write file index [" 
					+ this.target.getAbsolutePath() + "]: " + e.getMessage(), e );
		}
	}
	
}
