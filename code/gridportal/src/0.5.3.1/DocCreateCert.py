from LunarcPage import LunarcPage

from HyperText.HTML import *

import Lap

class DocCreateCert(LunarcPage):

	def title(self):
		return 'Getting Started :: Creating a certificate request'

	def writeContent(self):
	
		wr = self.writeln
		w = self.write
		
		content = """
		
		<h2>Getting Started :: Creating a certificate request</h2>
		
		<table>
		<tr>
		<td valign=top>
		
		<p>On the <span id="acc">Create certificate request</span> page, fill in the form.
		</p>
		<ul>
		<li><strong>Full name:</strong> your full name (the program will not accept non-ASCII characters)
		<li><strong>Email address:</strong> your email address
		<li><strong>Domain:</strong> the domain of your organization (usually that will be the part of your email address after the '@'. eg. <span id="accs">ntnu.no</span> for an email address like <span id="accs">marmat@ntnu.no</span>. The purpose of this field is to identify you as an individual tied to a specific organization, which may or may not be reflected in your email address (eg. if you have a hotmail email account, give the domain of your institution, not "hotmail.com").
		<li><strong>Level-O Organization:</strong> this will always be <span id="accs">Grid</span>
		<li><strong>Level-1 Organization:</strong> this will always be <span id="accs">NorduGrid</span>
		</ul>
		Now click <span id="accb">Create</span>.
		</p>
		
		<p>In the password dialog, enter a password to use with your certificate (and GRIDportal). Then, click <span id="accb">Proceed</span>. Your certificate request is now created.
		</p>

		</td>
		<td>&nbsp;&nbsp;</td>
		<td valign=top>
		
		<img src="images/start/cert_create01.png"><br><br>
		<img src="images/start/cert_create02.png"><br><br>
		<img src="images/start/cert_create03.png"><br>

		</td>
		
		</tr>
		</table>
		
		<p>&nbsp;</p>
		
		<table>
		<tr>
		<td valign=top>
		
		<p>On the <span id="acc">Summary</span> page, click <span id="accb">Detect</span>. Your certificate request should now be detected correctly, as shown in the figure. You can click <span id="accb">View certificate</span> to inspect the contents of the certificate request.
		</p>
		
		<p>On the <span id="acc">Mail signing request</span> page, you should now see a filled in form. This form does not mail the request for signature, it simply shows what has to be sent. In your email client, send an email message with the values shown in this form.
		<br><strong>NOTE:</strong> Be sure to attach your signature request file (<span id="accs">usercert_request.pem</span>) with the message you send!
		</p>
		
		<p>You should receive your signed certificate by email within a few days.
		</p>
		
		<p><strong>NOTE:</strong> Some of our users have wondered why GridPortalToolkit cannot send the email message itself. It is simply because sending a message from a program requires an email server to available for this operation. This is a requirement we do not wish GridPortalToolkit to have, thus assuming that every user of GRIDportal already has a working email account.
		</p>
		
		<p>&nbsp;</p>
		<p><a href="DocToolkit"><< Return to Getting Started Main Menu</a></p>
		
		</td>
		<td>&nbsp;&nbsp;</td>
		<td valign=top>
		
		<img src="images/start/cert_reqdetect01.png"><br><br>
		<img src="images/start/cert_reqsign01.png"><br>

		</td>
		
		</tr>
		</table>
		
		<p>&nbsp;</p>		
		"""
		
		w(content)
