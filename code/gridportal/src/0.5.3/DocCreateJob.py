from LunarcPage import LunarcPage

from HyperText.HTML import *

import Lap

class DocCreateJob(LunarcPage):

	def title(self):
		return 'User''s Guide :: Create a job'

	def writeContent(self):
	
		wr = self.writeln
		w = self.write
		
		content = """
		
		<h2>User's Guide :: Create a job</h2>
		
		<table>
		<tr>
		<td valign=top>

		<p>Use the <span id="acc">Create</span> menu to create a job for the application you intend to run. 
		To help you fill in the form, the field labels are clickable links which point you to
		descriptions for each field. You can change the default job name to a different one but <b>be
		advised that creating a job with the same name as an existing job will overwrite the old one
		without notice.</b> Use the file upload field(s) to upload any input files your job
		may require. You have the option of entering your email address on the form, this is used to
		send you updates about the status of your job later when you submit the job to the grid.
		Once you submit the form, all information about this job will be stored on the
		server.
		</p>
		
	
		<p>You will now receive a status message on the job creation. Unless anything goes wrong,
		it will be created successfully. Click <span id="accb">OK</span> to proceed to the job
		list. Your new job should appear there.
		</p>
		
		<p><b>Note:</b> Once a job is created, it can be submitted as many times as you like.
		</p>
		
		<p>&nbsp;</p>
		<p><a href="DocUserGuide"><< Return to User Guide Main Menu</a></p>
		
		</td>
		<td>&nbsp;&nbsp;</td>
		<td valign=top>
		
		<img src="images/guide/create_job01.png"><br>
		<img src="images/guide/create_job02.png"><br>
		<img src="images/guide/create_job03.png"><br>
		<img src="images/guide/create_job04.png"><br>
		
		</td>
		
		</tr>
		</table>
		"""
		
		w(content)
