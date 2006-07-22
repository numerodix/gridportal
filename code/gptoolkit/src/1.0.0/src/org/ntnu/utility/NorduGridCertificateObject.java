/**
 * This file is subject to the GNU General Public License (GPL)
 * (http://www.gnu.org/copyleft/gpl.html)
 */

package org.ntnu.utility;


import java.util.*;
import java.io.*;

import org.bouncycastle.jce.PKCS10CertificationRequest;

import org.bouncycastle.asn1.pkcs.CertificationRequestInfo;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.asn1.DERObjectIdentifier;

import org.bouncycastle.util.encoders.Base64;

import java.security.PublicKey;
import java.security.PrivateKey;


/**
 * An abstract class to handle a certificate or certificate request object.
 * Some fields are common for both, some methods are too.
 *
 * @author Martin Matusiak
 */
abstract class NorduGridCertificateObject {

	public static final String org0 = "Grid";
	public static final String org1 = "NorduGrid";

	protected String domain;
	protected String name;
	protected String email;
	
	protected String issuer;
	protected String validfrom;
	protected String validto;
	protected boolean valid;
	
	protected PublicKey publickey;
	
	protected boolean populated = false;
	

	abstract public boolean readFromFile(String filepath);
	
	
	public boolean isPopulated() {
		return populated;
	}
	
	
	public PublicKey getPublicKey() {
		return publickey;
	}	
	
	
	public String getDN() {
		if (domain != null)
			return "O=" + org0 +
							",O=" + org1 +
							",OU=" + domain +
							",CN=" + name +
							",EmailAddress=" + email;
		else return null;
	}
	
	
	public String getDNAsUsername() {
		return domain.replaceAll("\\.", "") + "_" + name.replaceAll(" ", "");
	}
	
	
/**
 * A method to receive information about the certificate [request] object in a
 * structured form (for later processing).
 */
	public Vector[] getAttributes() {
		String[][] labels = {
			{"0-level organization", "1-level organization", "Domain", "Full name",
			"Email address"},
			{"Issuer", "Valid from", "Valid to"}
		};	
		String[][] values = {
			{org0, org1, domain, name, email},
			{issuer, validfrom, validto}
		};
	
		Vector subject = new Vector();
		Vector signature = new Vector();
		
		// populate subject vector with data
		subject.add(getVector("Subject", null));
		for (int i=0; i < labels[0].length; i++) {
			subject.add(getVector( labels[0][i], values[0][i] ));
		}
		
		// signature is valid
		signature.add(getVector("Signature", null));
		if (valid) {
			signature.add(getVector("Signature present", "Yes"));	
			signature.add(getVector("Certificate is valid", "Yes"));
			for (int i=0; i < labels[1].length; i++) {
				signature.add(getVector( labels[1][i], values[1][i] ));
			}
			
		// signature is invalid, certificate has expired
		} else if (issuer != null) {
			signature.add(getVector("Signature present", "Yes"));
			signature.add(getVector("Certificate is valid", "No, needs renewal"));
			for (int i=0; i < labels[1].length; i++) {
				signature.add(getVector( labels[1][i], "Not available" ));
			}
			
		// signature is not found
		} else {
			signature.add(getVector("Signature present", "No"));
			signature.add(getVector("Certificate is valid", "No, needs signing"));
		}
		
		Vector[] out = {subject, signature};
		return out;
	}
	
	
	private List getVector(String name, String value) {
		Vector vector = new Vector();
		vector.add(name);
		vector.add(value);
		return vector;
	}

}