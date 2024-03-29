/*
 * Created on 24-Feb-2004 at 21:29:57.
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
package de.enough.polish.util;

import java.io.*;
import java.io.File;
import java.io.IOException;

/**
 * <p>Handles text files.</p>
 *
 * <p>copyright Enough Software 2004</p>
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
	private ResourceUtil resourceUtil;

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
	 * Creates a new TextFile which can be loaded from the JAR file.
	 *  
	 * @param baseDir the directory within the jar file
	 * @param fileName the name of the file
	 * @param lastModificationTime the modification time of the corresponding jar
	 * @param resourceUtil the jar-loader utility
	 */
	public TextFile(String baseDir, 
			String fileName, 
			long lastModificationTime, 
			ResourceUtil resourceUtil) 
	{
		this.resourceUtil = resourceUtil;
		this.lastModified = lastModificationTime;
		this.fileName = fileName;
		this.baseDir = baseDir;
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
			if (this.resourceUtil != null) {
				this.content = this.resourceUtil.readTextFile( this.baseDir + "/" + this.fileName );
			} else {
				this.content = FileUtil.readTextFile( this.file );
			}
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
