from LunarcPage import LunarcPage

from HyperText.HTML import *

import Lap

class DocCleanJob(LunarcPage):

	def title(self):
		return 'User''s Guide :: Clean a job run'

	def writeContent(self):
	
		wr = self.writeln
		w = self.write
		
		content = """
		
		<h2>User's Guide :: Clean a job run</h2>
		
		<table>
		<tr>
		<td valign=top>
		
		<p>If your job exceeds the time limit allocated for it (recall you set this yourself when
		creating the job) or you wait too long before collecting the results (or something else goes wrong,
		like an error on the grid), its execution will be
		stopped and the job run will be marked as either <span id="accs">DELETED</span> or 
		<span id="accs">FAILED</span>. This job run is now of
		no use to anyone, the results of the run (if any) are lost and the only reason the job is not
		deleted by the grid automatically is so that you can find out what happened to it.
		</p>
		
		<p>Another reason to clean a job could be if you do not wish to retrieve the results of the
		job run for some reason, then you can just clean the job after it's finished (to terminate a job
		even before it's finished, kill the job run).
		</p>		
		
		<p>To clean up this job, select <span id="acc">Manage > Jobs submitted
		to grid...</span>, select the job run marked <span id="accs">DELETED</span>
		or <span id="accs">FAILED</span> and click 
		<span id="accb">Clean</span>. You will receive a status message on the job cleaning, click
		<span id="accb">OK</span> in the menu. You are now returned to the list of jobs on the grid
		and you should see that your job is no longer in the list.
		</p>
		
		<p>&nbsp;</p>
		<p><a href="DocUserGuide"><< Return to User Guide Main Menu</a></p>		
		
		</td>
		<td>&nbsp;&nbsp;</td>
		<td valign=top>
		
		<img src="images/guide/clean_job01.png"><br>
		
		</td>
		
		</tr>
		</table>
		
		<img src="images/guide/clean_job02.png"><br>
		<img src="images/guide/clean_job03.png"><br>
		<img src="images/guide/clean_job04.png"><br>
		"""
		
		w(content)
