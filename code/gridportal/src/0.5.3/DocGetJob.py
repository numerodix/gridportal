from LunarcPage import LunarcPage

from HyperText.HTML import *

import Lap

class DocGetJob(LunarcPage):

	def title(self):
		return 'User''s Guide :: Retrieve results from a job run'

	def writeContent(self):
	
		wr = self.writeln
		w = self.write
		
		content = """
		
		<h2>User's Guide :: Retrieve results from a job run</h2>
		
		<table>
		<tr>
		<td valign=top>
		
		<p>Choose <span id="acc">Manage > Monitor running jobs</span> to list the jobs you have 
		submitted. If your job run is marked <span id="accs">FINISHED</span>, you can retrieve
		the results of the job run. Choose <span id="acc">Manage > Jobs submitted to 
		grid...</span>, select the job run marked <span id="accs">FINISHED</span> and click
		<span id="accb">Get</span>. The results of your job will now be transferred to the server
		and the job run is deleted from the grid.
		</p>
		
		<p>Choose <span id="acc">Manage > Local jobs...</span> to list the jobs you have created
		and select the you just downloaded. Click <span id="accb">View files</span>. If you
		have run the job more than once, the results of every job run will be displayed here.
		For every job run, you can view the files or delete the job results.
		</p>
		
		<p>Choose the job run of your choice and click <span id="accb">View</span>. Now you
		get a list of all the files which make up the results of your job run. The special
		directory <span id="acc">gmlog</span> contains specifics about your job run and is
		used for debugging, so you can safely ignore it unless something went wrong. 
		On this screen, you may wish to view the individual files or download them. You can 
		also download this entire directory as a tar.gz (compressed archive) and view the 
		files on your computer.
		Simply click <span id="accb">Download all (tar.gz)</span> and save the file on your
		computer.
		</p>
		
		<p><b>Note:</b> If the results of your job run contain big files (10MB+), it may 
		take a long time to transfer those files (or all of the files in a tar.gz archive) 
		to your computer. Unfortunately, this kind of file transfer is unsuited for big files
		so the transfer may even fail if the file to be transferred is very big. This depends
		greatly on the speed of your internet connection.
		</p>
		
		<p>&nbsp;</p>
		<p><a href="DocUserGuide"><< Return to User Guide Main Menu</a></p>		
		
		</td>
		<td>&nbsp;&nbsp;</td>
		<td valign=top>
		
		<img src="images/guide/get_job01.png"><br>
		<img src="images/guide/get_job02.png"><br>
		<img src="images/guide/get_job03.png"><br>
		<img src="images/guide/get_job04.png"><br>
		<img src="images/guide/get_job05.png"><br>
		<img src="images/guide/get_job06.png"><br>
		<img src="images/guide/get_job07.png"><br>
		<img src="images/guide/get_job08.png"><br>
		
		</td>
		
		</tr>
		</table>
		"""
		
		w(content)
