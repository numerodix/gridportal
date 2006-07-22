from LunarcPage import LunarcPage

from HyperText.HTML import *

import Lap

class DocDeleteJob(LunarcPage):

	def title(self):
		return 'User''s Guide :: Delete a job'

	def writeContent(self):
	
		wr = self.writeln
		w = self.write
		
		content = """
		
		<h2>User's Guide :: Delete a job</h2>
		
		<table>
		<tr>
		<td valign=top>		
		
		<p>Choose <span id="acc">Manage > Local jobs...</span> to list the jobs you have created. Select
		the job you wish to delete and click <span id="accb">Delete</span>. On the next screen, click
		<span id="accb">Yes</span> to confirm. Your job will be deleted from
		the server, <b>along with the results from any runs it has completed</b>.
		</p>
		
		<p>You are now taken back to the job list and you can see your job is no longer listed.
		</p>
		
		<p>&nbsp;</p>
		<p><a href="DocUserGuide"><< Return to User Guide Main Menu</a></p>		
		
		</td>
		<td>&nbsp;&nbsp;</td>
		<td valign=top>
		
		<img src="images/guide/delete_job01.png"><br>
		<img src="images/guide/delete_job02.png"><br>
		<img src="images/guide/delete_job03.png"><br>
		<img src="images/guide/delete_job04.png"><br>
		
		</td>
		
		</tr>
		</table>
		"""
		
		w(content)
