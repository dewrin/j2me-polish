/*
 * Created on 20-Feb-2004 at 20:09:42.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.dict;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 * <p>A simple dictionary.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        20-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class DictMidlet extends MIDlet implements CommandListener {
	
	private Screen startScreen;
	private Display display;
	
	
	/**
	 * 
	 */
	public DictMidlet() {
		super();
		try {
			//#debug
			System.out.println("DictMidlet is created.");
			this.startScreen = new TextBox( "Hello", "what's going on?", 100, TextField.ANY );
			this.display = Display.getDisplay( this );
		} catch (Exception e) {
			//#mdebug error
			e.printStackTrace();
			System.out.println("unable to start midlet: " + e.getMessage() );
			//#enddebug
		}
		// TODO enough implement DictMidlet
	}
	
	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#startApp()
	 */
	protected void startApp() throws MIDletStateChangeException {
		//#debug
		System.out.println("DictMidlet is started.");
		this.display.setCurrent( this.startScreen );
		// TODO enough implement startApp
	}
	
	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#pauseApp()
	 */
	protected void pauseApp() {
		// TODO enough implement pauseApp
	}
	
	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#destroyApp(boolean)
	 */
	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
		// TODO enough implement destroyApp
	}
	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
	 */
	public void commandAction(Command cmd, Displayable screen) {
		// TODO enough implement commandAction
	}

}
