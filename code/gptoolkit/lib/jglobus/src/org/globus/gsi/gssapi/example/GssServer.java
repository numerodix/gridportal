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
package org.globus.gsi.gssapi.example;

import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.globus.gsi.gssapi.SSLUtil;
import org.globus.gsi.gssapi.GSSConstants;
import org.globus.gsi.gssapi.GlobusGSSManagerImpl;
import org.globus.gsi.GSIConstants;

import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSCredential;

import org.gridforum.jgss.ExtendedGSSContext;

import java.io.OutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.net.ServerSocket;

public class GssServer {

    private static final String helpMsg = 
	"Where options are:\n" +
	" -gss-mode mode\t\t\tmode is: 'ssl' or 'gsi' (default)\n" +
	" -deleg-type type\t\ttype is: 'none', 'limited' (default), or 'full'\n" +
	" -lifetime time\t\t\tLifetime of context. time is in seconds.\n" +
	" -rejectLimitedProxy\t\tEnables checking for limited proxies. By default off\n" +
	" -anonymous\t\t\tDo not require client authentication\n" +
	" -enable-conf\t\t\tEnables confidentiality (do encryption) (enabled by default)\n" +
	" -disable-conf\t\t\tDisables confidentiality (no encryption)";

    public static void main(String [] args) {

	String usage = "Usage: java GssServer [options] [port]";

	GetOpts opts = new GetOpts(usage, helpMsg);

	int pos = opts.parse(args);
	int port = 0;

	if (pos < args.length) {
	    port = Integer.parseInt(args[pos]);
	}

	try {
	    ServerSocket server = new ServerSocket(port);
	    System.out.println("Server running at: " + server.getLocalPort());
	    while(true) {
		Client c = new Client(server.accept(), opts);
		c.start();
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	} 
    }

}

class Client extends Thread {

    GetOpts opts;
    Socket s;
    
    public Client(Socket s, GetOpts opts) {
	this.s = s;
	this.opts = opts;
    }

    public void run() {

	System.out.println("client connected");
	
	// to make sure we use right impl
	GSSManager manager = new GlobusGSSManagerImpl();
	ExtendedGSSContext context = null;
	
	try {
	    OutputStream out = s.getOutputStream();
	    InputStream in = s.getInputStream();
	    
	    byte [] inToken = null;
	    byte [] outToken = null;

	    context = (ExtendedGSSContext)manager.createContext((GSSCredential)null);
	    
	    context.requestConf(opts.conf);
	    
	    context.setOption(GSSConstants.GSS_MODE,
			      (opts.gsiMode) ? 
			      GSIConstants.MODE_GSI : 
			      GSIConstants.MODE_SSL);
	    
	    context.setOption(GSSConstants.REJECT_LIMITED_PROXY,
			      new Boolean(opts.rejectLimitedProxy));
	    
	    context.setOption(GSSConstants.REQUIRE_CLIENT_AUTH,
			      new Boolean(!opts.anonymity));
	    
	    // Loop while there still is a token to be processed
	    while (!context.isEstablished()) {
		inToken = SSLUtil.readSslMessage(in);
		
		outToken 
		    = context.acceptSecContext(inToken, 0, inToken.length);
		
		if (outToken != null) {
		    out.write(outToken);
		    out.flush();
		}
	    }
	    
	    System.out.println("Context established.");
	    System.out.println("Initiator : " + context.getSrcName());
	    System.out.println("Acceptor  : " + context.getTargName());	    
	    System.out.println("Lifetime  : " + context.getLifetime());
	    System.out.println("Privacy   : " + context.getConfState());
	    
	    GlobusGSSCredentialImpl cred = 
		(GlobusGSSCredentialImpl)context.getDelegCred();
	    System.out.println("Delegated credential :");
	    if (cred != null) {
		System.out.println(cred.getGlobusCredential());
	    } else {
		System.out.println("None");
	    }
	    
	    inToken = SSLUtil.readSslMessage(in);
	    
	    outToken = context.unwrap(inToken, 0, inToken.length, null);
	    
	    System.out.println(new String(outToken));
	    
	    byte[] msg = 
		"HTTP/1.1 404 Not Found\r\nConnection: close\r\n\r\n".getBytes();
	    
	    outToken = context.wrap(msg, 0, msg.length, null);
	    
	    out.write(outToken);
	    out.flush();
	    
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    try { s.close(); } catch(Exception e) {}
	    try {
		context.dispose();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    System.out.println("client disconnected");
	}
    }
}
