/*
 * Created on 15-Mar-2004 at 11:12:27.
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
package de.enough.polish.preprocess.backgrounds;

import de.enough.polish.preprocess.*;
import de.enough.polish.util.CastUtil;

import org.apache.tools.ant.BuildException;

import java.awt.Color;
import java.util.HashMap;

/**
 * <p>Creates the PulsatingBackground from CSS values.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        15-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class PulsatingBackgroundConverter extends BackgroundConverter {
	/**
	 * 
	 */
	public PulsatingBackgroundConverter() {
		super();
	}
	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.BackgroundConverter#createNewStatement(java.util.HashMap, de.enough.polish.preprocess.Style, de.enough.polish.preprocess.StyleSheet)
	 */
	protected String createNewStatement(HashMap background, Style style,
			StyleSheet styleSheet) 
	throws BuildException 
	{
		StringBuffer buffer = new StringBuffer();
		String startColorStr = (String) background.get("start-color");
		if (startColorStr == null) {
			throw new BuildException("Invalid CSS: the background type [pulsating] needs to define the attribute [start-color].");
		}
		startColorStr = this.colorConverter.parseColor(startColorStr);
		int startColor = Integer.decode( startColorStr ).intValue();
		String endColorStr = (String) background.get("end-color");
		if (endColorStr == null) {
			throw new BuildException("Invalid CSS: the background type [pulsating] needs to define the attribute [end-color].");
		}
		endColorStr = this.colorConverter.parseColor(endColorStr);
		int endColor = Integer.decode( endColorStr ).intValue();
		String stepsStr = (String) background.get("steps");
		int steps = 10;
		if (stepsStr != null) {
			steps = parseInt( "steps", stepsStr );
			if (steps < 2) {
				throw new BuildException("Invalid CSS: the [steps] attribute of the background type [pulsating] needs to be at least 2; the value [" + steps + "] is not valid.");
			}
		}
		Color rgbColor = new Color( startColor );
		int startRed = rgbColor.getRed();
		int startGreen = rgbColor.getGreen();
		int startBlue = rgbColor.getBlue();
		//System.out.println("start-red=" + startRed);
		//System.out.println("start-green=" + startGreen);
		//System.out.println("start-blue=" + startBlue);
		rgbColor = new Color( endColor );
		int endRed = rgbColor.getRed();
		int endGreen = rgbColor.getGreen();
		int endBlue = rgbColor.getBlue();
		double addRed = ((double)(startRed - endRed) / (double) steps);
		double addGreen = ((double)(startGreen - endGreen) / (double) steps);
		double addBlue = ((double)(startBlue - endBlue ) / (double) steps);
		int[] colors = new int[ steps ];
		for (int i = 0; i < steps; i++) {
			double count = (double) i;
			int red = startRed - (int)(count * addRed);
			int green = startGreen - (int)(count * addGreen );
			int blue = startBlue - (int)(count * addBlue);
			rgbColor = new Color( red, green, blue );
			colors[i] = rgbColor.getRGB();
		}
		colors[ steps -1 ] = endColor;
		buffer.append("new ")
			  .append( BACKGROUNDS_PACKAGE )
			  .append( "PulsatingBackground( new int[]{" );
		for (int i = 0; i < colors.length; i++) {
			buffer.append( colors[i] );
			if (i != (colors.length - 1)) {
				buffer.append(",");
			}
		}
		buffer.append("}, ");
		boolean repeat = true;
		String repeatStr = (String) background.get("repeat");
		if (repeatStr != null) {
			repeat = CastUtil.getBoolean( repeatStr );
		}
		buffer.append( repeat )
			  .append(", ");
		boolean backAndForth = true;
		String backAndForthStr = (String) background.get("back-and-forth");
		if (backAndForthStr != null) {
			backAndForth = CastUtil.getBoolean( backAndForthStr );
		}
		buffer.append( backAndForth )
			  .append(")");
		return buffer.toString();
	}
}
