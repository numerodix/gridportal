/**
 * This file is subject to the GNU General Public License (GPL)
 * (http://www.gnu.org/copyleft/gpl.html)
 */

package org.ntnu.utility;


import java.util.*;

import java.io.*;

import java.math.BigInteger;

import java.security.*;
import java.security.cert.*;

import javax.crypto.*;
import javax.crypto.spec.*;
import javax.crypto.spec.IvParameterSpec;

import javax.net.ssl.*;

import org.globus.cog.security.cert.request.GridCertRequest;


/**
 * A library class to handle certificate request creation, can be invoked
 * in a separate thread.
 *
 * @author Martin Matusiak
 */
class LibCert implements Runnable {

	private String dnString;
	private char[] password;
	private String userCertReqFile = null;
	private String userCertFile = null;
	private String userKeyFile = null;
	private String globusDir = null;

	// report status to listener
	private ThreadListener threadlistener;


	public LibCert(ThreadListener threadlistener) {
	
		this.threadlistener = threadlistener;
	
	}
	
	
	/**<pre>
		*this method checks whether String test is printable,
		*to avoids ASN.1 parsing problems at several OpenSSL based CAs
		*
		*this is a bad workaround really --- OpenSSL should handle UTF8 !?
		*</pre>
		*/
	
	public boolean charsetIsValid(String test) {
		for (int i = test.length() - 1; i >= 0; i--) {
			if (test.charAt(i) > 0x007f) {
				return false;
			}

			if (test.charAt(i) == '_') {
				return false; //
			}
		}

		return true;
	}
	
	
	public void setDN(String name, String email, String domain,
		String org0, String org1) {
		dnString =  "O=" + org0 +
						",O=" + org1 +
						",OU=" + domain +
						",CN=" + name +
						",EmailAddress=" + email;
	}


	public void setPassword(char[] password) {
		this.password = password;
	}


	public void setPath(String globusDir) {
		this.globusDir = globusDir;
	}
	
	
	public void setKeys(String userCertFile, String userCertReqFile, 
		String userKeyFile) {
		this.userCertFile = userCertFile;
		this.userCertReqFile = userCertReqFile;
		this.userKeyFile = userKeyFile;
	}


	public void run() {
		generateRequest();
	}


/**
 * Creates a PKCS10 certificate request.
 */
	public void generateRequest() {

		try {

			// Create target directory if it does not exist
			File dir = new File(globusDir);
			if(!dir.exists())
				dir.mkdirs();
				
			// Check if certificate file exists
			if ((new File(globusDir + userCertReqFile)).exists())
				throw new Exception("Certificate request file exists in: " +
					globusDir + userCertReqFile + ", will not overwrite.");
				
			// Check if private key file exists
			if ((new File(globusDir + userKeyFile)).exists())
				throw new Exception("Private key file exists in: " +
					globusDir + userKeyFile + ", will not overwrite.");
/*		
			// Set up the provider
			Security.addProvider(new BouncyCastleProvider());
			Provider[] providers = Security.getProviders();
			for (int i=0; i < providers.length; i++) {
				System.out.println(providers[i].toString());
			}
	
			//X509Name subject = stringToBcX509Name(dnString);
			X509Name subject = new X509Name(dnString);
			
			// generate a new RSA keypair
			KeyPair kp = lib.generateNewKeys("RSA", 1024);
			
			//
			// set up the parameters
			//
			byte[] salt = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
			int iterationCount = 100;
			PBEParameterSpec defParams = new PBEParameterSpec(salt,
							iterationCount);
			String alg = "1.2.840.113549.1.12.1.3"; // 3 key triple DES with SHA-1
			AlgorithmParameters params = AlgorithmParameters.getInstance(alg,
							"BC");

			params.init(defParams);
	
			//
			// set up the key
			//
			PBEKeySpec pbeSpec = new PBEKeySpec(password);
			SecretKeyFactory keyFact = SecretKeyFactory.getInstance(alg, "BC");
			Cipher cipher = Cipher.getInstance(alg, "BC");
	
			cipher.init(Cipher.WRAP_MODE, keyFact.generateSecret(pbeSpec),
					params);
	
			byte[] wrappedKey = cipher.wrap(kp.getPrivate());
	
			//
			// create encrypted object
			//
			EncryptedPrivateKeyInfo pInfo = new EncryptedPrivateKeyInfo(params,
							wrappedKey);
	
			// create the request object
			PKCS10CertificationRequest req1 = new PKCS10CertificationRequest("SHA1withRSA",
							subject, kp.getPublic(), null, kp.getPrivate());
			req1.verify();
	
			byte[] request = req1.getEncoded();

			FileOutputStream fos = new FileOutputStream(name +
							System.getProperty("file.separator") + userKeyFile);
			String keyStart = "-----BEGIN ENCRYPTED PRIVATE KEY-----\n";
			String keyEnd = "\n-----END ENCRYPTED PRIVATE KEY-----\n";
			fos.write(keyStart.getBytes());
			fos.write(Base64.encode(pInfo.getEncoded(), true));
			fos.write(keyEnd.getBytes());
			fos.close();
				
			fos = new FileOutputStream(name + userCertReqFile);
				String certStart = "-----BEGIN CERTIFICATE REQUEST-----\n";
			String certEnd = "\n-----END CERTIFICATE REQUEST-----\n";
			fos.write(certStart.getBytes());
			fos.write(Base64.encode(request, true));
			fos.write(certEnd.getBytes());
			fos.close();
*/
//			System.out.println("X509: " + subject.toString());

			GridCertRequest gridcertrequest = new GridCertRequest();
			gridcertrequest.genCertificateRequest(dnString, "", new String(password),
				globusDir + userKeyFile,
				globusDir + userCertFile,
				globusDir + userCertReqFile
			);
// 			System.out.println(new String(password));
				
		} catch (Exception e) {
			
			threadlistener.reportFatalError(e);
			
		}
			
	}

}