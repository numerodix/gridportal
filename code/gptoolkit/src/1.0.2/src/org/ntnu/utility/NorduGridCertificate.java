/**
 * This file is subject to the GNU General Public License (GPL)
 * (http://www.gnu.org/copyleft/gpl.html)
 */

package org.ntnu.utility;


import java.util.*;
import java.io.*;

import java.security.cert.X509Certificate;
import java.security.cert.CertificateFactory;
import java.security.PublicKey;
import java.security.PrivateKey;

import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.x509.*;

import org.bouncycastle.jce.*;


/**
 * A class to manage a certificate.
 *
 * @author Martin Matusiak
 */
class NorduGridCertificate extends NorduGridCertificateObject {

/* the email address is known as rfc822Name, corresponding to int value 1:
 SubjectAltName ::= GeneralNames

 GeneralNames :: = SEQUENCE SIZE (1..MAX) OF GeneralName

 GeneralName ::= CHOICE {
      otherName                       [0]     OtherName,
      rfc822Name                      [1]     IA5String,
      dNSName                         [2]     IA5String,
      x400Address                     [3]     ORAddress,
      directoryName                   [4]     Name,
      ediPartyName                    [5]     EDIPartyName,
      uniformResourceIdentifier       [6]     IA5String,
      iPAddress                       [7]     OCTET STRING,
      registeredID                    [8]     OBJECT IDENTIFIER}
*/	
	private static final Integer x509ExtenstionEmail = new Integer(1);
		
	private X509Certificate cert;
	
	
	public X509Certificate getObject() {
		return cert;
	}


	public boolean readFromFile(String filepath) {
		try {

			String base64string;
			base64string = getCertFileContentWithDelimiters(filepath);

			InputStream inStream = new StringBufferInputStream(base64string);
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			cert = (X509Certificate) cf.generateCertificate(inStream);
			inStream.close();
			
			if (!parseDNstring(cert.getSubjectX500Principal().getName())) return false;
			
			this.email = readEmail(cert);
			this.issuer = cert.getIssuerX500Principal().getName();
			this.valid = checkValidity();
			this.validfrom = cert.getNotBefore().toString();
			this.validto = cert.getNotAfter().toString();
			
			this.publickey = cert.getPublicKey();
		
		} catch (Exception e) {
			//e.printStackTrace();
			return false;
		}
		
		populated = true;
		return true;
	}
	
	
	public boolean checkValidity() {
	
		try {
			cert.checkValidity();
		} catch (Exception e) {
			return false;
		}
		
		return true;
	
	}
	
	
	public boolean verify() {
		PublicKey pub = cert.getPublicKey();
		//PrivateKey priv = 
		
		try {
			cert.verify(cert.getPublicKey());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	
	private boolean parseDNstring(String DNstring) {
		
		try {
			StringTokenizer tok = new StringTokenizer(DNstring, ",");
			while (tok.hasMoreTokens()) {
				String pair = tok.nextToken().trim();
				
				StringTokenizer to = new StringTokenizer(pair, "=");
				while (to.hasMoreTokens()) {
					String key = to.nextToken();
					
					if (key.equals("CN"))
						name = to.nextToken().trim();
					else if (key.equals("OU"))
						domain = to.nextToken().trim();
				}
			}
		} catch (Exception e) {
			return false;
		}
	
		return true;
	}
	
	
	private String readEmail(X509Certificate cert) {
	
		try {
		
			Collection names = cert.getSubjectAlternativeNames();
			
			Iterator it = names.iterator();
			while (it.hasNext()) {
				List name = (List) it.next();
				Iterator i = name.iterator();
				
				while (i.hasNext()) {
					Integer key = (Integer) i.next();

					if (key.equals(x509ExtenstionEmail))
						return (String) i.next();
				}
			}
			
		} catch (Exception e) {
			return null;
		}
		
		return null;
	}
	
}