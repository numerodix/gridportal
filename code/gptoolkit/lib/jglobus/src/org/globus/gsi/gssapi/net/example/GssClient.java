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
package org.globus.gsi.gssapi.net.example;

import org.globus.gsi.GSIConstants;
import org.globus.gsi.gssapi.GSSConstants;
import org.globus.gsi.gssapi.GlobusGSSManagerImpl;
import org.globus.gsi.gssapi.net.GssSocketFactory;
import org.globus.gsi.gssapi.net.GssSocket;
import org.globus.gsi.gssapi.auth.Authorization;
import org.globus.gsi.gssapi.auth.GSSAuthorization;
import org.globus.gsi.gssapi.auth.SelfAuthorization;
import org.globus.gsi.gssapi.auth.HostAuthorization;
import org.globus.gsi.gssapi.auth.IdentityAuthorization;

import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;

import org.gridforum.jgss.ExtendedGSSContext;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class GssClient {

    private static final String helpMsg = 
	"Where options are:\n" +
	" -gss-mode mode\t\t\tmode is: 'ssl' or 'gsi' (default)\n" +
	" -deleg-type type\t\ttype is: 'none', 'limited' (default), or 'full'\n" +
	" -lifetime time\t\t\tLifetime of context. time is in seconds.\n" +
	" -rejectLimitedProxy\t\tEnables checking for limited proxies (off by default)\n" +
	" -anonymous\t\t\tDo not send certificates to the server\n" +
	" -enable-conf\t\t\tEnables confidentiality (do encryption) (enabled by default)\n" +
	" -disable-conf\t\t\tDisables confidentiality (no encryption)\n" +
	" -auth auth\t\t\tIf auth is 'host' host authorization will be performed.\n" +
	"           \t\t\tIf auth is 'self' self authorization will be performed.\n" +
	"           \t\t\tOtherwise, identity authorization is performed.\n" +
	"           \t\t\tAuthorization is not performed by default.\n" +
	" -wrap-mode mode\t\tmode is: 'ssl' (default) or 'gsi'";
    

    public static void main(String [] args) {

	String usage = "Usage: java GssClient [options] host port";

	GetOpts opts = new GetOpts(usage, helpMsg);

	int pos = opts.parse(args);
	
	if (pos + 2 > args.length) {
	    System.err.println(usage);
	    return;
	}

	String host = args[pos];
	int port = Integer.parseInt(args[pos+1]);
	
	// to make sure we use right impl
	GSSManager manager = new GlobusGSSManagerImpl();

	ExtendedGSSContext context = null;
	Socket s = null;
	Authorization auth = SelfAuthorization.getInstance();
	try {
	    GSSName targetName = null;
	    if (opts.auth != null) {
		if (opts.auth.equals("host")) {
		    auth = HostAuthorization.getInstance();
		} else if (opts.auth.equals("self")) {
		    auth = SelfAuthorization.getInstance();
		} else {
		    auth = new IdentityAuthorization(opts.auth);
		}
	    }

            // XXX: When doing delegation targetName cannot be null.
            // additional authorization will be performed after the handshake 
            // in the socket code.
            if (opts.deleg) {
                if (auth instanceof GSSAuthorization) {
                    GSSAuthorization gssAuth = (GSSAuthorization)auth;
                    targetName = gssAuth.getExpectedName(null, host);
                }
            }
	    context = (ExtendedGSSContext)manager.createContext(targetName,
								GSSConstants.MECH_OID,
								null, 
								opts.lifetime);
	    
	    context.requestCredDeleg(opts.deleg);
	    context.requestConf(opts.conf);
	    context.requestAnonymity(opts.anonymity);

	    context.setOption(GSSConstants.GSS_MODE,
			      (opts.gsiMode) ? 
			      GSIConstants.MODE_GSI : 
			      GSIConstants.MODE_SSL);

	    if (opts.deleg) {
		context.setOption(GSSConstants.DELEGATION_TYPE,
				  (opts.limitedDeleg) ? 
				  GSIConstants.DELEGATION_TYPE_LIMITED :
				  GSIConstants.DELEGATION_TYPE_FULL);
	    }

	    context.setOption(GSSConstants.REJECT_LIMITED_PROXY,
			      new Boolean(opts.rejectLimitedProxy));

	    s = GssSocketFactory.getDefault().createSocket(host, port, context);
	    ((GssSocket)s).setWrapMode(opts.wrapMode);
	    ((GssSocket)s).setAuthorization(auth);

	    OutputStream out = s.getOutputStream();
	    InputStream in = s.getInputStream();

	    System.out.println("Context established.");
	    System.out.println("Initiator : " + context.getSrcName());
	    System.out.println("Acceptor  : " + context.getTargName());
	    System.out.println("Lifetime  : " + context.getLifetime());
	    System.out.println("Privacy   : " + context.getConfState());
	    System.out.println("Anonymity : " + context.getAnonymityState());

	    String msg = 
		"POST ping/jobmanager HTTP/1.1\r\n" +
		"Host: " + host + "\r\n" +
		"Content-Type: application/x-globus-gram\r\n" +
		"Content-Length: 0\r\n\r\n";

	    byte [] tmp = msg.getBytes();

	    out.write(tmp);
	    out.flush();

	    String line = null;
	    BufferedReader r = new BufferedReader(new InputStreamReader(in));
	    while ( (line = r.readLine()) != null ) {
		System.out.println(line);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    if (s != null) {
		try { s.close(); } catch(Exception e) {}
	    }
	}
    }
}
