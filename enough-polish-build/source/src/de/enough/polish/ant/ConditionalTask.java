/*
 * Created on 26-Jan-2004 at 08:26:38.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant;

import org.apache.tools.ant.Task;

/**
 * <p>A task which execution can be triggered and stopped with if and unless parameters.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        26-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class ConditionalTask extends Task {

	private boolean isActive = true;

	/**
	 * Creates a new conditional task
	 */
	public ConditionalTask() {
		super();
	}
	
	/**
	 * Sets the ant-property which needs to be defined to allow the execution of this task.
	 *  
	 * @param ifExpr the ant-property which needs to be defined 
	 */
	public void setIf(String ifExpr) {
		this.isActive = this.isActive &&
			(getProject().getProperty(ifExpr) != null);
	}
	
	/**
	 * Sets the ant-property which must not be defined to allow the execution of this task.
	 * 
	 * @param unlessExpr the ant-property which must not be defined 
	 */
	public void setUnless(String unlessExpr) {
		this.isActive = this.isActive &&
			(getProject().getProperty(unlessExpr) == null);
	}

	/**
	 * Checks if this task should be executed.
	 * Subclasses should call isActive() to determine whether this task 
	 * should be executed.
	 * 
	 * @return true when this task should be executed.
	 */
	public boolean isActive() {
		return this.isActive;
	}
	

}
