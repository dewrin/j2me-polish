/*
 * Created on May 31, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.izforge.izpack.panels;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


import com.izforge.izpack.installer.InstallData;
import com.izforge.izpack.installer.InstallerFrame;
import com.izforge.izpack.installer.IzPanel;
import com.izforge.izpack.util.MultiLineLabel;

/**
 * @author robertvirkus
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ChooseLicensePanel extends IzPanel 
implements KeyListener, ChangeListener, DocumentListener {
	
	private final static int GPL = 1;
	private final static int EVALUATION = 2;
	private final static int COMMERCIAL = 3;
	private final static int KEY_LENGTH = 12;
	
	ButtonGroup radioBox;
	JRadioButton gplButton;
	JRadioButton evaluationButton;
	JRadioButton commercialButton;
	JTextField licenseField;
	private int selectedLicense = -1;

	/**
	 * @param parent
	 * @param idata
	 */
	public ChooseLicensePanel(InstallerFrame parent, InstallData idata) {
		super(parent, idata);
	
		
		// The layout
	    GridBagLayout layout = new GridBagLayout();
	    setLayout(layout);
	    GridBagConstraints constraints = new GridBagConstraints();
	    constraints.insets = new Insets(0, 0, 0, 0);
	    constraints.fill = GridBagConstraints.VERTICAL;
	    constraints.anchor = GridBagConstraints.SOUTHWEST;
	    
	    JLabel title = new JLabel("License Selection");
	    title.setFont( title.getFont().deriveFont( title.getFont().getSize() * 2F ));
	    constraints.gridwidth = 3;
	    constraints.gridx = 0;
	    constraints.gridy = 0;
	    layout.setConstraints( title, constraints );
	    add( title );
	    
	    
	    JLabel chooseType = new JLabel("Please choose the license for using J2ME Polish:");
	    constraints.gridwidth = 3;
	    constraints.gridx = 0;
	    constraints.gridy = 1;
	    layout.setConstraints( chooseType, constraints );
	    add( chooseType );
	    
	    chooseType = new JLabel("Type:");
	    constraints.gridwidth = 1;
	    constraints.gridx = 0;
	    constraints.gridy = 2;
	    layout.setConstraints( chooseType, constraints );
	    add( chooseType );
	    
	    
	    this.radioBox = new ButtonGroup();
	    JRadioButton radioButton = new JRadioButton("GPL");
	    radioBox.add( radioButton );
	    constraints.gridy = 2;
	    constraints.gridx = 1;
	    constraints.gridwidth = 2;
	    layout.setConstraints( radioButton, constraints );
	    add( radioButton );
	    radioButton.addChangeListener( this );
	    this.gplButton = radioButton;
	    
	    radioButton = new JRadioButton("Evaluation");
	    radioBox.add( radioButton );
	    constraints.gridy = 3;
	    layout.setConstraints( radioButton, constraints );
	    add( radioButton );
	    radioButton.addChangeListener( this );
	    this.evaluationButton = radioButton;
	    
	    radioButton = new JRadioButton("Commercial / None-GPL");
	    radioBox.add( radioButton );
	    constraints.gridy = 4;
	    layout.setConstraints( radioButton, constraints );
	    add( radioButton );
	    radioButton.addChangeListener( this );
	    this.commercialButton = radioButton;
	    
	    MultiLineLabel licenseLabel = new MultiLineLabel( "If you use J2ME Polish commercially, please enter your license key here:" );
	    constraints.gridy = 5;
	    constraints.gridx = 0;
	    constraints.gridwidth = 3;
	    constraints.gridheight = 2;
	    layout.setConstraints( licenseLabel, constraints );
	    add( licenseLabel );
	    
	    JLabel keyLabel = new JLabel("License-key: ");
	    constraints.gridwidth = 1;
	    constraints.gridx = 0;
	    constraints.gridy = 7;
	    layout.setConstraints( keyLabel, constraints );
	    add( keyLabel );
	    
	    
	    this.licenseField = new JTextField( 12 );
	    this.licenseField.getDocument().addDocumentListener( this );
	    constraints.gridy = 7;
	    constraints.gridx = 1;
	    constraints.gridwidth = 2;
	    constraints.gridheight = 1;
	    layout.setConstraints( licenseField, constraints );
		add( this.licenseField );
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent event) {
		if (this.commercialButton.isSelected()) {
			String text = this.licenseField.getText();
			if (text != null && text.length() == KEY_LENGTH) {
				this.parent.unlockNextButton();
			} else {
				this.parent.lockNextButton();
			}
			this.licenseField.requestFocus();
		} else {
			this.commercialButton.setSelected( true );
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent event) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent event) {
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent event) {
		Object source = event.getSource();
		if (source == this.commercialButton ) {
			if (this.commercialButton.isSelected()) {
				this.selectedLicense = COMMERCIAL;
				String text = this.licenseField.getText();
				if (text != null && text.length() == KEY_LENGTH) {
					this.parent.unlockNextButton();
				} else {
					this.parent.lockNextButton();
					this.licenseField.requestFocus();
				}
			}
		} else if ( source == this.gplButton) {
			if (this.gplButton.isSelected()) {
				this.selectedLicense = GPL;
				this.parent.unlockNextButton();
			}
		} else if ( source == this.evaluationButton) {
			if (this.evaluationButton.isSelected()) {
				this.selectedLicense = EVALUATION;
				this.parent.unlockNextButton();
			}
		}
	}

	  /**
	   *  Indicates wether the panel has been validated or not.
	   *
	   * @return    true if the user has agreed.
	   */
	  public boolean isValidated()
	  {
	  	String licenseText = "GPL";
	  	boolean isValidated = (this.selectedLicense != -1 );
	  	if (this.selectedLicense == COMMERCIAL ) {
	  		licenseText = this.licenseField.getText();
	  		isValidated = ( licenseText != null && licenseText.length() == KEY_LENGTH ) ;
	  	}
	
		if ( isValidated ) {
			// now set the license variable:
			this.idata.setVariable( "J2ME_POLISH_LICENSE", licenseText );
			return true;
		} else {
			this.parent.lockNextButton();
			return false;
		}
	  }

	  /**  Called when the panel becomes active.  */
	  public void panelActivate()
	  {
		String text = this.licenseField.getText();
		if ( (text != null && text.length() == KEY_LENGTH ) 
				|| ((this.selectedLicense != -1)) && (this.selectedLicense != COMMERCIAL )) {
			this.parent.unlockNextButton();
		} else {
			this.parent.lockNextButton();
		}
	  }

	/* (non-Javadoc)
	 * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
	 */
	public void insertUpdate(DocumentEvent event) {
		changedUpdate( event );
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
	 */
	public void removeUpdate(DocumentEvent event) {
		changedUpdate( event );
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
	 */
	public void changedUpdate(DocumentEvent event) {
		if (!this.commercialButton.isSelected()) {
			this.commercialButton.setSelected( true );
		}
		String text = this.licenseField.getText();
		if (text != null && text.length() == KEY_LENGTH) {
			this.parent.unlockNextButton();
		} else {
			this.parent.lockNextButton();
		}
		this.licenseField.requestFocus();
	}
}
