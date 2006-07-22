/**
 * This file is subject to the GNU General Public License (GPL)
 * (http://www.gnu.org/copyleft/gpl.html)
 */

package org.ntnu.utility;


import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;

import java.util.*;


/**
 * An abstract class which defines the framework for most of the panels
 * in the application. The GuiPanelGeneric is designed to be used as a thread
 * dispatcher as well as a normal gui class.
 *
 * @author Martin Matusiak
 */
abstract class GuiPanelGeneric extends JPanel {

	protected ThreadListener threadlistener = new ThreadListener();

	protected final static int textFieldWidth = 17;

	protected Frame parent;
	protected GuiPanelStatusBar statusbar;
	
	private ArrayList windows;


	public GuiPanelGeneric(Frame parent, ArrayList windows) {

		this.parent = parent;
		this.windows = windows;

	}
	

/**
 * Initiating the status bar of the panel.
 */
	public void setStatusBar(GuiPanelStatusBar statusbar) {
	
		// Register status bar
		this.statusbar = statusbar;
		statusbar.addStatus("Idle");
	}


/*	Starting threads, a common interface
		preThread() to do class specific things before call to startThread()
		startThread() to prepare thread execution and start the process
		postThread() to handle post-thread stuff in the gui class
		cleanupThread() to handle clean up
*/

/**
 * The method which prepares the thread for launch, assumed to receive 
 * a password in most cases. This method will set any parameters specific 
 * to this panel or this thread and then call startThread().
 */
	public void preThread(char[] password) {}
	

/**
 * The method does initiating common to all panels and then launches 
 * the thread. The priority is set to be low so that the gui stays
 * responsive. This method also initiates the TimerTaskUpdateStatusBar
 * object, which is set to update the gui while the thread is running.
 */
	public void startThread(Runnable runnable, String startMsg) {

		// Generate certificate
		Thread thread = new Thread(runnable, runnable.getClass().getName());

		// keep gui responsive
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
		
		// Print status of thread process
		statusbar.addMessage("+++++++ " + startMsg + " +++++++\n");
		java.util.Timer timer = new java.util.Timer();
		timer.scheduleAtFixedRate((new
			TimerTaskUpdateStatusBar(timer, thread, this, statusbar)), 0, 500);

	}
	

/**
 * Panel/thread specific post-processing goes in here. Usually this is
 * reading the exit status of the thread and doing error processing.
 */
	public void postThread() {}
	
	
/**
 * Common post-thread operations go in here.
 */	
	public void cleanupThread() {
			
		// Reset for next run of the thread
		threadlistener.reset();
			
	}
	

}


/**
 * This class is a TimerTask which takes care of updating the gui
 * while a thread is running.
 *
 * @author Martin Matusiak
 */
class TimerTaskUpdateStatusBar extends TimerTask {

	private java.util.Timer timer;
	private Thread thread;
	private GuiPanelGeneric parent;
	private GuiPanelStatusBar statusbar;


	public TimerTaskUpdateStatusBar(java.util.Timer timer, Thread thread, 
		GuiPanelGeneric parent, GuiPanelStatusBar statusbar) {
		this.timer = timer;
		this.thread = thread;
		this.parent = parent;
		this.statusbar = statusbar;
	}


/**
 * This method is invoked every fixed interval and updates the gui if the
 * thread is still running. Otherwise, it calls the post-thread methods of 
 * GuiPanelGeneric.
 */
	public void run() {
		if (thread.isAlive()) {
			statusbar.addProcessIndicator(null);
		} else {
			timer.cancel();
			
			// turn over control to original class
			parent.postThread();
			
			// run clean up routine
			parent.cleanupThread();
		}
	}

}


/**
 * This class is used to receive error messages from the running thread.
 *
 * @author Martin Matusiak
 */
class ThreadListener {

	private boolean error = false;
	private String trace = "";
	
	
	public void reportFatalError(Exception ex) {
		
		// Nullify string from last error
		trace = "";
		
		// Report the error
		trace += ex.getMessage();
		
		trace += "\n" + ex.getClass().getName() + " at";
		
		// Print stack trace
		StackTraceElement[] stacktrace = ex.getStackTrace();
		for (int i=0; i < stacktrace.length; i++) {
			trace += "\n" + stacktrace[i].toString();
		}
		error = true;
		
	}
	
	
	public boolean hasError() {
		return error;
	}
	
	
	public String getError() {
		return trace;
	}
	
	
	public void reset() {
		error = false;
	}

}
