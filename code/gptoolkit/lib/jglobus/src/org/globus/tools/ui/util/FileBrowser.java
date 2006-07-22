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
package org.globus.tools.ui.util;

import java.io.File;
import java.awt.Insets;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JFileChooser;

public class FileBrowser extends JPanel implements ActionListener {

    private JTextField file;
    private JButton browser;
    private FileFilter fileFilter;

    boolean specialfilter = false;
    private String title, okLabel;
    

    public FileBrowser(String title, String label, String okLabel) {
	
	this.title = title;
	this.okLabel = okLabel;
	
	file = new JTextField(30);
	browser = new JButton("...");
	browser.addActionListener(this);
	
	setLayout(new BorderLayout(5, 1));
	add("North", new JLabel(label));
	add("Center", file);
	add("East", browser);
    }

  public FileBrowser(String title, String okLabel) {
    this.title = title;
    this.okLabel = okLabel;
    
    file = new JTextField(30);
    browser = new JButton("...");
    browser.addActionListener(this);
    
    setLayout(new BorderLayout(5, 1));
    add("Center", file);
    add("East", browser);
  }
  
    public Insets getInsets() {
	return new Insets(5, 2, 5, 2);
    }

    public void enableSpecialFilter() {
	specialfilter = true;
    }

  private File getSelectedFile() {
    JFileChooser filechooser = new JFileChooser();

    if (fileFilter != null) {
	filechooser.addChoosableFileFilter( fileFilter );
	filechooser.setFileFilter( fileFilter );
    }

    File ff  = null;
    String tt = file.getText();
    if (tt.trim().length() == 0) {
	ff = new File(".");
    } else {
	ff = new File( file.getText() );
    }

    filechooser.setCurrentDirectory( ff );
    filechooser.setSelectedFile( ff );

    filechooser.setApproveButtonText(okLabel);
    filechooser.setDialogTitle(title);
   
    if(filechooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      File f = filechooser.getSelectedFile();
      return f;
    } 
    return null;
  }

  public void actionPerformed(ActionEvent e) {
    File f = getSelectedFile();
    if (f != null)
      file.setText( f.getAbsolutePath() );
  }

  public String getFile() {
    return file.getText().trim();
  }

    public void setFileFilter(FileFilter filter) {
	fileFilter = filter;
    }

  public void setFile(String f) {
    file.setText(f);
  }

}
