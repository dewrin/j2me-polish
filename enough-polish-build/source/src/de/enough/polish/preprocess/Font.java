/*
 * Created on 01-Mar-2004 at 15:27:17.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.preprocess;

import org.apache.tools.ant.BuildException;

/**
 * <p>Respresents a J2ME font.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        01-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class Font extends StyleComponent {
	
	public final static String STYLE_PLAIN = "plain";
	public final static String STYLE_BOLD = "bold";
	public final static String STYLE_ITALIC = "italic";
	public final static String STYLE_UNDERLINED = "underlined";
	
	public final static String SIZE_SMALL = "small";
	public final static String SIZE_MEDIUM = "medium";
	public final static String SIZE_LARGE = "large";
	
	public final static String FACE_SYSTEM = "system";
	public final static String FACE_MONOSPACE = "monospace";
	public final static String FACE_PROPORTIONAL = "proportional";
	
	// faces, styles and sizes could also reside in a hashmap for
	// faster retrieval, e.g. 
	// this.size = SIZES.get( sizeName );
	// if (this.size == null) { throw BuildException() }
	
	private String face = FACE_SYSTEM;
	private String style = STYLE_PLAIN;
	private String size = SIZE_MEDIUM;
	private String color;
	
	/**
	 * Creates a new font definition.
	 * 
	 * @param name the variable name of this font.
	 */
	public Font( String name ) {
		super( name );
	}
	


	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.StyleComponent#getSourceCode()
	 */
	public String[] getSourceCode() {
		StringBuffer fontCode = new StringBuffer();
		if ( this.name != null ) {
			fontCode.append("public Font ")
					.append( this.name )
					.append( " = ");
		}
		
		// first param is the font face:
		fontCode.append( "Font.getFont( Font.");
		if (  this.face == FACE_SYSTEM ) {
			fontCode.append( "FACE_SYSTEM" );
		} else if (this.face == FACE_MONOSPACE ) {
			fontCode.append( "FACE_MONOSPACE" );
		} else {
			fontCode.append( "FACE_PROPORTIONAL" );
		}
		
		// second param is the style:
		fontCode.append(", Font.");
		if (this.style == STYLE_PLAIN) {
			fontCode.append("STYLE_PLAIN");
		} else if (this.style == STYLE_BOLD) {
			fontCode.append("STYLE_BOLD");
		} else if (this.style == STYLE_ITALIC) {
			fontCode.append("STYLE_ITALIC");
		} else {
			fontCode.append("STYLE_UNDERLINED");
		}
		
		// third param is the size:
		fontCode.append(", Font.");
		if (this.size == SIZE_SMALL ) {
			fontCode.append("SIZE_SMALL");
		} else if (this.size == SIZE_MEDIUM ) {
			fontCode.append("SIZE_MEDIUM");
		} else {
			fontCode.append("SIZE_LARGE");
		}
		fontCode.append(" )");
		
		//TODO think about adding the color
		
		return new String[]{ fontCode.toString() }; 
	}
	
	/**
	 * @return Returns the color.
	 */
	public String getColor() {
		return this.color;
	}
	/**
	 * @param color The color to set.
	 */
	public void setColor(String color) {
		this.color = color;
	}
	/**
	 * @return Returns the face.
	 */
	public String getFace() {
		return this.face;
	}
	
	/**
	 * @param face The face to set.
	 */
	public void setFace(String face) {
		face = face.toLowerCase();
		if (face.equals( FACE_SYSTEM)) {
			this.face = FACE_SYSTEM;
		} else if (face.equals(FACE_MONOSPACE)) {
			this.face = FACE_MONOSPACE;
		} else if (face.equals(FACE_PROPORTIONAL) ) {
			this.face = FACE_PROPORTIONAL;
		} else {
			throw new BuildException("Invalid CSS: The font-face [" + face 
					+" ] is not known. Use either [" + FACE_SYSTEM + "], [" 
					+ FACE_PROPORTIONAL + "] or [" + FACE_MONOSPACE + "].");
		}
		this.face = face;
	}
	/**
	 * @return Returns the size.
	 */
	public String getSize() {
		return this.size;
	}
	/**
	 * @param size The size to set.
	 */
	public void setSize(String size) {
		size = size.toLowerCase();
		if (size.equals(SIZE_SMALL)) {
			this.size =  SIZE_SMALL;
		} else if (size.equals(SIZE_MEDIUM)) {
			this.size = SIZE_MEDIUM;
		} else if (size.equals(SIZE_LARGE)) {
			this.size = SIZE_LARGE;
		} else {
			throw new BuildException("Invalid CSS: The font-size [" + size 
					+ "] is not known. Either use [" + SIZE_SMALL + "], [" 
					+ SIZE_MEDIUM + "] or [" + SIZE_LARGE + "]." );
		}
	}
	/**
	 * @return Returns the style.
	 */
	public String getStyle() {
		return this.style;
	}
	/**
	 * @param style The style to set.
	 */
	public void setStyle(String style) {
		style = style.toLowerCase();
		if (style.equals(STYLE_PLAIN)) {
			this.style = STYLE_PLAIN;
		} else if (style.equals(STYLE_BOLD)) {
			this.style = STYLE_BOLD;
		} else if (style.equals(STYLE_ITALIC)) {
			this.style = STYLE_ITALIC;
		} else if (style.equals(STYLE_UNDERLINED)) {
			this.style = STYLE_UNDERLINED;
		} else {
			throw new BuildException("Invalid CSS: The font-style [" + style 
					+ "] is not known. Use either [" + STYLE_PLAIN + "], [" + STYLE_BOLD
					+ "], [" + STYLE_ITALIC + "] or [" + STYLE_UNDERLINED + "].");
		}
	}
}
