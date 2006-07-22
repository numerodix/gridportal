from SecurePage import SecurePage
from time import *

import os
import sys
import pwd
import string
import pickle

import pyARC

import Lap
import LapWeb
import LapPovRay

class ManageGridJobPage(SecurePage):

	def writeHead(self):
	
		if self.session().hasValue("grid_job_metarefresh"):
			self.writeln("""<META HTTP-EQUIV="REFRESH" CONTENT="0;ManageGridJobPage">""")
			self.session().delValue("grid_job_metarefresh")

		SecurePage.writeHead(self)


	def writeContent(self):
		
		if self.session().hasValue("grid_job_status"):

			# Show any form message boxes
			
			if self.session().value("grid_job_status")<>"":
				LapWeb.messageBox(self, self.session().value("grid_job_status"), "Message", "ManageGridJobPage")

			self.session().delValue("grid_job_status")

		elif self.session().hasValue("grid_job_wait"):

			message = self.session().value("grid_job_wait")
			self.session().delValue("grid_job_wait")

			LapWeb.pleaseWaitBox(self, message)
			
		else:
			
			# Get user information
		
			user = Lap.User(self.session().value('authenticated_user'))
			userDir = user.getDir();
			
			# Get the ARC user interface
			
			ui = pyARC.Ui(user)
			
			# Get a job status list
			
			jobs = ui.jobStatus()
			
			if jobs==None:
				
				LapWeb.messageBox(self, "grid-proxy is about to expire please create a new proxy.", "Manage running jobs")
			
			elif len(jobs)==0:
				
				# No job dirs exists => no jobs to manage
				
				LapWeb.messageBox(self, "No jobs to manage", "Get jobs")
				
			else:
				
				# Create form for managing jobs
				
				form = LapWeb.Form(self, "frmJobManager", "", "Manage GRID jobs", 50, len(jobs)+1, 6)
				form.setTableLayout(True)
				
				form.setAction("ManageGridJobPage")
				
				row = 0
				
				form.addNormalText("JobID", row, 1)
				form.addNormalText("JobName", row, 3)
				form.addNormalText("Status", row, 5)
				
				row = row + 1
				
				for key in jobs.keys():
					form.addRadio("", "chkJob", "%s;%s" % (jobs[key]["name"],key), row, 0)
					form.addNormalText(key, row, 1)
					form.addNormalText("", row, 2)
					form.addNormalText(jobs[key]["name"], row, 3)
					form.addNormalText("", row, 4)
					form.addNormalText(jobs[key]["status"], row, 5)
					
					row = row + 1
					
				form.addFormButton("Get", "_action_getJob")
				form.addFormButton("Kill", "_action_killJob")
				form.addFormButton("Clean", "_action_cleanJob")
				
				form.setHaveSubmit(False)
					
				form.render()
			
	def getJob(self):
		
		if self.request().hasValue("chkJob"):
			
			# Get selected jobname
			
			jobName, jobID = string.split(self.request().value("chkJob"),";")
			
			# Get user dir

			self.pleaseWait("Downloading job...")
			
			user = Lap.User(self.session().value('authenticated_user'))
			userDir = user.getDir();
			ui = pyARC.Ui(user)
			
			downloadDir = os.path.join(userDir, "job_%s" % jobName)
			try:
				os.mkdir(downloadDir)
			except:
				pass
			
			result = ui.get(jobID, downloadDir)
			
			self.setFormStatus(result[0])
			
		else:
			self.setFormStatus("A job must be selected.")
			self.writeBody()
		
		
	def killJob(self):

		if self.request().hasValue("chkJob"):
			
			# Get selected jobname
			
			jobName, jobID = string.split(self.request().value("chkJob"),";")
			
			# Get user dir

			self.pleaseWait("Killing job...")
			
			user = Lap.User(self.session().value('authenticated_user'))
			userDir = user.getDir();
			ui = pyARC.Ui(user)

			result = ui.kill(jobID)
			
			self.setFormStatus(result[0])
			
		else:
			self.setFormStatus("A job must be selected.")
			self.writeBody()
	
	def cleanJob(self):

		if self.request().hasValue("chkJob"):
			
			# Get selected jobname
			
			jobName, jobID = string.split(self.request().value("chkJob"),";")
			
			# Get user dir

			self.pleaseWait("Cleaning job...")
			
			user = Lap.User(self.session().value('authenticated_user'))
			userDir = user.getDir();
			ui = pyARC.Ui(user)

			result = ui.clean(jobID)
			
			self.setFormStatus(result[0])
						
		else:
			self.setFormStatus("A job must be selected.")
			self.writeBody()
			
	def setFormStatus(self, status):
		self.session().setValue("grid_job_status", status)

	def pleaseWait(self, message):
		self.session().setValue("grid_job_wait", message)
		self.session().setValue("grid_job_metarefresh", "")
		self.writeHead()
		self.writeBody()
		self.response().flush()

		
	def actions(self):
		return SecurePage.actions(self) + ["getJob",
			 "killJob",
			 "cleanJob"]
