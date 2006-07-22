from LunarcPage import LunarcPage

from HyperText.HTML import *

import Lap

class DocTar(LunarcPage):

	def title(self):
		return 'Handling .tar.gz files with 7-Zip'

	def writeContent(self):
	
		wr = self.writeln
		w = self.write
		
		content = """
		
		<h2>Handling .tar.gz files with 7-Zip</h2>
		
		<table>
		<tr>
		<td valign=top>
		
		<p>The archives you download from this site are compressed with tar and gzip. This
		guide will explain how to open them.
		</p>
		
		<p>First, download 7-Zip from <a href="http://www.7-zip.org/" target="_new">
		http://www.7-zip.org/</a>. Click <span id="accb">Download Now</a>.
		</p>
			
		</td>
		<td>&nbsp;&nbsp;</td>
		<td valign=top>
		
		<img src="images/tar/tar01.png"><p>&nbsp;</p>
	
		</td>
		</tr>
		
		
		<tr>
		<td valign=top>
		
		<p>Next, choose a download site 
		</p>
			
		</td>
		<td>&nbsp;&nbsp;</td>
		<td valign=top>
		
		<img src="images/tar/tar02.png"><p>&nbsp;</p>
		
		</td>
		</tr>
				
		
		
		<tr>
		<td valign=top>
		
		<p>Now the download starts...
		</p>
			
		</td>
		<td>&nbsp;&nbsp;</td>
		<td valign=top>
		
		<img src="images/tar/tar03.png"><p>&nbsp;</p>
		
		</td>
		</tr>
		
		
		
		<tr>
		<td valign=top>
		
		<p>Once the download is complete, double-click on the file to start
		the installer. Click <span id="accb">Install</a>.
		</p>
			
		</td>
		<td>&nbsp;&nbsp;</td>
		<td valign=top>
		
		<img src="images/tar/tar04.png"><p>&nbsp;</p>
		
		</td>
		</tr>
		
		
		
		<tr>
		<td valign=top>
		
		<p>Now that you have 7-Zip installed, start the program and select 
		<span id="acc">Tools > Options...</span> from the top menu. You will see
		the dialog box shown on the right. Select gz and tar from the list and click
		<span id="accb">Apply</span>. This will associate files with .gz and .tar extensions
		in Windows Explorer to open with 7-Zip. Now you can double click on a .tar or .gz file 
		to open it.
		</p>
			
		</td>
		<td>&nbsp;&nbsp;</td>
		<td valign=top>
		
		<img src="images/tar/tar05.png"><p>&nbsp;</p>
		
		</td>
		</tr>
		
		
		
		<tr>
		<td valign=top>
		
		<p>Next, click on the <span id="acc">Plugins</span> tab, select 
		<span id="acc">7-Zip</span> and click <span id="acc">Options</span>. You will
		now see the dialog box on the right. These options control integration with Windows
		Explorer. Select <span id="acc">Integrate 7-Zip to shell context menu</span> and check
		<span id="acc">Extract Here</span> from the list. You will see why this is useful in
		the next step. Click <span id="accb">OK</span> to exit the dialog and click
		<span id="accb">OK</span> again to exit the parent dialog.
		</p>
			
		</td>
		<td>&nbsp;&nbsp;</td>
		<td valign=top>
		
		<img src="images/tar/tar06.png"><p>&nbsp;</p>
		
		</td>
		</tr>
		
		
		
		<tr>
		<td valign=top>
		
		<p>In Windows Explorer, you can now right-click on a .tar.gz file and choose Extract Here
		to extract the archive to the current directory.
		</p>
		
		<p><b>Note:</b> A .tar.gz archive is actually a <span id="acc">tar</span> archive inside a
		<span id="acc">gzip</span> archive. So when you extract the archive, this will happen in two
		steps, first extract the <span id="acc">gzip</span> archive, then the <span id="acc">tar</span>
		archive.
		</p>
			
		</td>
		<td>&nbsp;&nbsp;</td>
		<td valign=top>
		
		<img src="images/tar/tar07.png"><p>&nbsp;</p>
		
		</td>
		</tr>				
		
		
		
		</table>
		"""
		
		w(content)
