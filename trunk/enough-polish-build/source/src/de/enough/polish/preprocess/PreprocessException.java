/*
 * Created on 16-Jan-2004 at 13:52:38.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.preprocess;

/**
 * <p>Exception thrown when the preprocessing fails.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        16-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class PreprocessException extends Exception {


	/**
	 * Creates a new PreprocessException.
	 * 
	 * @param description explanation of the error
	 */
	public PreprocessException(String description) {
		super(description);
	}

	/**
	 * Creates a new PreprocessException.
	 * 
	 * @param throwable the source exception
	 */
	public PreprocessException(Throwable throwable) {
		super(throwable);
	}

	/**
	 * Creates a new PreprocessException.
	 * 
	 * @param description explanation of the error
	 * @param throwable the source exception
	 */
	public PreprocessException(String description, Throwable throwable) {
		super(description, throwable);
	}

}
