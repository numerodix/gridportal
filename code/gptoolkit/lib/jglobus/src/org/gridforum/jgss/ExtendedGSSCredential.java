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

import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;

/**
 * Defines Java API for credential export extension as defined in the 
 * <a href="http://www.gridforum.org/security/gsi/draft-ggf-gss-extensions-08.pdf">GSS-API Extensions document</a>.
 * Some of the functions might not specify all the parameters as in the document. 
 * <BR><BR>Notes:
 * <UL>
 * <LI>Protection key is currently not supported.</LI>
 * </UL>
 */
public interface ExtendedGSSCredential extends GSSCredential {   
    
    public static final int 
	IMPEXP_OPAQUE = 0,
	IMPEXP_MECH_SPECIFIC = 1;

    /**
     * Exports this credential so that another process might import it. 
     * The exported credential might be imported again using the 
     * {@link ExtendedGSSManager#createCredential(byte[], int, int, Oid, int) 
     * ExtendedGSSManager.createCredential} method.
     * 
     * @param option 
     *        The export type. If set to {@link ExtendedGSSCredential#IMPEXP_OPAQUE
     *        ExtendedGSSCredential.IMPEXP_OPAQUE} exported buffer is an opaque
     *        buffer suitable for storage in memory or on disk or passing to
     *        another process. If set to {@link ExtendedGSSCredential#IMPEXP_MECH_SPECIFIC 
     *        ExtendedGSSCredential.IMPEXP_MECH_SPECIFIC} exported buffer is a 
     *        buffer filled with mechanism-specific information that the calling
     *        application can use to pass the credential to another process that 
     *        is not written to the GSS-API.
     * @return The buffer containing the credential
     * @exception GSSException containing the following major error codes: 
     *            <code>GSSException.CREDENTIAL_EXPIRED,
     *            GSSException.UNAVAILABLE, GSSException.FAILURE</code>
     */
    public byte[] export(int option)
	throws GSSException;
    
    /**
     * Exports this credential so that another process might import it. 
     * The exported credential might be imported again using the 
     * {@link ExtendedGSSManager#createCredential(byte[], int, int, Oid, int)
     * ExtendedGSSManager.createCredential} method.
     * 
     * @param option 
     *        The export type. If set to {@link ExtendedGSSCredential#IMPEXP_OPAQUE 
     *        ExtendedGSSCredential.IMPEXP_OPAQUE} exported buffer is an opaque
     *        buffer suitable for storage in memory or on disk or passing to 
     *        another process. If set to {@link ExtendedGSSCredential#IMPEXP_MECH_SPECIFIC
     *        ExtendedGSSCredential.IMPEXP_MECH_SPECIFIC} exported buffer is a buffer
     *        filled with mechanism-specific information that the calling application
     *        can use to pass the credential to another process that is not written
     *        to the GSS-API.
     * @param mech Desired mechanism for exported credential, may be null to 
     *             indicate system default.
     * @return The buffer containing the credential
     * @exception GSSException containing the following major error codes:
     *            <code>GSSException.CREDENTIAL_EXPIRED,
     *            GSSException.UNAVAILABLE, GSSException.BAD_MECH, GSSException.FAILURE</code>
     */
    public byte[] export(int option, Oid mech)
	throws GSSException;

    /**
     * Retrieves arbitrary data about this credential.
     *
     * @param oid the oid of the information desired.
     * @return the information desired. Might be null.
     * @exception GSSException containing the following major error codes: 
     *            <code>GSSException.FAILURE</code>
     */
    public Object inquireByOid(Oid oid) throws GSSException;
}
