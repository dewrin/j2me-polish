/*
 * Created on 01-Mar-2004 at 14:52:59.
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
package de.enough.polish.preprocess;

import org.apache.tools.ant.BuildException;

import java.io.*;
import java.util.ArrayList;

/**
 * <p>Reads CSS files and combines the found style-definitions.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        01-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public final class CssReader {
	
	private StyleSheet styleSheet;
	
	/**
	 * Creates a new empty CssReader.
	 */
	public CssReader() {
		this.styleSheet = new StyleSheet();
	}
	
	/**
	 * Creates a new CssReader with the specified style-sheet as a base.
	 * 
	 * @param styleSheet the base for this reader. The given style sheet will
	 *        be copied and not changed.
	 */
	public CssReader(StyleSheet styleSheet) {
		this.styleSheet = new StyleSheet( styleSheet );
	}	
	
	/**
	 * Reads the given file and adds/supplements all styles.
	 * 
	 * @param filePath the name and path of the file which contains the CSS-definitions
	 * @throws IOException when the file could not be found or not be loaded
	 */
	public void add( String filePath ) throws IOException {
		add( new File(filePath) );
	}

	/**
	 * Reads the given file and adds/supplements all styles.
	 * 
	 * @param file the file which contains the CSS-definitions
	 * @throws IOException when the file could not be found or not be loaded
	 */
	public void add(File file) throws IOException {
		if (this.styleSheet.lastModified() < file.lastModified() ) {
			this.styleSheet.setLastModified( file.lastModified() );
		}
		BufferedReader in = new BufferedReader(new FileReader(file));
		String line;
		StringBuffer buffer = new StringBuffer();
		while ((line = in.readLine()) != null) {
			buffer.append( line );
		}
		in.close();
		add( buffer );
	}

	/**
	 * Adds the contents of a CSS file.
	 * 
	 * @param buffer the StringBuffer containing CSS declarations.
	 */
	public void add(StringBuffer buffer) {
		buffer = removeCssComments( buffer );
		// create single chunks of the buffer:
		String[] cssBlocks = split( buffer );
		for (int i = 0; i < cssBlocks.length; i++) {
			String cssBlock = cssBlocks[i];
			addCssBlock( new CssBlock( cssBlock ) );
		}
	}
	
	/**
	 * Adds the given CSS-block.
	 * 
	 * @param cssBlock the definition of a single CSS-class, can contain sub-blocks.
	 */
	protected void addCssBlock(CssBlock cssBlock) {
		this.styleSheet.addCssBlock(cssBlock);
	}

	/**
	 * Adds the given style sheet.
	 * All properties of the given sheet will override the existing properties.
	 * 
	 * @param sheet the style sheet
	 */
	public void add(StyleSheet sheet) {
		this.styleSheet.add( sheet );
		
	}
	
	/**
	 * Retrieves a copy of the parsed style sheet.
	 * This is used to get a sheet for vendors and device-groups.
	 * The style sheet is not fully processed so far, for example
	 * the inheritance relations are not set yet.
	 * 
	 * @return a copy of the parsed style sheet.
	 */
	public StyleSheet getStyleSheetCopy() {
		return new StyleSheet( this.styleSheet );
	}
	
	/**
	 * Retrieves the parsed style sheet.
	 * The style sheet is not fully processed so far, for example
	 * the inheritance relations are not set yet.
	 *  
	 * @return the style sheet used by this reader.
	 */
	public StyleSheet getStyleSheet() {
		return this.styleSheet;
	}

	
	/**
	 * Removes all CSS comments from the given buffer.
	 * 
	 * @param buffer the CSS declarations
	 * @return the CSS declarations without comments.
	 */
	public final static StringBuffer removeCssComments( StringBuffer buffer ) {
		StringBuffer clean = new StringBuffer( buffer.length() );
		char[] chars = buffer.toString().toCharArray();
		boolean inComment = false;
		char lastChar = 0;
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (c == '*' && lastChar == '/') {
				if (inComment) {
					throw new BuildException("Invalid CSS comment: A comment is opened within another comment in code: " + buffer.toString() );
				}
				inComment = true;
				//System.out.println("deleting char [" + (clean.length()-1) + "] from buffer [" + clean.toString() + "].");
				clean.deleteCharAt( clean.length()-1);
			} else if (c == '/' && lastChar == '*') {
				if (!inComment) {
					throw new BuildException("Invalid CSS comment: A comment is closed even though it is not opened in code: " + buffer.toString() );
				}
				inComment = false;
			} else if (!inComment) {
				clean.append( c );
			}
			lastChar = c;
		}
		if (inComment) {
			throw new BuildException("Invalid CSS comment: A comment is opened without closing it in code: " + buffer.toString() );
		}
		return clean;
	}

	/**
	 * Splits a CSS-StringBuffer into chunks.
	 * Each chunk will start with the name of class and end with
	 * the appropriate closing parenthesis '}'.
	 * 
	 * @param buffer the raw buffer containing the CSS definitions
	 * @return the buffer split into chunks.
	 */
	public static final String[] split(StringBuffer buffer) {
		int parenthesisCount = 0;
		ArrayList chunksList = new ArrayList();
		int blockStart = 0;
		char[] chars = buffer.toString().toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (c == '{') {
				parenthesisCount++;
			} else if (c == '}') {
				parenthesisCount--;
				if (parenthesisCount == 0) {
					chunksList.add( buffer.substring(blockStart, i + 1) );
					blockStart = i+ 1;
				} else if (parenthesisCount < 0) {
					throw new BuildException("Invalid CSS - there is at least one closing parenthesis '}' too much in this CSS code: " + buffer.toString() );
				}
			}
		}
		if (parenthesisCount > 0) {
			throw new BuildException("Invalid CSS - there is at least one opening parenthesis '{' too much in this CSS code: " + buffer.toString() );
		}
		return (String[]) chunksList.toArray( new String[ chunksList.size()]);
	}


	
}
