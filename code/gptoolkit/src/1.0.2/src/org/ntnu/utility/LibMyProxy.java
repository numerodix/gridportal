/**
 * This file is subject to the GNU General Public License (GPL)
 * (http://www.gnu.org/copyleft/gpl.html)
 */

package org.ntnu.utility;


import java.io.*;

import java.security.cert.X509Certificate;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;

import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSName;

import org.globus.common.CoGProperties;
import org.globus.myproxy.*;
import org.globus.gsi.CertUtil;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.GSIConstants;
import org.globus.gsi.OpenSSLKey;
import org.globus.gsi.bc.BouncyCastleCertProcessingFactory;
import org.globus.gsi.bc.BouncyCastleOpenSSLKey;
import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;

// http://www-unix.mcs.anl.gov/~gawor/jglobus-nightly/HEAD/javadoc/org/globus/myproxy/MyProxy.html


/**
 * A library class to handle the MyProxy.store() operation, can be
 * invoked as separate thread.
 *
 * @author Martin Matusiak
 */
class LibMyProxy implements Runnable {

	private static final int KEY_LENGTH = 1024;
	// 5 minutes should be plenty
	private int credLifetime = 5 * 60;
	
	private String myProxyHost = null;
	private int myProxyPort = -1;
	
	private String username = null;
	private String password = null;
	
	// report status to listener
	private ThreadListener threadlistener;
 
 
	public LibMyProxy(ThreadListener threadlistener) {
		this.threadlistener = threadlistener;	
	}
	
	
 	public void setParams(String myProxyHost, String myProxyPort) {
		this.myProxyHost = myProxyHost;
		
		// port must be an integer
		try {
			this.myProxyPort = Integer.parseInt(myProxyPort);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
	
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	public void run() {
		storeCertificate();
		//getProxy();
	}
	
	
/**<pre>
 * A library class to handle the MyProxy store() operation. Uses the MyProxy
 * class from jglobus. The following is being sent:
 * + username (for use with MyProxy)
 * + password (for use with MyProxy, and to unlock the private key)
 * + certificate
 * + private key
 * + a proxy credential generated in the function
 *
 * </pre>
 */
	private void storeCertificate() {
		try {
		
			X509Certificate[] cert = new X509Certificate[1];
			cert[0] = Config.manager.getCert().getObject();

			OpenSSLKey key = Config.manager.getKey().getOpenSSLKey();

			MyProxy myproxy = new MyProxy(myProxyHost, myProxyPort);

			this.username = Config.manager.getDNAsUsername();
			StoreParams storeRequest = new StoreParams();
			storeRequest.setUserName(username);
			storeRequest.setPassphrase(password);
// 			storeRequest.setLifetime(24 * 3600);
//			storeRequest.setCredentialName(credName);
//			storeRequest.setCredentialDescription(credDesc);
			
			GSSCredential credential = createNewProxy(password,
				credLifetime, KEY_LENGTH);

			myproxy.store(credential, cert, key, storeRequest);
		
		} catch (Exception e) {
			//e.printStackTrace();
			threadlistener.reportFatalError(e);
		}
	}
	
	
/**
 * Create a proxy credential on the fly.
 */
	private GlobusGSSCredentialImpl createNewProxy(String keyPassword, int lifetime,
		int bits) throws Exception {
	
		CertUtil.init();
		
		OpenSSLKey opensslkey = new BouncyCastleOpenSSLKey(Config.userKeyFile);
	
		try {
			if (opensslkey.isEncrypted()) {
// 				System.out.println(keyPassword);
				opensslkey.decrypt(keyPassword);
			}
		} catch (Exception e) {
			throw new Exception("Could not decrypt private key, check password.");
		}
		
		PrivateKey userKey = opensslkey.getPrivateKey();
		X509Certificate userCert = CertUtil.loadCertificate(Config.userCertFile);
		
		Config.manager.verifyKeys(userCert.getPublicKey(), userKey);
		
		BouncyCastleCertProcessingFactory factory =
			BouncyCastleCertProcessingFactory.getDefault();
	
		boolean limited = false;
	
		int proxyType = (limited) ? 
			GSIConstants.DELEGATION_LIMITED : GSIConstants.DELEGATION_FULL;
		
		GlobusCredential proxy = 
		factory.createCredential(new X509Certificate[] {userCert},
						userKey,
						bits,
						lifetime,
						proxyType);
	
		return new GlobusGSSCredentialImpl(proxy, GSSCredential.INITIATE_ONLY);
	
	}
	
	
/**
 * Query MyProxy for a credential based on the store() operation being 
 * completed. Can be used to verify MyProxy.store() has been done successfully.
 */	
	public void getProxy() {
	
		try {
		
			MyProxy myproxy = new MyProxy(myProxyHost, myProxyPort);
			GSSCredential cred =
				myproxy.get("username", "password", 3600);
			GSSName credname = cred.getName();
			String o = "";
			o += "\nname " + credname.toString();
			o += "\nlifetime " + cred.getRemainingLifetime();
			System.out.println(o);
		
		} catch (Exception e) {
		 e.printStackTrace();
		}
	
	}

}