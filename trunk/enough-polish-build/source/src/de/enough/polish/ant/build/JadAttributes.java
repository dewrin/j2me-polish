/*
 * Created on Jun 18, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.enough.polish.ant.build;

import java.util.ArrayList;

import org.apache.tools.ant.BuildException;

/**
 * <p>Represents user-defined attributes for the JAD and the MANIFEST</p>
 * 
 * @author robert virkus, j2mepolish@enough.de
 */
public class JadAttributes {

	private ArrayList list;

	/**
	 * Creates a new list 
	 */
	public JadAttributes() {
		this.list = new ArrayList();
	}
	
	public void addAttribute( Attribute attribute ) {
		if (attribute.getName() == null) {
			throw new BuildException("Please check your <jad> definition, each attribute needs to have the attribute [name]");
		}
		if (attribute.getValue() == null) {
			throw new BuildException("Please check your <jad> definition, each attribute needs to have the attribute [value]");
		}
		this.list.add( list );
	}
	
	public Attribute[] getAttributes(){
		return (Attribute[]) this.list.toArray( new Attribute[ this.list.size() ] );
	}

}
