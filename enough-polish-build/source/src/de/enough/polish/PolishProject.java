/*
 * Created on 15-Jan-2004 at 15:57:31.
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
package de.enough.polish;

import de.enough.polish.preprocess.*;


/**
 * <p>Represents a J2ME project defined by the ant settings in the build.xml file.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        15-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class PolishProject extends PolishComponent {
	
	private boolean usesPolishGui;
	private boolean isDebugEnabled;
	private DebugManager debugManager;

	/**
	 * Creates a new project.
	 * 
	 * @param usesPolishGui true when this project uses the polish GUI framework.
	 * @param isDebugEnabled true when debugging is enabled at all.
	 * @param debugManager manages specific debugging settings.
	 */
	public PolishProject( boolean usesPolishGui, boolean isDebugEnabled, DebugManager debugManager ) {
		this.usesPolishGui = usesPolishGui;
		this.isDebugEnabled = isDebugEnabled;
		this.debugManager = debugManager;
	}
	
	/**
	 * Determines whether this project  uses the polish GUI-framework.
	 * 
	 * @return true when this project uses the polish GUI-framework.
	 */
	public boolean usesPolishGui() {
		return this.usesPolishGui;
	}
	
	/**
	 * Retrieves the debug manager which is responsible for specific debugging settings.
	 * 
	 * @return the debug manager.
	 */
	public DebugManager getDebugManager() {
		return this.debugManager;
	}

	/**
	 * Determines whether debugging is allowed.
	 * 
	 * @return true when debugging is enabled at all.
	 */
	public boolean isDebugEnabled() {
		return this.isDebugEnabled;
	}



}
