<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="styles.css" type="text/css">
</head>

<body>

<div="wrapper">

	<? include("_header.php"); ?>

	<div class="ui">
		<div class="subtitle">User interface</div>
	
		<h2>Log in to GRIDportal</h2>
			Provide a valid username and password to log into GRIDportal.
			<br><b>NOTE:</b> the login procedure assumes you already have stored your certificate on the server, otherwise you must complete that procedure first.
	
		<table style="margin: 0px auto;">
			<tr>
				<th>username:</th>
				<td><input value="jdoe"></td>
			</tr>
			<tr>
				<th>password:</th>
				<td><input type="password" value="jimmy"></td>
			</tr>
			<tr>
				<th><input type="submit" value="log in"></th>
				<td><input type="reset" value="reset"></td>
			</tr>
		</table>
	</div>
	
	<div class="design">
		<div class="subtitle">Design</div>
		
			The user supplies a username/password, which is used to query myProxy for a proxy. Once a proxy is available, the authentication is deemed successful. The proxy is now used to interact with NorduGrid/ARC. The session is managed on GRIDportal rules (default WebKit session timeout is 1h). GRIDportal renews the proxy if necessary (if it were to expire before the session).
	</div>
	
	<div class="implementation">
		<div class="subtitle">Implementation</div>
		
		We can use the <i>Authentication.pm</i> perl module from <a href="http://gridport.net">gridport</a> and write a simple client to query myProxy.
	</div>

</div>

</body>
</html>