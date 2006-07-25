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

		<div class="client">
	
			<h2>Register on GRIDportal</h2>
			Once you have a signed certificate, you are required to register with GRIDportal. Your certificate will be transferred to GRIDportal and after this is complete, you will no longer need it on your computer.
		
			<table style="margin: 0px auto;">
				<tr>
					<th>certificate:</th>
					<td>cert.pem</td>
				</tr>
				<tr>
					<th>username:</th>
					<td><input value="jdoe"></td>
				</tr>
				<tr>
					<th>password:</th>
					<td><input type="password" value="jimmy"></td>
				</tr>
				<tr>
					<th><input type="submit" value="send"></th>
					<td><input type="reset" value="reset"></td>
				</tr>
			</table>
	
		</div>

	</div>
	
	<div class="design">
		<div class="subtitle">Design</div>
		
		We may as well call this "account activation". The purpose is to send the certificate to myProxy (socket connection, not through the portal) and store it there with the username/password the user supplied before.
		<br><br>
		We can let myProxy policy determine who gets access and who does not.
		<br><br>
		<b>NOTE:</b> This is where use of the client application ends, it is only required for registration, not for everyday use.
	</div>
	
	<div class="implementation">
		<div class="subtitle">Implementation</div>
	</div>

</div>

</body>
</html>