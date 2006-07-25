from LunarcPage import LunarcPage

from HyperText.HTML import *

import Lap

class DocUsingToolkit(LunarcPage):

	def title(self):
		return 'Getting Started :: Using the toolkit'

	def writeContent(self):
	
		wr = self.writeln
		w = self.write
		
		content = """
		
		<h2>Getting Started :: Using the toolkit</h2>
		
		<table>
		<tr>
		<td valign=top>
		
		<p><b>Installing Java Runtime Environment (JRE)</b>
		</p>
		
		<p>Before you can run GridPortalToolkit, you will have to install the Java Runtime Environment. <span id="accs"><a href="http://www.java.com/en/download/">Download it</a></span> and follow the instructions to install.
		</p>
		
		<p>After downloading, the java installer should appear. Accept the licence agreement and click <span id="accb">Next</span>.
		</p>
		
		<p>On the next screen, choose <span id="accs">Typical</span> and click <span id="accb">Next</span>. Then wait for the install to complete.
		</p>
		
		</td>
		<td>&nbsp;&nbsp;</td>
		<td valign=top>
		
		<img src="images/start/java_download01.png"><br><br>
		<img src="images/start/java_download02.png"><br><br>
		<img src="images/start/java_install01.png"><br><br>
		<img src="images/start/java_install02.png"><br><br>
		<img src="images/start/java_install03.png"><br><br>
		<img src="images/start/java_install04.png"><br>
		</td>
		
		</tr>
		</table>
		
		
		
		<p>&nbsp;</p>
		
		<table>
		<tr>
		<td valign=top>
		
		<p><b>Installing GridPortalToolkit</b>
		</p>
		
		<p>First, <span id="accs"><a href="https://sourceforge.net/project/showfiles.php?group_id=171912&package_id=198107">download GridPortalToolkit</a></span>, the file is called <span id="accs">gptoolkit.zip</span>.
		</p>
		
		<p>Unzip file file with <span id="accs"><a href="DocTar">7-zip</a></span>.
		</p>
		
		</td>
		<td>&nbsp;&nbsp;</td>
		<td valign=top>
		
		<img src="images/start/gptoolkit_download01.png"><br>
		
		</td>
		
		</tr>
		</table>
		
		
		
		<p>&nbsp;</p>
		
		<table>
		<tr>
		<td valign=top>
		
		<p><b>Running GridPortalToolkit</b>
		</p>
		
		<p>Now, open the <span id="acc">gptoolkit</span> directory and execute the file <span id="accs">run.bat</span>. GridPortalToolkit should now appear on your screen.
		</p>
		
		<p>&nbsp;</p>
		<p><a href="DocGettingStarted"><< Return to Getting Started Main
Menu</a></p>
		
		</td>
		<td>&nbsp;&nbsp;</td>
		<td valign=top>
		
		<img src="images/start/gptoolkit_run01.png"><br><br>
		<img src="images/start/gptoolkit_run02.png"><br>

		</td>
		
		</tr>
		</table>
		
		<p>&nbsp;</p>
		"""
		
		w(content)
