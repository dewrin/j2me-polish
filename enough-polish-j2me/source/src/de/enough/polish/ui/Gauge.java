//#condition polish.usePolishGui
// generated by de.enough.doc2java.Doc2Java (www.enough.de) on Sat Dec 06 15:06:44 CET 2003
/*
 * Copyright (c) 2004 Robert Virkus / Enough Software
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
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.ui;

import de.enough.polish.util.Debug;

import javax.microedition.lcdui.*;

import java.io.IOException;

/**
 * Implements a graphical display, such as a bar graph, of an integer
 * value.  The <code>Gauge</code> contains a <em>current value</em>
 * that lies between zero and the <em>maximum value</em>, inclusive.
 * The application can control the current value and maximum value.
 * The range of values specified by the application may be larger than
 * the number of distinct visual states possible on the device, so
 * more than one value may have the same visual representation.
 * 
 * <P>For example, consider a <code>Gauge</code> object that has a
 * range of values from zero to <code>99</code>, running on a device
 * that displays the <code>Gauge's</code> approximate value using a
 * set of one to ten bars. The device might show one bar for values
 * zero through nine, two bars for values ten through <code>19</code>,
 * three bars for values <code>20</code> through <code>29</code>, and
 * so forth. </p>
 * 
 * <P>A <code>Gauge</code> may be interactive or
 * non-interactive. Applications may set or retrieve the
 * <code>Gauge's</code> value at any time regardless of the
 * interaction mode.  The implementation may change the visual
 * appearance of the bar graph depending on whether the object is
 * created in interactive mode. </p>
 * 
 * <P>In interactive mode, the user is allowed to modify the
 * value. The user will always have the means to change the value up
 * or down by one and may also have the means to change the value in
 * greater increments.  The user is prohibited from moving the value
 * outside the established range. The expected behavior is that the
 * application sets the initial value and then allows the user to
 * modify the value thereafter. However, the application is not
 * prohibited from modifying the value even while the user is
 * interacting with it. </p>
 * 
 * <p> In many cases the only means for the user to modify the value
 * will be to press a button to increase or decrease the value by one
 * unit at a time.  Therefore, applications should specify a range of
 * no more than a few dozen values. </p>
 * 
 * <P>In non-interactive mode, the user is prohibited from modifying
 * the value.  Non-interactive mode is used to provide feedback to the
 * user on the state of a long-running operation. One expected use of
 * the non-interactive mode is as a &quot;progress indicator&quot; or
 * &quot;activity indicator&quot; to give the user some feedback
 * during a long-running operation. The application may update the
 * value periodically using the <code>setValue()</code> method. </P>
 * 
 * <P>A non-interactive <code>Gauge</code> can have a definite or
 * indefinite range.  If a <code>Gauge</code> has definite range, it
 * will have an integer value between zero and the maximum value set
 * by the application, inclusive.  The implementation will provide a
 * graphical representation of this value such as described above.</p>
 * 
 * <P>A non-interactive <code>Gauge</code> that has indefinite range
 * will exist in one of four states: continuous-idle,
 * incremental-idle, continuous-running, or incremental-updating.
 * These states are intended to indicate to the user that some level
 * of activity is occurring.  With incremental-updating, progress can
 * be indicated to the user even though there is no known endpoint to
 * the activity.  With continuous-running, there is no progress that
 * gets reported to the user and there is no known endpoint;
 * continuous-running is merely a busy state indicator. The
 * implementation should use a graphical display that shows this
 * appropriately.  The implementation may use different graphics for
 * indefinite continuous gauges and indefinite incremental gauges.
 * Because of this, separate idle states exist for each mode.  For
 * example, the implementation might show an hourglass or spinning
 * watch in the continuous-running state, but show an animation with
 * different states, like a beach ball or candy-striped bar, in the
 * incremental-updating state.</p>
 * 
 * <p>In the continuous-idle or incremental-idle state, the
 * <code>Gauge</code> indicates that no activity is occurring. In the
 * incremental-updating state, the <code>Gauge</code> indicates
 * activity, but its graphical representation should be updated only
 * when the application requests an update with a call to
 * <code>setValue()</code>.  In the continuous-running state, the
 * <code>Gauge</code> indicates activity by showing an animation that
 * runs continuously, without update requests from the
 * application.</p>
 * 
 * <p>The values <code>CONTINUOUS_IDLE</code>,
 * <code>INCREMENTAL_IDLE</code>, <code>CONTINUOUS_RUNNING</code>, and
 * <code>INCREMENTAL_UPDATING</code> have their special meaning only
 * when the <code>Gauge</code> is non-interactive and has been set to
 * have indefinite range.  They are treated as ordinary values if the
 * <code>Gauge</code> is interactive or if it has been set to have a
 * definite range.</p>
 * 
 * <P>An application using the <code>Gauge</code> as a progress
 * indicator should typically also attach a <A HREF="../../../javax/microedition/lcdui/Command.html#STOP"><CODE>STOP</CODE></A>
 * command to the container containing the <code>Gauge</code> to allow
 * the user to halt the operation in progress.</p>
 * 
 * <h3>Notes for Application Developers</h3>
 * 
 * <P>As mentioned above, a non-interactive <code>Gauge</code> may be
 * used to give user feedback during a long-running operation.  If the
 * application can observe the progress of the operation as it
 * proceeds to an endpoint known in advance, then the application
 * should use a non-interactive <code>Gauge</code> with a definite
 * range.  For example, consider an application that is downloading a
 * file known to be <code>20</code> kilobytes in size.  The
 * application could set the <code>Gauge's</code> maximum value to be
 * <code>20</code> and set its value to the number of kilobytes
 * downloaded so far.  The user will be presented with a
 * <code>Gauge</code> that shows the portion of the task completed at
 * any given time.</P>
 * 
 * <P>If, on the other hand, the application is downloading a file of
 * unknown size, it should use a non-interactive <code>Gauge</code>
 * with indefinite range.  Ideally, the application should call
 * <CODE>setValue(INCREMENTAL_UPDATING)</CODE> periodically, perhaps
 * each time its input buffer has filled.  This will give the user an
 * indication of the rate at which progress is occurring.</P>
 * 
 * <P>Finally, if the application is performing an operation but has
 * no means of detecting progress, it should set a non-interactive
 * <code>Gauge</code> to have indefinite range and set its value to
 * <CODE>CONTINUOUS_RUNNING</CODE> or <CODE>CONTINUOUS_IDLE</CODE> as
 * appropriate.  For example, if the application has issued a request
 * to a network server and is about to block waiting for the server to
 * respond, it should set the <code>Gauge's</code> state to
 * <CODE>CONTINUOUS_RUNNING</CODE> before awaiting the response, and it
 * should set the state to <CODE>CONTINUOUS_IDLE</CODE> after it has
 * received the response.</P>
 * <HR>
 * 
 * @author Robert Virkus, robert@enough.de
 * @since MIDP 1.0
 */
public class Gauge extends Item
//#ifdef polish.images.backgroundLoad
implements ImageConsumer
//#endif
{
	/**
	 * A special value used for the maximum value in order to indicate that
	 * the <code>Gauge</code> has indefinite range.  This value may be
	 * used as the <code>maxValue</code>
	 * parameter to the constructor, the parameter passed to
	 * <code>setMaxValue()</code>, and
	 * as the return value of <code>getMaxValue()</code>.
	 * <P>
	 * The value of <code>INDEFINITE</code> is <code>-1</code>.</P>
	 * 
	 * 
	 * @since MIDP 2.0
	 */
	public static final int INDEFINITE = -1;

	/**
	 * The value representing the continuous-idle state of a
	 * non-interactive <code>Gauge</code> with indefinite range.  In
	 * the continuous-idle state, the gauge shows a graphic
	 * indicating that no work is in progress.
	 * 
	 * <p>This value has special meaning only for non-interactive
	 * gauges with indefinite range.  It is treated as an ordinary
	 * value for interactive gauges and for non-interactive gauges
	 * with definite range.</p>
	 * 
	 * <p>The value of <code>CONTINUOUS_IDLE</code> is
	 * <code>0</code>.</p>
	 * 
	 * 
	 * @since MIDP 2.0
	 */
	public static final int CONTINUOUS_IDLE = 0;

	/**
	 * The value representing the incremental-idle state of a
	 * non-interactive <code>Gauge</code> with indefinite range.  In
	 * the incremental-idle state, the gauge shows a graphic
	 * indicating that no work is in progress.
	 * 
	 * <p>This value has special meaning only for non-interactive
	 * gauges with indefinite range.  It is treated as an ordinary
	 * value for interactive gauges and for non-interactive gauges
	 * with definite range.</p>
	 * 
	 * <p>The value of <code>INCREMENTAL_IDLE</code> is
	 * <code>1</code>.</p>
	 * 
	 * 
	 * @since MIDP 2.0
	 */
	public static final int INCREMENTAL_IDLE = 1;

	/**
	 * The value representing the continuous-running state of a
	 * non-interactive <code>Gauge</code> with indefinite range.  In
	 * the continuous-running state, the gauge shows a
	 * continually-updating animation sequence that indicates that
	 * work is in progress.  Once the application sets a gauge into
	 * the continuous-running state, the animation should proceed
	 * without further requests from the application.
	 * 
	 * <p>This value has special meaning only for non-interactive
	 * gauges with indefinite range.  It is treated as an ordinary
	 * value for interactive gauges and for non-interactive gauges
	 * with definite range.</p>
	 * 
	 * <p>The value of <code>CONTINUOUS_RUNNING</code> is
	 * <code>2</code>.</p>
	 * 
	 * 
	 * @since MIDP 2.0
	 */
	public static final int CONTINUOUS_RUNNING = 2;

	/**
	 * The value representing the incremental-updating state of a
	 * non-interactive <code>Gauge</code> with indefinite range.  In
	 * the incremental-updating state, the gauge shows a graphic
	 * indicating that work is in progress, typically one frame of an
	 * animation sequence.  The graphic should be updated to the next
	 * frame in the sequence only when the application calls
	 * <code>setValue(INCREMENTAL_UPDATING)</code>.
	 * 
	 * <p>This value has special meaning only for non-interactive
	 * gauges with indefinite range.  It is treated as an ordinary
	 * value for interactive gauges and for non-interactive gauges
	 * with definite range.</p>
	 * 
	 * <p> The value of <code>INCREMENTAL_UPDATING</code> is
	 * <code>3</code>.</p>
	 * 
	 * 
	 * 
	 * @since MIDP 2.0
	 */
	public static final int INCREMENTAL_UPDATING = 3;
	
	private static final int MODE_CONTINUOUS = 0; 
	private static final int MODE_CHUNKED = 1;

	private int value;
	private String valueString;
	private int maxValue;
	private boolean isInteractive;
	private int color = 0x0000FF; //default color is blue
	private int mode = MODE_CHUNKED;
	private int chunkWidth = 6;
	private int gapWidth = 3;
	private int gapColor = 0xFFFFFF; // default gap color is white
	private Image image;
	private Image indicatorImage;
	private boolean isIndefinite;
	private int indefinitePos;
	//#ifdef polish.midp2
	private javax.microedition.lcdui.Gauge midpGauge;
	//#endif
	private boolean showValue = true;
	private boolean isValueLeft = true;
	private int fontColor;
	private Font font;
	private int valueWidth;

	/**
	 * Creates a new <code>Gauge</code> object with the given
	 * label, in interactive or non-interactive mode, with the given
	 * maximum and initial values.  In interactive mode (where
	 * <code>interactive</code> is <code>true</code>) the maximum
	 * value must be greater than zero, otherwise an exception is
	 * thrown.  In non-interactive mode (where
	 * <code>interactive</code> is <code>false</code>) the maximum
	 * value must be greater than zero or equal to the special value
	 * <code>INDEFINITE</code>, otherwise an exception is thrown.
	 * 
	 * <p>If the maximum value is greater than zero, the gauge has
	 * definite range.  In this case the initial value must be within
	 * the range zero to <code>maxValue</code>, inclusive.  If the
	 * initial value is less than zero, the value is set to zero.  If
	 * the initial value is greater than <code>maxValue</code>, it is
	 * set to <code>maxValue</code>.</p>
	 * 
	 * <p>If <code>interactive</code> is <code>false</code> and the
	 * maximum value is <code>INDEFINITE</code>, this creates a
	 * non-interactive gauge with indefinite range. The initial value
	 * must be one of <code>CONTINUOUS_IDLE</code>,
	 * <code>INCREMENTAL_IDLE</code>, <code>CONTINUOUS_RUNNING</code>,
	 * or <code>INCREMENTAL_UPDATING</code>.</p>
	 * 
	 * @param label the Gauge's label
	 * @param interactive tells whether the user can change the value
	 * @param maxValue the maximum value, or INDEFINITE
	 * @param initialValue the initial value in the range [0..maxValue], or one of CONTINUOUS_IDLE, INCREMENTAL_IDLE, CONTINUOUS_RUNNING, or INCREMENTAL_UPDATING if maxValue is INDEFINITE.
	 * @throws IllegalArgumentException if maxValue is not positive for interactive gauges
	 * 												  or if maxValue is neither positive nor INDEFINITE for non-interactive gauges
	 * 												  or if initialValue is not one of CONTINUOUS_IDLE, INCREMENTAL_IDLE, CONTINUOUS_RUNNING, or INCREMENTAL_UPDATING for a non-interactive gauge with indefinite range
	 * @see #INDEFINITE, #CONTINUOUS_IDLE, #INCREMENTAL_IDLE, #CONTINUOUS_RUNNING, #INCREMENTAL_UPDATING
	 */
	public Gauge( String label, boolean interactive, int maxValue, int initialValue)
	{
		this( label, interactive, maxValue, initialValue, null );
	}

	/**
	 * Creates a new <code>Gauge</code> object with the given
	 * label, in interactive or non-interactive mode, with the given
	 * maximum and initial values.  In interactive mode (where
	 * <code>interactive</code> is <code>true</code>) the maximum
	 * value must be greater than zero, otherwise an exception is
	 * thrown.  In non-interactive mode (where
	 * <code>interactive</code> is <code>false</code>) the maximum
	 * value must be greater than zero or equal to the special value
	 * <code>INDEFINITE</code>, otherwise an exception is thrown.
	 * 
	 * <p>If the maximum value is greater than zero, the gauge has
	 * definite range.  In this case the initial value must be within
	 * the range zero to <code>maxValue</code>, inclusive.  If the
	 * initial value is less than zero, the value is set to zero.  If
	 * the initial value is greater than <code>maxValue</code>, it is
	 * set to <code>maxValue</code>.</p>
	 * 
	 * <p>If <code>interactive</code> is <code>false</code> and the
	 * maximum value is <code>INDEFINITE</code>, this creates a
	 * non-interactive gauge with indefinite range. The initial value
	 * must be one of <code>CONTINUOUS_IDLE</code>,
	 * <code>INCREMENTAL_IDLE</code>, <code>CONTINUOUS_RUNNING</code>,
	 * or <code>INCREMENTAL_UPDATING</code>.</p>
	 * 
	 * @param label the Gauge's label
	 * @param interactive tells whether the user can change the value
	 * @param maxValue the maximum value, or INDEFINITE
	 * @param initialValue the initial value in the range [0..maxValue], or one of CONTINUOUS_IDLE, INCREMENTAL_IDLE, CONTINUOUS_RUNNING, or INCREMENTAL_UPDATING if maxValue is INDEFINITE.
	 * @param style the CSS style for this item
	 * @throws IllegalArgumentException if maxValue is not positive for interactive gauges
	 * 												  or if maxValue is neither positive nor INDEFINITE for non-interactive gauges
	 * 												  or if initialValue is not one of CONTINUOUS_IDLE, INCREMENTAL_IDLE, CONTINUOUS_RUNNING, or INCREMENTAL_UPDATING for a non-interactive gauge with indefinite range
	 * @see #INDEFINITE, #CONTINUOUS_IDLE, #INCREMENTAL_IDLE, #CONTINUOUS_RUNNING, #INCREMENTAL_UPDATING
	 */
	public Gauge( String label, boolean interactive, int maxValue, int initialValue, Style style )
	{
		super( label, Item.LAYOUT_DEFAULT, Item.PLAIN, style );
		// check values:
		//#ifndef polish.skipArgumentCheck
			if (interactive) {
				if (maxValue < 0 ) {
					//#ifdef polish.debugVerbose
						throw new IllegalArgumentException("Invalid maxValue for interactive Gauge: " + maxValue );
					//#else
						//# throw new IllegalArgumentException();
					//#endif
				}
				if (initialValue < 0 || initialValue > maxValue) {
					//#ifdef polish.debugVerbose
						throw new IllegalArgumentException("Invalid initialValue for interactive Gauge: " + initialValue );
					//#else
						//# throw new IllegalArgumentException();
					//#endif
				}
			} else {
				if (maxValue == INDEFINITE) {
					this.isIndefinite = true;
					this.maxValue = 20;
					if ( !( initialValue == CONTINUOUS_IDLE 
							|| initialValue == CONTINUOUS_RUNNING
							|| initialValue == INCREMENTAL_IDLE
							|| initialValue == INCREMENTAL_UPDATING ) ) {
						//#ifdef polish.debugVerbose
							throw new IllegalArgumentException("Invalid initialValue for indefinite Gauge: " + initialValue );
						//#else
							//# throw new IllegalArgumentException();
						//#endif
					}
				} else if (maxValue < 0 ) {
					//#ifdef polish.debugVerbose
						throw new IllegalArgumentException("Invalid maxValue for Gauge: " + maxValue );
					//#else
						//# throw new IllegalArgumentException();
					//#endif
				} else if (initialValue < 0 || initialValue > maxValue) {
					//#ifdef polish.debugVerbose
						throw new IllegalArgumentException("Invalid initialValue for Gauge: " + initialValue );
					//#else
						//# throw new IllegalArgumentException();
					//#endif
				}			
			}
		//#endif
		// set values
		this.isInteractive = interactive;
		if (interactive) {
			this.appearanceMode = Item.INTERACTIVE;
		}
		this.maxValue = maxValue;
		this.isIndefinite = (maxValue == INDEFINITE);
		setValue( initialValue );
	}

	//#ifdef polish.midp2
	/**
	 * Gets the MIDP Gauge class.
	 * This is used for Gauge elements which are added to an Alert.
	 * Compare Alert#setIndicator( Gauge ).
	 * This method is only avaiable on MIDP/2.0 devices.
	 * 
	 * @return the MIDP Gauge class.
	 */
	public javax.microedition.lcdui.Gauge getMidpGauge() {
		if (this.midpGauge == null) {
			this.midpGauge = new javax.microedition.lcdui.Gauge( this.label, this.isInteractive, this.maxValue, this.value ); 
		}
		return this.midpGauge;
	}
	//#endif

	/**
	 * Sets the current value of this <code>Gauge</code> object.
	 * 
	 * <p>If the gauge is interactive, or if it is non-interactive with
	 * definite range, the following rules apply.  If the value is less than
	 * zero, zero is used. If the current value is greater than the maximum
	 * value, the current value is set to be equal to the maximum value. </p>
	 * 
	 * <p> If this <code>Gauge</code> object is a non-interactive
	 * gauge with indefinite
	 * range, then value must be one of <code>CONTINUOUS_IDLE</code>,
	 * <code>INCREMENTAL_IDLE</code>, <code>CONTINUOUS_RUNNING</code>, or
	 * <code>INCREMENTAL_UPDATING</code>.
	 * Other values will cause an exception to be thrown.</p>
	 * 
	 * @param value the new value
	 * @throws IllegalArgumentException if value is not one of CONTINUOUS_IDLE,  INCREMENTAL_IDLE, CONTINUOUS_RUNNING, or INCREMENTAL_UPDATING for non-interactive gauges with indefinite range
	 * @see #CONTINUOUS_IDLE, #INCREMENTAL_IDLE, #CONTINUOUS_RUNNING, #INCREMENTAL_UPDATING, #getValue()
	 */
	public void setValue(int value)
	{
		if (this.isIndefinite) {
			if (this.value == CONTINUOUS_RUNNING ) {
				// when the value WAS continuous-running, remove this gauge from 
				// the animations:
				Screen scr = getScreen();
				if (scr != null) {
					scr.gauge = null;
				}
			}
			if (value == CONTINUOUS_IDLE) {
				this.indefinitePos = 0;
			} else if (value == CONTINUOUS_RUNNING){
				Screen scr = getScreen();
				if (scr != null) {
					scr.gauge = this;
				}
			} else if ( value == INCREMENTAL_IDLE ) {
				this.indefinitePos = 0;
			} else if ( value == INCREMENTAL_UPDATING ) {
				this.indefinitePos++;
				if (this.indefinitePos > this.maxValue ) {
					this.indefinitePos = 0;				
				}
			} else {
				//#ifdef polish.debugVerbose
					throw new IllegalArgumentException("Invalid value for indefinite Gauge: " + value );
				//#else
					//# throw new IllegalArgumentException();
				//#endif
			}
		} else if (value < 0  ) {
			value = 0;
		} else  if (value > this.maxValue) {
			value = this.maxValue;
		}
		this.value = value;
		this.valueString = "" + value;
		if (this.isInitialised) {
			if (this.isIndefinite) {
				updateIndefiniteIndicatorImage();
			} else {
				createIndicatorImage();
			}
		}
		//#ifdef polish.midp2
		if (this.midpGauge != null) {
			this.midpGauge.setValue( value );
		}
		//#endif	
		if (this.isInitialised) {
			repaint();
		}
	}

	/**
	 * Calculates the position of the indicator and creator the appropriate image.
	 * This method must not be called when the gauge has not yet been initialised.
	 */
	private void createIndicatorImage() {
		int percentage = (this.value * 100) / this.maxValue;
		int position = (percentage * (this.contentWidth - this.valueWidth)) / 100;
		if (position == 0) {
			position = 1;
		}
		this.indicatorImage = Image.createImage( position, this.contentHeight );
		Graphics g = this.indicatorImage.getGraphics();
		if (this.image != null) {
			int imageWidth = this.image.getWidth();
			int x = 0;
			while ( x < position ) {
				g.drawImage(this.image, x, 0, Graphics.TOP | Graphics.LEFT );
				x += imageWidth;
			}
		} else if (this.mode == MODE_CHUNKED) {
			/*
			if (this.chunkWidth == 0) {
				int spacePerChunk = this.contentWidth / this.maxValue;
				if (spacePerChunk > 5) {
					this.gapWidth = 3;
					this.chunkWidth = spacePerChunk - this.gapWidth;
				} else {
					
				}
			} */
			// paint the filling:
			g.setColor( this.color );
			g.fillRect( 0, 0, position, this.contentHeight );
			// paint the gaps:
			g.setColor( this.gapColor );
			int x = this.chunkWidth;
			while (x < position) {
				g.fillRect( x, 0, this.gapWidth, this.contentHeight );
				x += this.gapWidth + this.chunkWidth;
			}
		} else {
			// mode == CONTINUOUS
			g.setColor( this.color );
			g.fillRect( 0, 0, position, this.contentHeight );
		}
		
	}

	/**
	 * Updates the indicator image for an indefinite gauge.
	 */
	private void updateIndefiniteIndicatorImage() {
		Graphics g = this.indicatorImage.getGraphics();
		if (this.value == CONTINUOUS_IDLE || this.value == INCREMENTAL_IDLE) {
			g.setColor( this.gapColor );
			g.fillRect( 0, 0, this.contentWidth, this.contentHeight );
		} else if (this.value == CONTINUOUS_RUNNING ) {
			if (this.image != null) {
				g.setColor( this.color );
				g.fillRect( 0, 0, this.contentWidth, this.contentHeight );
				g.drawImage( this.image, this.indefinitePos, 0, Graphics.TOP | Graphics.LEFT );
			} else {
				g.setColor( this.color );
				g.fillRect( 0, 0, this.contentWidth, this.contentHeight );
				g.setColor( this.gapColor );
				int cWidth = this.chunkWidth + this.gapWidth;
				int x =  this.indefinitePos - cWidth;
				while (x < this.contentWidth) {
					g.fillRect( x, 0, this.gapWidth, this.contentHeight );
					x += cWidth;
				}
			}
		} else { // value == INCREMENTAL_UPDATE
			int percentage = (this.indefinitePos * 100) / this.maxValue;
			int position = (percentage * this.contentWidth) / 100;
			if (this.image != null) {
				g.setColor( this.color );
				g.fillRect( 0, 0, this.contentWidth, this.contentHeight );
				int imageWidth = this.image.getWidth();
				int x = 0;
				while (x < position) {
					g.drawImage( this.image, x, 0, Graphics.TOP | Graphics.LEFT );
					x += imageWidth;
				} 
			} else {
				g.setColor( this.gapColor );
				g.fillRect( 0, 0, this.contentWidth, this.contentHeight );
				g.setColor( this.color );
				int cWidth = this.chunkWidth + this.gapWidth;
				int x = 0;
				while (x < position) {
					g.fillRect( x, 0, this.chunkWidth, this.contentHeight );
					x += cWidth;
				}
			}
		}
	}

	/**
	 * Gets the current value of this <code>Gauge</code> object.
	 * 
	 * <p> If this <code>Gauge</code> object is a non-interactive
	 * gauge with indefinite
	 * range, the value returned will be one of <code>CONTINUOUS_IDLE</code>,
	 * <code>INCREMENTAL_IDLE</code>, <code>CONTINUOUS_RUNNING</code>, or
	 * <code>INCREMENTAL_UPDATING</code>.  Otherwise, it will be an integer
	 * between zero and the gauge's maximum value, inclusive.</p>
	 * 
	 * @return current value of the Gauge
	 * @see #CONTINUOUS_IDLE,  #INCREMENTAL_IDLE, #CONTINUOUS_RUNNING, #INCREMENTAL_UPDATING, #setValue(int)
	 */
	public int getValue()
	{
		return this.value;
	}

	/**
	 * Sets the maximum value of this <code>Gauge</code> object.
	 * 
	 * <p>For interactive gauges, the new maximum value must be greater than
	 * zero, otherwise an exception is thrown.  For non-interactive gauges,
	 * the new maximum value must be greater than zero or equal to the special
	 * value <code>INDEFINITE</code>, otherwise an exception is thrown.  </p>
	 * 
	 * <p>If the new maximum value is greater than zero, this provides the
	 * gauge with a definite range.  If the gauge previously had a definite
	 * range, and if the current value is greater than new maximum value, the
	 * current value is set to be equal to the new maximum value.  If the
	 * gauge previously had a definite range, and if the current value is less
	 * than or equal to the new maximum value, the current value is left
	 * unchanged. </p>
	 * 
	 * <p>If the new maximum value is greater than zero, and if the gauge had
	 * previously had indefinite range, this new maximum value provides it
	 * with a definite range.  Its graphical representation must change
	 * accordingly, the previous state of <code>CONTINUOUS_IDLE</code>,
	 * <code>INCREMENTAL_IDLE</code>, <code>CONTINUOUS_RUNNING</code>, or
	 * <code>INCREMENTAL_UPDATING</code> is ignored, and the current value
	 * is set to zero. </p>
	 * 
	 * <p>If this gauge is non-interactive and the new maximum value is
	 * <code>INDEFINITE</code>, this gives the gauge indefinite range.
	 * If the gauge
	 * previously had a definite range, its graphical representation must
	 * change accordingly, the previous value is ignored, and the current
	 * state is set to <code>CONTINUOUS_IDLE</code>.  If the gauge previously
	 * had an indefinite range, setting the maximum value to
	 * <code>INDEFINITE</code> will have no effect. </p>
	 * 
	 * @param maxValue the new maximum value
	 * @throws IllegalArgumentException if maxValue is invalid
	 * @see #INDEFINITE,  #getMaxValue()
	 */
	public void setMaxValue(int maxValue)
	{
		this.isInitialised = false;
		if (maxValue == INDEFINITE) {
			this.isIndefinite = true;
			//#ifndef polish.skipArgumentCheck
				} else if (maxValue < 0) {
					//#ifdef polish.verboseDebug
						throw new IllegalArgumentException("Invalid maxValue for Gauge: " + maxValue );
					//#else
						//# throw new IllegalArgumentException();
					//#endif
			//#endif
		}
		this.maxValue = maxValue;
		//#ifdef polish.midp2
		if (this.midpGauge != null) {
			this.midpGauge.setMaxValue( maxValue );
		}
		//#endif		
		this.isInitialised = false;
	}

	/**
	 * Gets the maximum value of this <code>Gauge</code> object.
	 * 
	 * <p>If this gauge is interactive, the maximum value will be a positive
	 * integer.  If this gauge is non-interactive, the maximum value will be a
	 * positive integer (indicating that the gauge has definite range)
	 * or the special value <code>INDEFINITE</code> (indicating that
	 * the gauge has indefinite range).</p>
	 * 
	 * @return the maximum value of the Gauge, or INDEFINITE
	 * @see #INDEFINITE, #setMaxValue(int)
	 */
	public int getMaxValue()
	{
		return this.maxValue;
	}

	/**
	 * Tells whether the user is allowed to change the value of the
	 * <code>Gauge</code>.
	 * 
	 * @return a boolean indicating whether the Gauge is interactive
	 */
	public boolean isInteractive()
	{
		return this.isInteractive;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#paint(int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		if (this.showValue && this.isValueLeft) {
			g.setFont( this.font );
			g.setColor( this.fontColor );
			g.drawString( this.valueString, x, y, Graphics.TOP | Graphics.LEFT );
			x += this.valueWidth;
		}
		g.drawImage(this.indicatorImage, x, y, Graphics.TOP | Graphics.LEFT );
		if (this.showValue && !this.isValueLeft) {
			g.setFont( this.font );
			g.setColor( this.fontColor );
			g.drawString( this.valueString, rightBorder, y, Graphics.TOP | Graphics.RIGHT );
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#initItem()
	 */
	protected void initContent(int firstLineWidth, int lineWidth) {
		this.valueWidth = 0;
		int valueHeight = 0;
		if (this.isIndefinite) {
			this.showValue = false;
		}
		if ( this.showValue) {
			if (this.font == null) {
				this.font = Font.getDefaultFont();
			}
			valueHeight = this.font.getHeight();
			this.valueWidth = this.font.stringWidth( "" + this.maxValue ) + this.paddingHorizontal;
		}
		// setting height:
		if (this.image != null) {
			this.contentHeight = this.image.getHeight();
		} else if (this.preferredHeight > 0 ) {
			this.contentHeight = this.preferredHeight;
		} else {
			this.contentHeight = 10;
		}
		if (this.contentHeight < valueHeight) {
			this.contentHeight = valueHeight;
		}
		// setting width:
		if (this.image != null 
				&& !this.isIndefinite 
				&& this.preferredWidth == 0 ) {
			this.contentWidth = this.image.getWidth() + this.valueWidth;
		} else if (this.preferredWidth > 0) {
			this.contentWidth = this.preferredWidth + this.valueWidth;
		} else { //if (this.isLayoutExpand) {
			this.contentWidth = firstLineWidth;
		}
		
		// update other settings:
		if (this.isIndefinite) {
			if (this.value == CONTINUOUS_RUNNING) {
				Screen scr = getScreen();
				if (scr != null) {
					// register this gauge at the current screen:
					scr.gauge = this;
				} else {
					System.out.println("unable to register gauge");
				}
			}
			if (this.image != null ) {
				if (this.value == CONTINUOUS_IDLE  || this.value == CONTINUOUS_RUNNING ) {
					this.maxValue = this.contentWidth;
				} else {
					this.maxValue = this.contentWidth / this.image.getWidth();
				}
			} else {
				this.maxValue = 20;
			}
			this.indicatorImage = Image.createImage( this.contentWidth - this.valueWidth, this.contentHeight );
			updateIndefiniteIndicatorImage();
		} else { // this is a definite gauge
			createIndicatorImage();
		}
	}

	//#ifdef polish.useDynamicStyles
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#getCssSelector()
	 */
	protected String createCssSelector() {
		return "gauge";
	}
	//#endif
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		super.setStyle(style);
		String colorStr = style.getProperty("gauge-color");
		if (colorStr != null) {
			this.color = Integer.parseInt( colorStr );
		}
		String widthStr = style.getProperty( "gauge-width");
		if (widthStr != null) {
			this.preferredWidth = Integer.parseInt( widthStr );
		}
		String heightStr = style.getProperty( "gauge-height");
		if (heightStr != null) {
			this.preferredHeight = Integer.parseInt( heightStr );
		}
		String modeStr = style.getProperty( "gauge-mode");
		if (modeStr != null) {
			if ("continuous".equals(modeStr)) {
				this.mode = MODE_CONTINUOUS;
			} else {
				this.mode = MODE_CHUNKED;
			}
		}
		String gapColorStr = style.getProperty( "gauge-gap-color");
		if (gapColorStr != null) {
			this.gapColor = Integer.parseInt( gapColorStr );
		}
		String gapWidthStr = style.getProperty( "gauge-gap-width");
		if (gapWidthStr != null) {
			this.gapWidth = Integer.parseInt( gapWidthStr );
		}
		String chunkWidthStr = style.getProperty( "gauge-chunk-width");
		if (chunkWidthStr != null) {
			this.chunkWidth = Integer.parseInt( chunkWidthStr );
		}
		String imageStr = style.getProperty( "gauge-image");
		if (imageStr != null) {
			try {
				this.image = StyleSheet.getImage( imageStr, this, false );
			} catch (IOException e) {
				//#debug error
				Debug.debug("unable to load gauge-image [" + imageStr + "]: " + e.getMessage(), e );
			}
		}
		if (this.maxValue != INDEFINITE) {
			String showValueStr = style.getProperty("gauge-show-value");
			if (showValueStr != null) {
				if ("true".equals(showValueStr)) {
					this.showValue = true;
				} else {
					this.showValue = false;					
				}
			}
			if (style.font != null) {
				this.font = style.font;
			}
			this.fontColor = style.fontColor;
			String valuePositionStr =  style.getProperty( "gauge-value-align" );
			if (valuePositionStr != null) {
				if ("right".equals(valuePositionStr)) {
					this.isValueLeft = false;
				} else {
					this.isValueLeft = true;
				}
			}
		}
	}

	//#ifdef polish.images.backgroundLoad
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ImageConsumer#setImage(java.lang.String, javax.microedition.lcdui.Image)
	 */
	public void setImage(String name, Image image) {
		this.image = image;
		this.isInitialised = false;
		repaint();
	}
	//#endif
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handleKeyPressed(int, int)
	 */
	protected boolean handleKeyPressed(int keyCode, int gameAction) {
		if (!this.isInteractive) {
			return false;
		}
		if (gameAction == Canvas.RIGHT) {
			if (this.value < this.maxValue) {
				setValue( ++ this.value );
				notifyStateChanged();
				return true;
			} else {
				return false;
			}			
		} else if (gameAction == Canvas.LEFT) {
			if (this.value > 0) {
				setValue( -- this.value );
				notifyStateChanged();
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#animate()
	 */
	public boolean animate() {
		if (this.isIndefinite && this.value == CONTINUOUS_RUNNING && this.isInitialised) {
			this.indefinitePos++;
			if (this.image == null) {
				if (this.indefinitePos > (this.chunkWidth + this.gapWidth)) {
					this.indefinitePos = 0;
				}
			} else if (this.indefinitePos > this.maxValue) {
				this.indefinitePos = 0;
			}
			updateIndefiniteIndicatorImage();
			return true;
		}
		return false;
	}
}
