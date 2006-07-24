/**
 * This file is subject to the GNU General Public License (GPL)
 * (http://www.gnu.org/copyleft/gpl.html)
 */

package org.ntnu.utility;


import java.security.*;
import java.security.interfaces.*;


/**
 * A centralized manager class to keep track of certificate objects.
 *
 * @author Martin Matusiak
 */
class NorduGridManager {

	private static NorduGridCertificate cert = new NorduGridCertificate();
	private static NorduGridCertificateRequest certreq =
		new NorduGridCertificateRequest();
	private static NorduGridCertificateObject certobj;
	private static NorduGridPrivateKey key = new NorduGridPrivateKey();
	
	
	private static final int CERTIFICATE = 2;
	private static final int REQUEST = 1;
	private static final int NONE = 0;
	
	private static int state = NONE;
	
	
	public NorduGridCertificateObject getCertObject() {
		if (state == CERTIFICATE) return cert;
		else if (state == REQUEST) return certreq;
		else return null;
	}
	
	
	public NorduGridCertificate getCert() throws Exception {
		readCert();
		state = CERTIFICATE;
		return cert;
	}
	
	
	public NorduGridCertificateRequest getCertReq() throws Exception {
		readCertReq();
		state = REQUEST;
		return certreq;
	}
	
	
	public NorduGridPrivateKey getKey() throws Exception {
		readPrivateKey();
		return key;
	}
	
	
	public static String getDNAsUsername() {
		if (certobj == null) return "";
		
		return certobj.getDNAsUsername();
	}
	
	
	private void readCert() throws Exception {
	
		// Certificate not found
		if (!LibCommon.fileExists(Config.userCertFile))
			throw new Exception("Certificate not found, expecting in " +
				Config.userCertFileName);
			
		// Could not load certificate
		if (!cert.readFromFile(Config.userCertFile))
			throw new NorduGridCertCorruptException("Certificate found in " +
				Config.userCertFileName + ", failed to load");
		
		// Check if private key is present
		//readPrivateKey();
		
		// Check if public corresponds to private key
		//verifyKeys(cert.getPublicKey(), key.getPrivateKey());
		
		certobj = cert;
		
	}
	
	
	private void readCertReq() throws Exception {
	
		// Certificate not found
		/* 
			This will seem misleading, but since the certificate takes presedence
			over the request, if there's no request, we report status about the
			certificate.
		*/
		if (!LibCommon.fileExists(Config.userCertReqFile))
			throw new Exception("Certificate not found, expecting in " +
				Config.userCertFileName);
			
		// Could not load certificate
		if (!certreq.readFromFile(Config.userCertReqFile))
			throw new Exception("Certificate request found in " +
				Config.userCertReqFileName + ", failed to load");
		
		// Check if private key is present
		//readPrivateKey();
		
		// Check if public corresponds to private key
		//verifyKeys(certreq.getPublicKey(), key.getPrivateKey());

		certobj = certreq;
		
	}
	
	
	private void readPrivateKey() throws Exception {
	
		// Private key not found
		if (!LibCommon.fileExists(Config.userKeyFile))
			throw new Exception("Private key not found, expecting in " +
				Config.userKeyFileName);
				
		// Could not load private key
		if (!key.readFromFile(Config.userKeyFile))
			throw new Exception("Private key found in " +
				Config.userKeyFileName + ", failed to load");
		
	}
	
	
/**
 * Verifies that a public key corresponds to a private key.
 *
 * @author Martin Matusiak
 */	
	public void verifyKeys(PublicKey pubkey, PrivateKey privkey) throws Exception {
		if ((pubkey == null) || (privkey == null))
			throw new Exception("Unable to process keys");
		
		RSAPublicKey pukey;
		RSAPrivateKey prkey;
		try {
			pukey = (RSAPublicKey) pubkey;
			prkey = (RSAPrivateKey) privkey;
		} catch (Exception e) {
			throw new Exception("Keys not in correct format (RSA)");
		}

/*		
		String out = "";
		out += "\npublic modulus  " + pukey.getModulus();
		out += "\nprivate modulus " + prkey.getModulus();
			
		out += "\npublic exponent " + pukey.getPublicExponent();
		out += "\nprivate exponent " + prkey.getPrivateExponent();
				
		out += "\npublic algorithm " + pukey.getAlgorithm();
		out += "\nprivate algorithm " + prkey.getAlgorithm();
				
		out += "\npublic format " + pukey.getFormat();
		out += "\nprivate format " + prkey.getFormat();
				
		out += "\npublic encoded " + pukey.getEncoded();
		out += "\nprivate encoded " + prkey.getEncoded();
		
		System.out.println(out);

			
		String pubmod = pukey.getModulus();
		String primod = prkey.getModulus();
*/			
		if (!pukey.getModulus().equals(prkey.getModulus()))
			throw new Exception("Private key does not match public key");
	}
	

}


/**
 * An exception class to distinguish the exception from any other.
 *
 * @author Martin Matusiak
 */
class NorduGridCertCorruptException extends Exception {
	public NorduGridCertCorruptException(String msg) {
		super(msg);
	}
}