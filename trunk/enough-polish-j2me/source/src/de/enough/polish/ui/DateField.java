//#condition polish.usePolishGui
/*
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
package de.enough.polish.ui;

import javax.microedition.lcdui.*;

import java.util.*;

/**
 * A <code>DateField</code> is an editable component for presenting
 * date and time (calendar)
 * information that may be placed into a <code>Form</code>. Value for
 * this field can be
 * initially set or left unset. If value is not set then the UI for the field
 * shows this clearly. The field value for &quot;not initialized
 * state&quot; is not valid
 * value and <code>getDate()</code> for this state returns <code>null</code>.
 * <p>
 * Instance of a <code>DateField</code> can be configured to accept
 * date or time information
 * or both of them. This input mode configuration is done by
 * <code>DATE</code>, <code>TIME</code> or
 * <code>DATE_TIME</code> static fields of this
 * class. <code>DATE</code> input mode allows to set only
 * date information and <code>TIME</code> only time information
 * (hours, minutes). <code>DATE_TIME</code>
 * allows to set both clock time and date values.
 * <p>
 * In <code>TIME</code> input mode the date components of
 * <code>Date</code> object
 * must be set to the &quot;zero epoch&quot; value of January 1, 1970.
 * <p>
 * Calendar calculations in this field are based on default locale and defined
 * time zone. Because of the calculations and different input modes date object
 * may not contain same millisecond value when set to this field and get back
 * from this field.
 * <HR>
 * 
 * @author Robert Virkus, robert@enough.de
 * @since MIDP 1.0
 */
public class DateField extends StringItem
implements CommandListener
{
	/**
	 * Input mode for date information (day, month, year). With this mode this
	 * <code>DateField</code> presents and allows only to modify date
	 * value. The time
	 * information of date object is ignored.
	 * 
	 * <P>Value <code>1</code> is assigned to <code>DATE</code>.</P></DL>
	 * 
	 */
	public static final int DATE = 1;

	/**
	 * Input mode for time information (hours and minutes). With this mode this
	 * <code>DateField</code> presents and allows only to modify
	 * time. The date components
	 * should be set to the &quot;zero epoch&quot; value of January 1, 1970 and
	 * should not be accessed.
	 * 
	 * <P>Value <code>2</code> is assigned to <code>TIME</code>.</P></DL>
	 * 
	 */
	public static final int TIME = 2;

	/**
	 * Input mode for date (day, month, year) and time (minutes, hours)
	 * information. With this mode this <code>DateField</code>
	 * presents and allows to modify
	 * both time and date information.
	 * 
	 * <P>Value <code>3</code> is assigned to <code>DATE_TIME</code>.</P></DL>
	 * 
	 * 
	 */
	public static final int DATE_TIME = 3;

	private Date date;
	private int inputMode;
	private int mode;
	private TimeZone timeZone;
	private boolean showCaret;
	private int originalWidth;
	private int originalHeight;
	private long lastCaretSwitch;
	private javax.microedition.lcdui.DateField midpDateField; 
	private javax.microedition.lcdui.Form form;

	private Calendar calendar;

	/**
	 * Creates a <code>DateField</code> object with the specified
	 * label and mode. This call
	 * is identical to <code>DateField(label, mode, null)</code>.
	 * 
	 * @param label item label
	 * @param mode the input mode, one of DATE, TIME or DATE_TIME
	 * @throws IllegalArgumentException if the input mode's value is invalid
	 */
	public DateField( String label, int mode)
	{
		this( label, mode, null, null );
	}

	/**
	 * Creates a <code>DateField</code> object with the specified
	 * label and mode. This call
	 * is identical to <code>DateField(label, mode, null)</code>.
	 * 
	 * @param label item label
	 * @param mode the input mode, one of DATE, TIME or DATE_TIME
	 * @param style the CSS style for this item
	 * @throws IllegalArgumentException if the input mode's value is invalid
	 */
	public DateField( String label, int mode, Style style)
	{
		this( label, mode, null, style );
	}
	
	/**
	 * Creates a date field in which calendar calculations are based
	 * on specific
	 * <code>TimeZone</code> object and the default calendaring system for the
	 * current locale.
	 * The value of the <code>DateField</code> is initially in the
	 * &quot;uninitialized&quot; state.
	 * If <code>timeZone</code> is <code>null</code>, the system's
	 * default time zone is used.
	 * 
	 * @param label item label
	 * @param mode the input mode, one of DATE, TIME or DATE_TIME
	 * @param timeZone a specific time zone, or null for the default time zone
	 * @throws IllegalArgumentException if the input mode's value is invalid
	 */
	public DateField( String label, int mode, TimeZone timeZone)
	{
		this( label, mode, timeZone, null );
	}

	/**
	 * Creates a date field in which calendar calculations are based
	 * on specific
	 * <code>TimeZone</code> object and the default calendaring system for the
	 * current locale.
	 * The value of the <code>DateField</code> is initially in the
	 * &quot;uninitialized&quot; state.
	 * If <code>timeZone</code> is <code>null</code>, the system's
	 * default time zone is used.
	 * 
	 * @param label item label
	 * @param mode the input mode, one of DATE, TIME or DATE_TIME
	 * @param timeZone a specific time zone, or null for the default time zone
	 * @param style the CSS style for this item
	 * @throws IllegalArgumentException if the input mode's value is invalid
	 */
	public DateField( String label, int mode, TimeZone timeZone, Style style)
	{
		super( label, null, INTERACTIVE, style );
		this.mode = mode;
		if (timeZone != null) {
			this.timeZone = timeZone;
		} else {
			this.timeZone = TimeZone.getDefault();
		}		
	}
	
	
	/**
	 * Returns date value of this field. Returned value is
	 * <code>null</code> if field
	 * value is
	 * not initialized. The date object is constructed according the rules of
	 * locale specific calendaring system and defined time zone.
	 * 
	 * In <code>TIME</code> mode field the date components are set to
	 * the &quot;zero
	 * epoch&quot; value of January 1, 1970. If a date object that presents time
	 * beyond one day from this &quot;zero epoch&quot; then this field
	 * is in &quot;not
	 * initialized&quot; state and this method returns <code>null</code>.
	 * 
	 * In <code>DATE</code> mode field the time component of the calendar is set
	 * to zero when
	 * constructing the date object.
	 * 
	 * @return date object representing time or date depending on input mode
	 * @see #setDate(java.util.Date)
	 */
	public Date getDate()
	{
		return this.date;
	}

	/**
	 * Sets a new value for this field. <code>null</code> can be
	 * passed to set the field
	 * state to &quot;not initialized&quot; state. The input mode of
	 * this field defines
	 * what components of passed <code>Date</code> object is used.<p>
	 * 
	 * In <code>TIME</code> input mode the date components must be set
	 * to the &quot;zero
	 * epoch&quot; value of January 1, 1970. If a date object that presents time
	 * beyond one day then this field is in &quot;not initialized&quot; state.
	 * In <code>TIME</code> input mode the date component of
	 * <code>Date</code> object is ignored and time
	 * component is used to precision of minutes.<p>
	 * 
	 * In <code>DATE</code> input mode the time component of
	 * <code>Date</code> object is ignored.<p>
	 * 
	 * In <code>DATE_TIME</code> input mode the date and time
	 * component of <code>Date</code> are used but
	 * only to precision of minutes.
	 * 
	 * @param date new value for this field
	 * @see #getDate()
	 */
	public void setDate( Date date)
	{
		this.date = date;
		if (this.midpDateField != null) {
			this.midpDateField.setDate( date );
		}
		// set date:
		if (date == null ) {
			if (this.mode == DATE) {
				//#if polish.DateFormat == us
					this.text = "MM-DD-YYYY";
				//#elif polish.DateFormat == de
					this.text = "TT.MM.JJJJ";
				//#else
					this.text = "YYYY-MM-DD";
				//#endif
			} else if (this.mode == TIME) {
				this.text = "hh:mm";
			} else {
				//#if polish.DateFormat == us
					this.text = "MM-DD-YYYY hh:mm";
				//#elif polish.DateFormat == de
					this.text = "TT.MM.JJJJ hh:mm";
				//#else
					this.text = "YYYY-MM-DD hh:mm";				
				//#endif
			}
		} else {
			if (this.calendar == null) {
				this.calendar = Calendar.getInstance();
				this.calendar.setTimeZone(this.timeZone);
			}
			this.calendar.setTime(date);
			StringBuffer buffer = new StringBuffer();
			if ((this.mode == DATE) || (this.mode == DATE_TIME)) {
				int year = this.calendar.get(Calendar.YEAR);
				int month = this.calendar.get( Calendar.MONTH );
				int day = this.calendar.get( Calendar.DAY_OF_MONTH );
				//#if polish.DateFormat == us
					if (month < 9) {
						buffer.append('0');
					}
					buffer.append( ++month )
				          .append("-");
					if (day < 10) {
						buffer.append( '0' );
					}
					buffer.append( day )
						  .append("-");
					buffer.append( year );
				//#elif polish.DateFormat == de
					if (day < 10) {
						buffer.append( '0' );
					}
					buffer.append( day )
						  .append(".");
					if (month < 9) {
						buffer.append('0');
					}
					buffer.append( ++month )
				          .append(".");
					buffer.append( year );
				//#else
					buffer.append( year )
					      .append("-");
					if (month < 9) {
						buffer.append('0');
					}
					buffer.append( ++month )
				          .append("-");
					if (day < 10) {
						buffer.append( '0' );
					}
					buffer.append( day );
				//#endif
				if  (this.mode == DATE_TIME) {
					buffer.append(' ');
				}
			}
			if ((this.mode == TIME) || (this.mode == DATE_TIME)) {
				int hour = this.calendar.get( Calendar.HOUR_OF_DAY );
				if (hour < 10) {
					buffer.append('0');
				}
				buffer.append( hour )
				      .append(':');
				int minute = this.calendar.get( Calendar.MINUTE );
				if (minute < 10) {
					buffer.append('0');
				}
				buffer.append( minute );
			}
			this.text = buffer.toString();
		} // date != null
		this.isInitialised = false;
	}

	/**
	 * Gets input mode for this date field. Valid input modes are
	 * <code>DATE</code>, <code>TIME</code> and <code>DATE_TIME</code>.
	 * 
	 * @return input mode of this field
	 * @see #setInputMode(int)
	 */
	public int getInputMode()
	{
		return this.inputMode;
	}

	/**
	 * Set input mode for this date field. Valid input modes are
	 * <code>DATE</code>, <code>TIME</code> and <code>DATE_TIME</code>.
	 * 
	 * @param mode the input mode, must be one of DATE, TIME or DATE_TIME
	 * @throws IllegalArgumentException if an invalid value is specified
	 * @see #getInputMode()
	 */
	public void setInputMode(int mode)
	{
		this.inputMode = mode;
		if (this.midpDateField != null) {
			this.midpDateField.setInputMode(mode);
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#paint(int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		super.paintContent(x, y, leftBorder, rightBorder, g);
		if (this.showCaret) {
			if (this.text == null) {
				// when the text is null the appropriate font and color
				// might not have been set, so set them now:
				g.setFont( this.font );
				g.setColor( this.textColor );
			}
			if (this.isLayoutCenter) {
				int centerX = leftBorder 
					+ (rightBorder - leftBorder) / 2 
					+ this.originalWidth / 2
					+ 2;
				if (this.originalHeight > 0) {
					y += this.originalHeight - this.font.getHeight();
				}
				g.drawChar('|', centerX, y, Graphics.TOP | Graphics.LEFT );
			} else {
				x += this.originalWidth + 2;
				if (this.originalHeight > 0) {
					y += this.originalHeight - this.font.getHeight();
				}
				g.drawChar('|', x, y, Graphics.TOP | Graphics.LEFT );
			}
		}
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#initItem()
	 */
	protected void initContent(int firstLineWidth, int lineWidth) {
		if (this.date == null) {
			if (this.mode == TIME) {
				setDate( new Date(0) );
			} else {
				setDate( new Date() );
			}
		}
		// init StringItem:
		super.initContent(firstLineWidth, lineWidth);
		this.originalWidth = this.contentWidth;
		this.originalHeight = this.contentHeight;
		if (this.contentWidth < this.minimumWidth) {
			this.contentWidth = this.minimumWidth;
		} 
		if (this.contentHeight < this.minimumHeight) {
			this.contentHeight = this.minimumHeight;
		} else  if (this.contentHeight < this.font.getHeight()) {
			this.contentHeight = this.font.getHeight();
			this.originalHeight = this.contentHeight;
		}
	}

	//#ifdef polish.useDynamicStyles
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#getCssSelector()
	 */
	protected String createCssSelector() {
		return "datefield";
	}
	//#endif

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		super.setStyle(style);
		String widthStr = style.getProperty("datefield-width");
		if (widthStr != null) {
			this.minimumWidth = Integer.parseInt( widthStr );
		}
		String heightStr = style.getProperty("datefield-height");
		if (heightStr != null) {
			this.minimumHeight = Integer.parseInt( heightStr );
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#animate()
	 */
	public boolean animate() {
		long currentTime = System.currentTimeMillis();
		if ( currentTime - this.lastCaretSwitch > 500 ) {
			this.lastCaretSwitch = currentTime;
			this.showCaret = ! this.showCaret;
			return true;
		} else {
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#defocus(de.enough.polish.ui.Style)
	 */
	protected void defocus(Style originalStyle) {
		super.defocus(originalStyle);
		this.showCaret = false;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handleKeyPressed(int, int)
	 */
	protected boolean handleKeyPressed(int keyCode, int gameAction) {
		if ((gameAction == Canvas.UP && keyCode != Canvas.KEY_NUM2) 
				|| (gameAction == Canvas.DOWN && keyCode != Canvas.KEY_NUM8)) {
			return false;
		}
		showDateForm();
		return true;
	}
	
	/**
	 * Shows the TextBox for entering texts.
	 */
	private void showDateForm() {
		if (this.midpDateField == null) {
			this.midpDateField = new javax.microedition.lcdui.DateField( this.label, this.mode, this.timeZone );
			this.midpDateField.setDate( this.date );
			this.form = new javax.microedition.lcdui.Form( StyleSheet.currentScreen.getTitle() );
			this.form.append( this.midpDateField );
			this.form.addCommand(StyleSheet.OK_CMD);
			this.form.addCommand(StyleSheet.CANCEL_CMD);
			this.form.setCommandListener( this );
		}
		StyleSheet.display.setCurrent( this.form );
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
	 */
	public void commandAction(Command cmd, Displayable box) {
		if (cmd == StyleSheet.CANCEL_CMD) {
			this.midpDateField.setDate( this.date );
		} else {
			setDate( this.midpDateField.getDate() );
			notifyStateChanged();
		}
		StyleSheet.display.setCurrent( StyleSheet.currentScreen );
	}
}
