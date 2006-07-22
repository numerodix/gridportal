from LunarcPage import LunarcPage

from HyperText.HTML import *

import Lap

class DocEditJob(LunarcPage):

	def title(self):
		return 'User''s Guide :: Edit a job'

	def writeContent(self):
	
		wr = self.writeln
		w = self.write
		
		content = """
		
		<h2>User's Guide :: Edit a job</h2>
		
		<table>
		<tr>
		<td valign=top>		
		
		<p>Choose <span id="acc">Manage > Local jobs...</span> to list the jobs you have created. Select
		the job you wish to edit and click <span id="accb">Edit</span>. Now make the changes you wish to
		make. You have the option to replace the input file(s) for your job if you wish to do that.
		</p>
		
		<p>When done, click <span id="accb">Modify</span> when done. You will receive a status message
		about the job editing.
		</p>
		
		<p>&nbsp;</p>
		<p><a href="DocUserGuide"><< Return to User Guide Main Menu</a></p>		
		
		</td>
		<td>&nbsp;&nbsp;</td>
		<td valign=top>
		
		<img src="images/guide/edit_job01.png"><br>
		<img src="images/guide/edit_job02.png"><br>
		<img src="images/guide/edit_job03.png"><br>
		<img src="images/guide/edit_job04.png"><br>
		
		</td>
		
		</tr>
		</table>
		"""
		
		w(content)
