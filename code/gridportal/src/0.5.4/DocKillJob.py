from LunarcPage import LunarcPage

from HyperText.HTML import *

import Lap

class DocKillJob(LunarcPage):

	def title(self):
		return 'User''s Guide :: Kill a job run'

	def writeContent(self):
	
		wr = self.writeln
		w = self.write
		
		content = """
		
		<h2>User's Guide :: Kill a job run</h2>
		
		<table>
		<tr>
		<td valign=top>
		
		<p>If you wish to cancel a running job, choose <span id="acc">Manage > Jobs submitted
		to grid...</span>. Choose the job run you wish to cancel and click <span id="accb">Kill</span>.
		Your job will be stopped and the results of the job run (if any) will be lost. The job itself
		is not deleted.
		</p>
		
		<p>You will receive a status message on the job killing. Click <span id="accb">OK</span> in
		the menu. You are now returned to the list of jobs on the grid and you should see that the job
		run you killed is no longer in the list.
		</p>
		
		<p><b>Note:</b> A killed job run is automatically cleaned by the grid, so you don't have to do
		this manually.
		</p>		
		
		<p>&nbsp;</p>
		<p><a href="DocUserGuide"><< Return to User Guide Main Menu</a></p>		
		
		</td>
		<td>&nbsp;&nbsp;</td>
		<td valign=top>
		
		<img src="images/guide/kill_job01.png"><br>
		
		</td>
		
		</tr>
		</table>
		
		<img src="images/guide/kill_job02.png"><br>
		<img src="images/guide/kill_job03.png"><br>
		<img src="images/guide/kill_job04.png"><br>
		"""
		
		w(content)
