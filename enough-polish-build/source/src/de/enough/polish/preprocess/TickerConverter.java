/*
 * Created on 12-Jun-2004 at 15:11:40.
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
package de.enough.polish.preprocess;

import de.enough.polish.util.StringList;

/**
 * <p>Converts call from MIDP getTicker() and setTicker(...) to J2ME Polish getPolishTicker() and setPolishTicker().</p>
 * <p>This is needed because in MIDP/2.0 the Ticker-calls have been moved from the
 * Screen class to the Displayable-class.
 * </p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        12-Jun-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public final class TickerConverter {

	/**
	 * Converts all getTicker()- and setTicker()-calls to the J2ME Polish-equivalent.
	 * 
	 * @param lines the source-code
	 * @return true when the source-code has been changed.
	 */
	public static final boolean convertTickerCalls( StringList lines ) {
		lines.reset();
		boolean changed = false;
		while (lines.next() ) {
			String line = lines.getCurrent();
			int startPos = line.indexOf(".getTicker"); 
			if ( startPos != -1) {
				line = line.substring(0, startPos)
					+ ".getPolishTicker"
					+ line.substring( startPos + 10 );
				changed = true;
				lines.setCurrent( line );
			}
			startPos = line.indexOf(".setTicker");
			if ( startPos != -1) {
				line = line.substring(0, startPos)
					+ ".setPolishTicker"
					+ line.substring( startPos + 10 );
				changed = true;
				lines.setCurrent( line );
			}
		}
		return changed;
	}

}
