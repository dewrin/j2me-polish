/*
 * Created on 15-Jan-2004 at 15:00:29.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish;

import de.enough.polish.ant.build.Variable;

import java.util.*;

/**
 * <p>Provides common functionalities for Project, Vendor, DeviceGroup and Device.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        15-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class PolishComponent {
	
	protected String preprocessName;
	protected PolishComponent parent;
	protected ArrayList symbols;
	protected ArrayList variables;

	/**
	 * Creates a new component.
	 * 
	 * @param preprocessName the specific name used for symbols and variables, e.g. "device"
	 */
	public PolishComponent( String preprocessName ) {
		this( preprocessName, null );
	}
	
	/**
	 * Creates a new component.
	 * 
	 * @param preprocessName the specific name used for symbols and variables, e.g. "device"
	 * @param parent the parent, e.g. is the parent of a vendor a project,
	 *              the parent of a device is a vendor.
	 */
	public PolishComponent( String preprocessName, PolishComponent parent ) {
		this.preprocessName = preprocessName;
		this.parent = parent;
		this.variables = new ArrayList();
		this.symbols = new ArrayList();
	}
	
	/**
	 * Retrieves all preprocessing-symbols of this component and its parent component.
	 * The symbols are arranged in a HashMap, so one can check for the definition
	 * of a symbol with <code> if (map.get( "symbol-name") != null) { // symbol is defined</code>.
	 * <p>Symbols can be retrieved in different ways:
	 * <ul>
	 * 	<li><b>polish.symbol-name</b>: this is the recommended way to check for a symbol.
	 * 				The symbols starting with "polish" can be defined by  the project, the vendor, 
	 * 				group or device used.</li>
	 * 	<li><b>project.symbol-name</b>: this name can be used to check for symbols which
	 * 				need to be defined in the project itself.</li>
	 * 	<li><b>manufacturer.symbol-name</b>: this name can be used to check for symbols which
	 * 				need to be defined in the current manufacturer definition (e.g. Nokia) itself.</li>
	 * 	<li><b>group.symbol-name</b>: this name can be used to check for symbols which
	 * 				need to be defined in the current groups (e.g. Series 60) itself.</li>
	 * 	<li><b>device.symbol-name</b>: this name can be used to check for symbols which
	 * 				need to be defined in the current device itself.</li>
	 * </ul>
	 * </p>
	 * @return the HashMap containing all names of the defined symbols as keys.
	 */
	public HashMap getSymbols() {
		HashMap definedSymbols;
		if (this.parent != null) {
			definedSymbols  = this.parent.getSymbols();
		} else {
			definedSymbols  = new HashMap();
		}
		// adding all defined symbols:
		for (Iterator iter = this.symbols.iterator(); iter.hasNext();) {
			String symbol = (String) iter.next();
			definedSymbols.put( "polish." + symbol, Boolean.TRUE );
			definedSymbols.put( this.preprocessName + "." + symbol, Boolean.TRUE );
		}
		// adding all variables-names as symbols:
		for (Iterator iter = this.variables.iterator(); iter.hasNext();) {
			Variable variable = (Variable) iter.next();
			String varName = variable.getName() + ":defined";
			definedSymbols.put( "polish." + varName, Boolean.TRUE );
			definedSymbols.put( this.preprocessName + "." + varName , Boolean.TRUE );
		}
		return definedSymbols;
	}
	
	/**
	 * Retrieves all preprocessing-variables of this component and its parent component.
	 * The values defined by a variable can be retrieved by calling 
	 * <code>String value = (String) map.get("variable-name")</code>.
	 * 
	 * <p>Variables can be named in different ways:
	 * <ul>
	 * 	<li><b>polish.variable-name</b>: this is the recommended way to check for a variable.
	 * 				The variables starting with "polish" can be defined by  the project, the vendor, 
	 * 				group or device used. They also can be overriden by specific settings.
	 * 			    For example a color "color.focus" could be defined in the project and
	 * 				also be defined in a device definition. "polish.color.focus" would then
	 * 			 	return the device definition while "project.color " would return the original 
	 * 				project definition, when the application is preprocessed for that
	 * 				device.</li>
	 * 	<li><b>project.variable-name</b>: this name can be used to check for variables which
	 * 				need to be defined in the project itself.</li>
	 * 	<li><b>manufacturer.variable-name</b>: this name can be used to check for variables which
	 * 				need to be defined in the current manufacturer definition (e.g. Nokia) itself.</li>
	 * 	<li><b>group.variable-name</b>: this name can be used to check for variables which
	 * 				need to be defined in the current groups (e.g. Series 60) itself.</li>
	 * 	<li><b>device.variable-name</b>: this name can be used to check for variables which
	 * 				need to be defined in the current device itself.</li>
	 * </ul>
	 * </p>
	 * @return a HashMap containing all names of the defined variables as the keys.
	 */
	public HashMap getVariables() {
		HashMap definedVars;
		if (this.parent != null) {
			definedVars = this.parent.getVariables();
		} else {
			definedVars = new HashMap();
		}
		for (Iterator iter = this.variables.iterator(); iter.hasNext();) {
			Variable variable = (Variable) iter.next();
			definedVars.put( "polish." + variable.getName(), variable.getValue() );
			definedVars.put( this.preprocessName + "." + variable.getName(), variable.getValue() );
		}
		return definedVars;
	}

}
