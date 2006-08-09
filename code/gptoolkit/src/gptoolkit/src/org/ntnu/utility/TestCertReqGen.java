/**
 * This file is subject to the GNU General Public License (GPL)
 * (http://www.gnu.org/copyleft/gpl.html)
 */

package org.ntnu.utility;


import java.security.*;
import java.security.interfaces.*;

import org.globus.cog.security.cert.request.GridCertRequest;
import org.globus.gsi.OpenSSLKey;
import org.globus.gsi.bc.BouncyCastleOpenSSLKey;


/**
 * Helper class to verify certificate creation mechanism.
 *
 * @author Martin Matusiak
 */
class TestCertReqGen {

	private static String password = "asdfgh";

	public static void main(String[] args) throws Exception {
	
		LibCommon.initProvider();
	
		GridCertRequest gridcertrequest = new GridCertRequest();
		gridcertrequest.genCertificateRequest("O=Grid,O=NorduGrid,OU=ntnu.no,CN=Martin Matusiak",
			"", password,
			Config.userKeyFile,
			Config.userCertFile,
			Config.userCertReqFile
		);
		
		
		
		OpenSSLKey opensslkey = new BouncyCastleOpenSSLKey(Config.userKeyFile);
		
//		if (opensslkey.isEncrypted())
			opensslkey.decrypt(password);
		
		RSAPrivateKey userKey = (RSAPrivateKey) opensslkey.getPrivateKey();
		System.out.println(userKey.getModulus());
	
	}
	
}
