/*
 * Created on 23-Feb-2004 at 14:24:04.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.build;

import org.apache.tools.ant.BuildException;

import java.util.ArrayList;

/**
 * <p>Containts information about the obfuscator which should be used.</p>
 * <p>Can be used for a more detailed setting than just sing the 
 * &lt;build&gt;attributes "obfuscator" and "obsucate".</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        23-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class ObfuscatorSetting {
	
	private ArrayList keeps;
	private boolean enable;
	private String name;
	private String className;

	/**
	 * Creates a new empty obfuscator setting. 
	 */
	public ObfuscatorSetting() {
		this.keeps = new ArrayList();
		this.enable = true;
	}
	
	public void addConfiguredKeep( Keep keep ) {
		if (keep.getClassName() == null) {
			throw new BuildException("The <keep> element needs to define the attribute [class]. Please check your <obfuscator> setting.");
		}
		this.keeps.add( keep );
	}
	
	public void addConfiguredPreserve( Keep keep ) {
		addConfiguredKeep(keep);
	}
	
	public void setEnable( boolean enable ) {
		this.enable = enable;
	}
	
	public boolean isEnabled() {
		return this.enable;
	}
	
	public void setName( String name ) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setClass( String className ) {
		this.className = className;
	}
	
	public String getClassName() {
		return this.className;
	}
	
	/**
	 * Retrieves the names of classes which should not be obfuscated.
	 * 
	 * @return An array with the names of classes which should not be obfuscated.
	 */
	public String[] getPreserveClassNames() {
		Keep[] keepDefinitions = (Keep[]) this.keeps.toArray( new Keep[ this.keeps.size() ] );
		String[] preserves = new String[ keepDefinitions.length ];
		for (int i = 0; i < keepDefinitions.length; i++) {
			Keep keep = keepDefinitions[i];
			preserves[i] = keep.getClassName();
		}
		return preserves;
	}

}
