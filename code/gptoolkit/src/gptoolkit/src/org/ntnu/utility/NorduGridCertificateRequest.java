/**
 * This file is subject to the GNU General Public License (GPL)
 * (http://www.gnu.org/copyleft/gpl.html)
 */

package org.ntnu.utility;


import java.io.*;
import java.util.*;

import org.bouncycastle.jce.PKCS10CertificationRequest;

import org.bouncycastle.asn1.pkcs.CertificationRequestInfo;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.asn1.DERObjectIdentifier;

import org.bouncycastle.util.encoders.Base64;


/**
 * A class to manage a certificate request.
 *
 * @author Martin Matusiak
 */
class NorduGridCertificateRequest extends NorduGridCertificateObject {

	private PKCS10CertificationRequest certreq;
	
	
	public PKCS10CertificationRequest getObject() {
		return certreq;
	}
	
	
/**
 * Reads a certificate request file in PKCS10 and PEM format.
 */	
	public boolean readFromFile(String filepath) {
	
		try {
			
			// initiate the certificate request
			BufferedReader br = new BufferedReader(new FileReader(filepath));
			String begin = br.readLine();
			if (begin.equals("-----BEGIN CERTIFICATE REQUEST-----") == false)
				return false;
				
				
			String base64 = new String();
			boolean tracking = true;
			while (tracking) {
			String line = br.readLine();
				if (line.startsWith("-----"))
				tracking = false;
				else
				base64 += line;
			}
			br.close();
			byte[] certData = Base64.decode(base64);
			certreq = new PKCS10CertificationRequest(certData);
			
			// read info from cert request
			CertificationRequestInfo certinfo = certreq.getCertificationRequestInfo();
			X509Name x509name = certinfo.getSubject();
			
			readAttributes(x509name);
			
			this.publickey = certreq.getPublicKey();

		} catch (Exception e) {
			//e.printStackTrace();
			return false;
		}
		
		populated = true;
		return true;
	
	}
	
	
	private void readAttributes(X509Name x509name) throws Exception {
		Vector oids = x509name.getOIDs();
		Vector values = x509name.getValues();
		
		for (int i=0; i < oids.size(); i++) {
			String id = ((DERObjectIdentifier) oids.get(i)).toString();
			String value = (String) values.get(i);
			//System.out.println( id.toString() + ", " + value);
			
			if (id.equals("2.5.4.11"))
				this.domain = value;
			else if (id.equals("2.5.4.3"))
				this.name = value;
			else if (id.equals("1.2.840.113549.1.9.1"))
				this.email = value;
			
		}
	}

}