/*
 * Created on 24-Nov-2003 at 14:48:58
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
*/
package de.enough.polish.build.util;

/**
 * <p>Manages a String-array.</p>
 * <p>
 * Example:
 * <code>
 * String[] lines = readFile();
 * StringList list = new StringList( lines );
 * while (list.next()) {
 * 	String line = list.getCurrent();
 * 	// process line...
 * }
 * </code>
 * </p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        14-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
class StringList {
	private String[] lines;
	private int startIndex = 0;
	private int currentIndex = -1;
	
	/**
	 * Creates a new StringList.
	 * 
	 * @param lines the String array which should be managed by this list.
	 */
	public StringList( String[] lines ) {
		this.lines = lines;
	}
	
	/**
	 * Retrieves the current index of this list.
	 * 
	 * @return the current index, the first index has the value 0.
	 */
	public int getCurrentIndex() {
		return this.currentIndex;
	}
	
	/**
	 * Retrieves the current line.
	 * next() has to be called at least one time before getCurrent() can be called.
	 * 
	 * @return the current line.
	 * @see #next()
	 * @throws ArrayIndexOutOfBoundsException when next() has never been called before.
	 */
	public String getCurrent() {
		if (this.currentIndex == -1) {
			throw new ArrayIndexOutOfBoundsException("StringList: next() has to be called at least once before getCurrent() can be called.");
		}
		return this.lines[ this.currentIndex ];
	}
	
	/**
	 * Updates the current line.
	 *  
	 * @param value the new value of the current line.
	 * @see #getCurrent()
	 */
	public void setCurrent( String value ) {
		this.lines[ this.currentIndex ] = value;
	}

	/**
	 * Shifts the current index one step further.
	 * 
	 * @return true when there is a next line.
	 */
	public boolean next() {
		if (this.currentIndex < this.lines.length) {
			this.currentIndex++;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Sets the new index from where lines can be read.
	 * The current index will also be set on one less than the new start index.
	 * So after calling next() the getCurrent()-method will return lines[startIndex] 
	 * 
	 * @param startIndex the new start index, 0 is the first line.
	 */
	public void setStartIndex( int startIndex ) {
		this.startIndex = startIndex;
		this.currentIndex = startIndex - 1;
	}
	
	/**
	 * Resets the current index to the start index of this list.
	 * The getCurrent()-method will return lines[startIndex] after calling next(). 
	 */
	public void reset() {
		this.currentIndex = this.startIndex - 1;
	}
	
	
	/**
	 * Retrieves the internal array of this StringList.
	 * 
	 * @return the internal array of this StringList.
	 */
	public String[] getArray(){
		return this.lines;
	}
	
	/**
	 * Sets the value of the specified array-index.
	 *  
	 * @param index the array-index of the line 
	 * @param value the value of the line
	 * @throws ArrayIndexOutOfBoundsException when the 0 > index >= lines.length 
	 */
	public void set( int index, String value ) {
		this.lines[index] = value;
	}
	
}