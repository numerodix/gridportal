from LunarcPage import LunarcPage

from HyperText.HTML import *

import Lap

class DocSubmitJob(LunarcPage):

	def title(self):
		return 'User''s Guide :: Submit a job'

	def writeContent(self):
	
		wr = self.writeln
		w = self.write
		
		content = """
		
		<h2>User's Guide :: Submit a job</h2>
		
		<table>
		<tr>
		<td valign=top>		
		
		<p>Choose <span id="acc">Manage > Local jobs...</span> to list the jobs you have created. Select
		the job you wish to submit and click <span id="accb">Submit</span>. You will now receive a status
		message about your submission. If the submission was successful, your job is now queued for 
		execution on the grid.
		</p>
		
		<p>Now that your job has been submitted successfully, you may wish to keep an eye on it.
		Select <span id="acc">Manage > Monitor running jobs</span> to list the jobs that have been
		submitted. Your job may not appear there immediately, because there often is a synchronization
		problem between the server and the grid, so if your job does not show up, select
		<span id="acc">Manage > Synchronize job list</span>. This takes you straight to the job 
		status page and now you should see your job in the list. (If your job is still missing, try
		synchronizing again, alternatively just wait a minute or two and it should appear.)
		</p>
		
		<p><b>Note:</b> Your job is now running on the grid and you can check up on it any time you like. 
		If you chose to receive email updates, you will receive two of them for every job. Once when 
		the job is accepted for execution by the grid and once when it has finished or has been 
		terminated.
		</p>
		
		<p><b>Note:</b> Once your job has completed, you must collect the results within 24 hours.
		If you fail to get the results by then, they will be lost and the job run will be marked
		<span id="accs">DELETED</span>. (The job itself will not be deleted, just the results
		of the job run.)
		For this reason, it's very handy to use email notification.
		</p>
		
		<p>&nbsp;</p>
		<p><a href="DocUserGuide"><< Return to User Guide Main Menu</a></p>		
		
		</td>
		<td>&nbsp;&nbsp;</td>
		<td valign=top>
		
		<img src="images/guide/submit_job01.png"><br>
		<img src="images/guide/submit_job02.png"><br>
		<img src="images/guide/submit_job03.png"><br>
		<img src="images/guide/submit_job04.png"><br>
		<img src="images/guide/submit_job05.png"><br>
		<img src="images/guide/submit_job06.png"><br>
		
		</td>
		
		</tr>
		</table>
		"""
		
		w(content)
