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

			<h2>Create a certificate</h2>
				Please be advised that you must enter a valid email address. You will need this address later to complete the registration procedure.
				<br><b>NOTE:</b> Please do take great care in not forgetting the username or password you enter below. There is no possibility of looking them up for you, if you become locked out of your account, you will have to register a new one.
		
			<table style="margin: 0px auto;">
				<tr>
					<th>first name:</th>
					<td><input value="John"></td>
				</tr>
				<tr>
					<th>last name:</th>
					<td><input value="Doe"></td>
				</tr>
				<tr>
					<th>email address:</th>
					<td><input value="john.doe@ntnu.no"></td>
				</tr>
				<tr>
					<th>organization:</th>
					<td><input value="ntnu.no"></td>
				</tr>			
				<tr>
					<td>&nbsp;</td><td></td>
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
					<th><input type="submit" value="submit"></th>
					<td><input type="reset" value="reset"></td>
				</tr>
			</table>

		</div>

	</div>
	
	<div class="design">
		<div class="subtitle">Design</div>

		All certificate handling is done in a small client application, distributed as a java app (an applet possibly?). The user first creates a certificate as shown. The username and password are stored to later be used in a session with myProxy. The password supplied also becomes the password corresponding to the certificate.
	</div>
	
	<div class="implementation">
		<div class="subtitle">Implementation</div>
	</div>

</div>

</body>
</html>