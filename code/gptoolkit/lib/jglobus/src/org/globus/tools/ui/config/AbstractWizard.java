/*
 * Portions of this file Copyright 1999-2005 University of Chicago
 * Portions of this file Copyright 1999-2005 The University of Southern California.
 *
 * This file or a portion of this file is licensed under the
 * terms of the Globus Toolkit Public License, found at
 * http://www.globus.org/toolkit/download/license.html.
 * If you redistribute this file, with or without
 * modifications, you must include this notice in the file.
 */
package org.globus.tools.ui.config;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/** This class forms the base from which configuration wizards can be built.
 *
 */
public abstract class AbstractWizard extends JFrame implements ActionListener {

    protected int moduleCount = 0;
    protected JPanel modulePanel = new JPanel();
    protected int currentModuleNumber = 0;

    private JButton backButton = new JButton( "<< Back" );
    private JButton nextButton = new JButton( "Next >>" );
    private JButton finishButton = new JButton( "Finish" );

    public AbstractWizard() {
    	initComponents();
    }

    private void initComponents() {
	this.addWindowListener( new java.awt.event.WindowAdapter() {
		public void windowClosing( java.awt.event.WindowEvent we ) {
		    System.exit(0);
		}
	} );

    	java.awt.Container contentPane = this.getContentPane();

	//Setup the empty module panel
	modulePanel.setLayout( new CardLayout() );
	contentPane.add( modulePanel );


	//Setup the button panel
    	JPanel buttonPanel = new JPanel();

	JButton cancelButton = new JButton( "Cancel" );
	cancelButton.addActionListener( this );
	buttonPanel.add( cancelButton );

	backButton.addActionListener( this );
	backButton.setEnabled( false );
	buttonPanel.add( backButton );

	nextButton.addActionListener( this );
	nextButton.setEnabled( false );
	buttonPanel.add( nextButton );

	finishButton.addActionListener( this );
	finishButton.setEnabled( false );
	buttonPanel.add( finishButton );

	contentPane.add( buttonPanel, "South" );
    }

    protected void addModule( ConfigurationModule module ) {
	moduleCount++;
	modulePanel.add( (Component)module, "Step " + new Integer( moduleCount ) );
	if( moduleCount == 1 ) {
	    finishButton.setEnabled( true );
	} else if( moduleCount > 1 ) {
	    nextButton.setEnabled( true );
	    finishButton.setEnabled( false );
	}
    }

    public abstract void saveSettings() ;

    /*
    private void saveSettings() {
    	int componentCount = modulePanel.getComponentCount();
	for( int i=0; i<componentCount; i++ ) {
	    ((ConfigurationModule)modulePanel.getComponent( i )).saveSettings();
	}
    }
    */

    private boolean verifyAndSaveCurrent() {
	ConfigurationModule m = 
	    (ConfigurationModule)modulePanel.getComponent(currentModuleNumber);
	boolean rs = m.verifySettings();
	if (!rs) return false;
	m.saveSettings();
	return true;
    }

    public void actionPerformed( ActionEvent ae ) {
	Object source = ae.getSource();
	String actionCommand = ae.getActionCommand();
	if( actionCommand.equals( "Cancel" ) ) {
	    System.exit(0);
	} else if( actionCommand.equals( "<< Back" ) ) {
	    if( ((JButton) source).isEnabled() ) {
	    	((CardLayout)modulePanel.getLayout()).previous( modulePanel );
		currentModuleNumber--;
		if( currentModuleNumber == 0 ) {
		    backButton.setEnabled( false );
		}
		if( currentModuleNumber < (moduleCount -1) ) {
		    nextButton.setEnabled( true );
		    finishButton.setEnabled( false );
		}
	    }
	} else if( actionCommand.equals( "Next >>" ) ) {
	    if( ((JButton) source).isEnabled() ) {
		if (!verifyAndSaveCurrent()) return;
		((CardLayout)modulePanel.getLayout()).next( modulePanel );
		currentModuleNumber++;
		if( currentModuleNumber == (moduleCount - 1) ) {
		    nextButton.setEnabled( false );
		    finishButton.setEnabled( true );
		}
		if( currentModuleNumber > 0 ) {
		    backButton.setEnabled( true );
		}
	    }
	} else if( actionCommand.equals( "Finish" ) ) {
	    if (!verifyAndSaveCurrent()) return;
	    saveSettings();
	}
    }
}
