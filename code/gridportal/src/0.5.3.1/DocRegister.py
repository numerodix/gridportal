from LunarcPage import LunarcPage

from HyperText.HTML import *

import Lap

class DocRegister(LunarcPage):

	def title(self):
		return 'Getting Started :: Registering with GRIDportal'

	def writeContent(self):
	
		wr = self.writeln
		w = self.write
		
		content = """
		
		<h2>Getting Started :: Registering with GRIDportal</h2>
		
		<table>
		<tr>
		<td valign=top>
		
		<p>To proceed with the instructions below, you will need a <span id="accs"><a href="DocCreateCert">signed certificate</a></span>. If you have received it by email, place it in %your_homedir%/.globus (on Windows 2000/XP this would be <span id="accs">c:\Documents and settings\(username)\.globus</span>, on Unix/MacOsX it is <span id="accs">~/.globus</span>).
		</p>
		
		<p>On the <span id="acc">Summary</span> page, click <span id="accb">Detect</span>. Your certificate request should now be detected correctly, as shown in the figure. You can click <span id="accb">View certificate</span> to inspect the contents of the certificate.
		</p>
		
		<p>On the <span id="acc">Register with GRIDportal</span> page, the <span id="accb">Register</span> button is now activated. Click <span id="accb">Register</span>.
		</p>
		
		<p>In the password dialog, enter the password you chose to use with your certificate and GRIDportal. Click <span id="accb">Proceed</span>.
		</p>
		
		<p>You will now be registered with GRIDportal. Please make a note of the username you have been assigned, it is of the form <span id="accs">domaincom_FullName</span>. You will use this to log into GRIDportal. Your password is the one you have already used to both create your certificate and to register.
		</p>
		
		<p>&nbsp;</p>
		<p><a href="DocToolkit"><< Return to Getting Started Main Menu</a></p>
		
		</td>
		<td>&nbsp;&nbsp;</td>
		<td valign=top>
		
		<img src="images/start/myproxy_detect01.png"><br><br>
		<img src="images/start/myproxy_reg01.png"><br><br>
		<img src="images/start/myproxy_reg02.png"><br><br>
		<img src="images/start/myproxy_reg03.png"><br>

		</td>
		
		</tr>
		</table>
		
		<p>&nbsp;</p>
		"""
		
		w(content)
