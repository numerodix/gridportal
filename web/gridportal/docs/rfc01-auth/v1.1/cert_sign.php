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
	
			<h2>Request certificate signature</h2>
			Your certificate now has to be signed by a valid certificate authority. The certificate will be sent to a certificate authority which will verify you as a member of the organization you stated.
			<br>Choose the authority closest to you to send a request for signing.
		
			<table style="margin: 0px auto;">
				<tr>
					<th>first name:</th>
					<td>John</td>
				</tr>
				<tr>
					<th>last name:</th>
					<td>Doe</td>
				</tr>
				<tr>
					<th>email address:</th>
					<td>john.doe@ntnu.no</td>
				</tr>
				<tr>
					<th>organization:</th>
					<td>ntnu.no</td>
				</tr>			
				<tr>
					<td>&nbsp;</td><td></td>
				</tr>
				<tr>
					<th>username:</th>
					<td>jdoe</td>
				</tr>
				<tr>
					<th>password:</th>
					<td>*****</td>
				</tr>
				<tr>
					<td>&nbsp;</td><td></td>
				</tr>
				<tr>
					<th>certificate authority:</th>
					<td>
						<select>
							<option selected>nordugrid.org</option>
							<option>nbi.dk</option>
							<option>mama's certificates</option>
						</select>
					</td>
				</tr>
				<tr>
					<th>additional information:</th>
					<td>
						<textarea cols=40 rows=4>Any additional information which could be helpful to identify you as a member of said organization.</textarea>
					</td>
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
		
		The certificate signiture request form is not changed from v1.0 but it is now contained within the client application.
	</div>
	
	<div class="implementation">
		<div class="subtitle">Implementation</div>
	</div>

</div>

</body>
</html>