from LunarcPage import LunarcPage

from HyperText.HTML import *

import Lap

class DocUserGuide(LunarcPage):

	def title(self):
		return 'User''s guide'

	def writeContent(self):
	
		wr = self.writeln
		w = self.write
		
		content = """
		
		<h2>User's Guide</h2>
		
		<p><b>Working with jobs</b>
		</p>		
		
		<ul>
		<li><a href="DocCreateJob">Create a job</a>
		<li><a href="DocEditJob">Edit a job</a>
		<li><a href="DocDeleteJob">Delete a job</a>
		<li><a href="DocSubmitJob">Submit a job</a>
		<li><a href="DocKillJob">Kill a job run</a>
		<li><a href="DocCleanJob">Clean a job run</a>
		<li><a href="DocGetJob">Retrieve results from a job run</a>
		</ul>
		
		<p><b>Key concepts</b>
		</p>
		
		<dt><span id="acc">grid</span></dt>
		<dd>The grid is the computational site where your job is being sent for
		processing. Storage on the grid is <i>temporary</i> to the time
		required to complete the job, after that your job run will be deleted
		if you fail to collect it within 24 hours.</dd>
		
		<dl>
		<dt><span id="acc">job</span></dt>
		<dd>A job describes <i>how</i> to carry out the job.
		A job is defined by the user for a specific
		application. The job consists of a range of settings available for that 
		application, along with a few settings common to all jobs (like cpu time,
		email address etc). A more precise name for a job would be
		<span id="acc">job definition</span>, because a job is a static entity
		which describes how to carry out the computation.
		A job is stored on the <span id="acc">server</span>.</dd>
		
		<dt><span id="acc">job run</span></dt>
		<dd>A job run <i>is</i> the job being carried out.
		A job run is a job which has been submitted to the grid. The simple
		difference between a job and a job run is that a job run <i>runs</i>
		while a job is static. A job run is stored somewhere on the 
		<span id="acc">grid</span> and its lifetime is limited to the time it
		takes to run the job. A job run consists of the results of the job, the
		output of the job.</dd>
		
		<dt><span id="acc">server</span></dt>
		<dd>The server is a machine closely connected to the 
		<span id="acc">grid</span>. It is where this web site lives and where
		your jobs live. Storage on the server is <i>permanent</i>, which means
		your jobs will be stored until you choose to delete them.</dd>
		</dl>
		
		<p><b>Job status explained</b>
		</p>		
		
		<dt><span id="accs">ACCEPTED</span></dt>
		<dd>job submitted but not yet processed</dd>
		
		<dt><span id="accs">PREPARING</span></dt>
		<dd>input files are being retrieved</dd>
		
		<dt><span id="accs">SUBMITTING</span></dt>
		<dd>interaction with grid ongoing</dd>
		
		<dt><span id="accs">INLRMS: Q</span></dt>
		<dd>job is queued by grid</dd>
		
		<dt><span id="accs">INLRMS: R</span></dt>
		<dd>job is running</dd>
		
		<dt><span id="accs">FINISHING</span></dt>
		<dd>output files are being transferred</dd>
		
		<dt><span id="accs">FINISHED</span></dt>
		<dd>job is finished</dd>
		
		<dt><span id="accs">CANCELING</span></dt>
		<dd>job is being canceled</dd>
		
		<dt><span id="accs">DELETED</span></dt>
		<dd>job is removed due to expiration time</dd>
		
		<p>&nbsp;</p>
		"""
		
		w(content)
