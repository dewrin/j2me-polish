/*
 * Created on 24-Feb-2004 at 21:29:57.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.util;

import java.io.*;
import java.io.File;
import java.io.IOException;

/**
 * <p>Handles text files.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        24-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class TextFile {
	
	private String fileName;
	private String baseDir;
	private File file;
	private long lastModified;
	private String[] content;

	/**
	 * Creates a new text file.
	 * 
	 * @param baseDir The name of the source directory
	 * @param fileName The name and relative path of the text file
	 * @throws FileNotFoundException when the file does not exist.
	 */
	public TextFile( String baseDir, String fileName ) 
	throws FileNotFoundException 
	{
		this.baseDir = baseDir;
		this.fileName = fileName;
		updateFile();
		this.lastModified = this.file.lastModified();
		if (!this.file.exists()) {
			throw new FileNotFoundException("The file [" + this.file.getAbsolutePath() + "] does not exist.");
		}
	}
	
	/**
	 * @return Returns the name and relative path of this text file.
	 */
	public String getFileName() {
		return this.fileName;
	}
	

	/**
	 * @return Returns the time of last modification.
	 */
	public long lastModified() {
		return this.lastModified;
	}

	/**
	 * Retrieves the content of this text file.
	 * When the content has not been loaded yet, it will be loaded now.
	 * 
	 * @return Returns a copy the content of this text file.
	 * @throws IOException when the file could not be read.
	 */
	public String[] getContent() 
	throws IOException 
	{
		if (this.content == null ) {
			this.content = FileUtil.readTextFile( this.file );
		}
		String[] copy = (String[]) this.content.clone();
		return copy;
	}
	
	/**
	 * @param content the content of this text file.
	 */
	public void setContent(String[] content) {
		this.content = content;
	}

	/**
	 * @return Returns the base directory of this file.
	 */
	public String getBaseDir() {
		return this.baseDir;
	}
	
	/**
	 * @param baseDir the base directory of this file
	 */
	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
		updateFile();
	}

	/**
	 * @return Returns the File instance.
	 */
	public File getFile() {
		return this.file;
	}
	
	/**
	 * Saves this text file.
	 * 
	 * @throws IOException when the saving failed.
	 */
	public void save() 
	throws IOException 
	{
		FileUtil.writeTextFile(this.file, this.content);
		updateFile();
		this.lastModified = this.file.lastModified();
	}
	
	/**
	 * Saves the text file to a different base directory.
	 * 
	 * @param targetDir The directory to which this file should be saved.
	 * @throws IOException when the saving failed.
	 */
	public void saveToDir( String targetDir ) 
	throws IOException 
	{
		saveToDir( targetDir, this.content, false );
	}

	/**
	 * Saves the text file to a different base directory.
	 * 
	 * @param targetDir The directory to which this file should be saved.
	 * @param lines The content which should be saved.
	 * @throws IOException when the saving failed.
	 */
	public void saveToDir( String targetDir, String[] lines ) 
	throws IOException 
	{
		saveToDir( targetDir, lines, false );
	}

	/**
	 * Saves the text file to a different base directory.
	 * 
	 * @param targetDir The directory to which this file should be saved.
	 * @param lines The content which should be saved.
	 * @param update True when the targetDir should be used as the new base directory,
	 *        and when the content of this file should be updated.
	 * @throws IOException when the saving failed.
	 */
	public void saveToDir(String targetDir, String[] lines, boolean update) 
	throws IOException 
	{
		if (update) {
			this.content = lines;
			this.baseDir = targetDir;
			updateFile();
			save();
		} else {
			File targetFile = new File( targetDir + File.separatorChar + this.fileName );
			FileUtil.writeTextFile(targetFile, lines);
		}
		
	}

	/**
	 * Resets the file.
	 */
	private final void updateFile() {
		this.file = new File( this.baseDir + File.separatorChar + this.fileName );
	}

}
