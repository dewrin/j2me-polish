/*
 * Created on 25-Feb-2004 at 21:40:23.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant;

import org.apache.tools.ant.Project;

/**
 * <p>The base class for any nested element which can be conditional.</p>
 * <p>This class supports the attributes [if] and [unless]. 
 *    When the if-attribute is specified, the corresponding property
 *    needs to be defined in ant's build.xml.
 *    When the unless-attribute is defined, the corresponding property
 *    must not be defined in the build.xml.
 *    Classes can check if the conditions of this element are
 *    met by calling isActive().
 *    Nested elements which want to make use of other conditional nested
 *    element needs to have a reference to the ant-project.
 *    This can be done with the help of the create&lt;nested-element-name&gt; method.
 * </p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        25-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class ConditionalElement {
	
	private String ifExpression;
	private String unlessExpression;

	/** 
	 * Creates a new conditional element.
	 */
	public ConditionalElement() {
		// initialisation is done via the setter and getter methods.
	}
	
	/**
	 * Sets the ant-property which needs to be defined to allow the execution of this task.
	 *  
	 * @param ifExpr the ant-property which needs to be defined 
	 */
	public void setIf(String ifExpr) {
		this.ifExpression = ifExpr;
	}
	
	/**
	 * Sets the ant-property which must not be defined to allow the execution of this task.
	 * 
	 * @param unlessExpr the ant-property which must not be defined 
	 */
	public void setUnless(String unlessExpr) {
		this.unlessExpression = unlessExpr;
	}

	/**
	 * Checks if this element should be used.
	 * 
	 * @param project The project to which this nested element belongs to.
	 * @return true when this element is valid
	 */
	public boolean isActive( Project project ) {
		if (this.unlessExpression != null) {
			if (project.getProperty(this.unlessExpression) != null) {
				return false;
			}
		}
		if (this.ifExpression != null ) {
			if ( project.getProperty(this.ifExpression) == null) {
				return false;
			}
		}
		return true;
	}

	
}
