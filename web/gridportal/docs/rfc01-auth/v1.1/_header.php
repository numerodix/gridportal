	<div class="title">
		<h1>GRIDportal RFC01 : authentication mechanism</h1>
		pages: <a href="index.php">cert_new</a> | 
					<a href="cert_sign.php">cert_sign</a> | 
					<a href="myproxy_reg.php">myproxy_reg</a> | 
					<a href="user_login.php">user_login</a>
	</div>
	
	<b>Version 1.1</b><br>
	For reasons of maintaining better security, a new authentication model was devised. The main security threat is seen to be the system of sending certificates to the server. A compromised server could allow an attacker to intercept private keys. A compromise solution is presented here, which will hopefully both improve security and still accomodate a reasonably user friendly mechanism.
	<br><br>