/*
 * Created on 20-Jan-2003 at 15:05:18.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.util;

//#ifdef polish.debug.visual
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
//#endif

/**
 * <p>Is used for debugging of information.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        20-Jan-2003 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public final class Debug {
	//#ifdef polish.debug.visual
	static final ArrayList MESSAGES = new ArrayList( 100 );
	//#endif
	
	/**
	 * Prints a message with a time-stamp.
	 * 
	 * @param time the time in ms.
	 * @param message the message
	 */
	public static final void debug( long time, String message ) {
		debug( time + message );
	}
	
	/**
	 * Prints a message.
	 * 
	 * @param message the message.
	 * @param exception the exception
	 */
	public static final void debug( String message, Throwable exception ) {
		//#ifndef polish.debug.visual
		System.out.println( message );
		if (exception != null) {
			exception.printStackTrace();
		}
		//#else
		// add message to list:
		MESSAGES.add( message );
		if (MESSAGES.size() > 98) {
			MESSAGES.remove( 0 );
		}
		//#endif
	}
	
	/**
	 * Prints a message.
	 * 
	 * @param message the message.
	 */
	public static final void debug( String message ) {
		debug( message, null );
	}
	
	//#ifdef polish.debug.visual
	static final Form getMessagesBox( boolean reverseSort ) {
		String[] messages = (String[]) MESSAGES.toArray( new String[ MESSAGES.size() ] );
		StringItem[] items = new StringItem[ messages.length ];
		int index = messages.length - 1;
		for (int i = 0; i < items.length; i++) {
			String message;
			if (reverseSort) {
				message = messages[ index ];
				index--;
			} else {
				message = messages[i];
			}
			items[i] = new StringItem( null, message );
		}
		return new Form( "debug", items );
	}
	//#endif
	
}
