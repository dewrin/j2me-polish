/*
 * Created on Jun 18, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.enough.polish.ant.build;

import de.enough.polish.Variable;

/**
 * @author robertvirkus
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Attribute extends Variable {

	private String ifValue;

	/**
	 * Creates a new empty attribute
	 */
	public Attribute() {
		super();
	}
	
	/**
	 * Creates a new attribute 
	 * 
	 * @param name the name of the attribute
	 * @param value the value of the attribute
	 */
	public Attribute(String name, String value) {
		super(name, value);
	}
	
	public void setIf( String ifValue ) {
		this.ifValue = ifValue;
	}
	
	public String getIf() {
		return this.ifValue;
	}
}
