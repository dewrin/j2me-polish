/*
 * Created on 25-Feb-2004 at 21:40:23.
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
package de.enough.polish.ant;

import de.enough.polish.util.CastUtil;

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
			return ! CastUtil.getBoolean(project.getProperty(this.unlessExpression)); 
		}
		if (this.ifExpression != null ) {
			return CastUtil.getBoolean(project.getProperty(this.ifExpression)); 
		}
		return true;
	}

	
}
