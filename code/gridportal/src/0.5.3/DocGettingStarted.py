from LunarcPage import LunarcPage

from HyperText.HTML import *

import Lap

class DocGettingStarted(LunarcPage):

	def title(self):
		return 'Getting Started'

	def writeContent(self):
	
		wr = self.writeln
		w = self.write
		
		content = """
		
		<h2>Getting Started</h2>
		
		<p><b>Contents</b>
		</p>
		
		<ol>
		<li><a href="DocIntroduction">Introduction</a>
		<li><a href="DocUsingToolkit">Using the toolkit</a>
		<li><a href="DocCreateCert">Creating a certificate request</a>
		<li><a href="DocRegister">Registering with GRIDportal</a>
		<li><a href="DocVirtualOrganisation">Virtual Organisations</a>
		</ol>
		
		<p>&nbsp;</p>
		
		<p><b>Quick start guide</b>
		</p>
		In order to get started with GRIDportal, you will need to use the GridPortalToolkit. Follow these steps to install the toolkit:
		<ol>
		<li>Install <span id="accs"><a href="http://www.java.com/en/download/">Java Runtime Environment (JRE) 1.4.2</a></span> (or later)
		<li>Download the <span id="accs"><a href="http://gridportal.dynalias.org/gptoolkit/www/files/gptoolkit.zip">GridPortalToolkit</a></span> and unzip the file.
		</ol>
		
		The toolkit is now ready for use. To start it:
		<ol>
		<li>From the unzipped files, execute <span id="accs">run.bat</span> (for Windows) or <span id="accs">run.sh</span> (for Unix/MacOsX).
		</ol>
		
		Now, using the toolkit, proceed with these instructions to obtain access to GRIDportal:
		<ol>
		<li>On the page <span id="acc">Create certificate request</span>, fill in the form and click <span id="accb">Create</span> and decide on a password you will use with GRIDportal.
		<li>On the <span id="acc">Summary</span> page, click <span id="accb">Detect</span>.
		<li>On the page <span id="acc">Mail signing request</span>, follow the instructions and use your standard email client to send the email as instructed.
		</ol>
		
		You will receive your signed certificate by email within a few days. Once you have it, place it in %your_homedir%/.globus (on Windows 2000/XP this would be <span id="accs">c:\Documents and settings\(username)\.globus</span>, on Unix/MacOsX it is <span id="accs">~/.globus</span>). Then, proceed with these instructions:
		<ol>
		<li>On the <span id="acc">Summary</span> page, click <span id="accb">Detect</span>. (Your certificate should now be detected successfully.)
		<li>On the page <span id="acc">Register with GRIDportal</span>, click <span id="accb">Register</span> and enter the password you chose before. (When this completes successfully, take a note of the username you have been given, it is of the form <span id="accs">domaincom_FullName</span>.)
		</ol>
		
		You can now log into GRIDportal with the username and password mentioned above. To be able to submit jobs, you also have to join a <span id="accs">Virtual Organisation</span>. See list of Virtual Organisations on <span id="accs"><a href="http://www.nordugrid.org/NorduGridVO/index.php#volist">this page</a></span>.
		
		<p>&nbsp;</p>
		"""
		
		w(content)
