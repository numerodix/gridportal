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
package org.gridforum.jgss;

import org.ietf.jgss.GSSManager;
import org.ietf.jgss.Oid;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;

/**
 * Defines Java API for credential import extension as defined in the 
 * <a href="http://www.gridforum.org/security/gsi/draft-ggf-gss-extensions-07.pdf">GSS-API Extensions document</a>.
 * Some of the functions might not specify all the parameters as in the document. 
 * <BR><BR>Notes:
 * <UL>
 * <LI>Protection key is currently not supported.</LI>
 * </UL>
 */
public abstract class ExtendedGSSManager extends GSSManager {

    private static ExtendedGSSManager gssManager;
    
    protected ExtendedGSSManager() {}

    /**
     * A factory method for creating a previously exported credential.
     *
     * @param buff 
     *        The token emitted from the {@link ExtendedGSSCredential#export(int, Oid)
     *        ExtendedGSSCredential.export} method.
     * @param option
     *        The import type. The import type must be the same as the 
     *        option used to export the buffer.
     * @param lifetime 
     *        The number of seconds that credentials should remain valid. Use 
     *        GSSCredential.INDEFINITE_LIFETIME to request that the credentials have 
     *        the maximum permitted lifetime. Use GSSCredential.DEFAULT_LIFETIME to request
     *        default credential lifetime.
     * @param mech 
     *        The desired mechanism for the imported credential, may be null to indicate system default.
     * @param usage 
     *        The intended usage for this credential object. The value of this parameter must be one of:
     *        GSSCredential.INITIATE_AND_ACCEPT, GSSCredential.ACCEPT_ONLY, and GSSCredential.INITIATE_ONLY. 
     * @exception GSSException containing the following major error codes: <code>GSSException.BAD_MECH, 
     *            GSSException.DEFECTIVE_TOKEN, GSSException.NO_CRED, GSSException.CREDENTIAL_EXPIRED,
     *            GSSException.FAILURE</code>
     */
    public abstract GSSCredential createCredential (byte[] buff, 
						    int option,
						    int lifetime,
						    Oid mech,
						    int usage)
	throws GSSException;
    
    public synchronized static GSSManager getInstance() {
	if (gssManager == null) {
	    String className = System.getProperty("org.globus.gsi.gssapi.provider");
	    if (className == null) {
		className = "org.globus.gsi.gssapi.GlobusGSSManagerImpl";
	    }
	    try {
		Class clazz = Class.forName(className);
		if (!ExtendedGSSManager.class.isAssignableFrom(clazz)) {
		    throw new RuntimeException("Invalid ExtendedGSSManager provider class: '" + 
					       className + "'");
		}
		gssManager = (ExtendedGSSManager)clazz.newInstance();
	    } catch (ClassNotFoundException e) {
		throw new RuntimeException("Unable to load '" + className + "' class: " +
					   e.getMessage());
	    } catch (InstantiationException e) {
		throw new RuntimeException("Unable to instantiate '" + className + "' class: " +
					   e.getMessage());
	    } catch (IllegalAccessException e) {
		throw new RuntimeException("Unable to instantiate '" + className + "' class: " +
					   e.getMessage());
	    }
	}
	return gssManager;
    }
    
}
