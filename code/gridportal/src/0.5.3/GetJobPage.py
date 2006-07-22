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

class GetJobPage(SecurePage):

	def writeContent(self):
		
		if self.session().hasValue("getjob_status"):

			# Show any form message boxes
			
			if self.session().value("getjob_status")<>"":
				Lap.messageBox(self, self.session().value("getjob_status"), "Message", "ManageJobPage")

			self.session().delValue("getjob_status")
			
		elif self.session().hasValue("getjob_confirm"):
			
			# Show form confirmation dialogs
			
			Lap.messageBoxYesNo(self,
								self.session().value("getjob_confirm"),
								self.session().value("getjob_confirm_title"),
								"_action_deleteJobYes",
								"_action_deleteJobNo"
								)
			
			self.session().delValue("getjob_confirm")
			self.session().delValue("getjob_confirm_title")
			self.session().delValue("getjob_confirm_no_page")
			self.session().delValue("getjob_confirm_yes_page")
			
		else:		
		
			wl = self.writeln
			w = self.write
			
			user = Lap.User(self.session().value('authenticated_user'))
			userDir = user.getDir();
			
			ui = pyARC.Ui(user)
			
			jobs = ui.jobStatus()
			
			if len(jobs)==0:
				
				# No job dirs exists => no jobs to manage
				
				Lap.messageBox(self, "No jobs to get", "Get jobs")
			else:
				
				# Create form for managing jobs
				
				form = LapWeb.Form(self, "frmJobManager", "", "Get jobs")
				
				form.setAction("ManageJobPage")
				
				for job in jobs:
					form.addRadio(job["name"] + job["ID"], "chkJob", key)
					
				form.addFormButton("Get", "_action_getJob")
				form.addFormButton("Kill", "_action_killJob")
				form.addFormButton("Clean", "_action_cleanJob")
				
				form.setHaveSubmit(False)
					
				form.render()
			
	def getJob(self):
		
		if self.request().hasValue("chkJob"):
			
			# Get selected jobname
			
			jobID = self.request().value("chkJob")
			
			# Get user dir
			
			user = Lap.User(self.session().value('authenticated_user'))
			userDir = user.getDir();
		else:
			self.setFormStatus("A job must be selected.")
			self.writeBody()
		
		
	def killJob(self):
		pass
	
	def cleanJob(self):

		if self.request().hasValue("chkJob"):
			
			# Get selected jobname
			
			jobID = self.request().value("chkJob")
			
			# Get user dir
			
			user = Lap.User(self.session().value('authenticated_user'))
			userDir = user.getDir();
			jobDir = os.path.join(userDir, "job_%s" % jobName)
			
			# Read the job task 
			
			taskFile = file(os.path.join(jobDir,"job.task"), "r")
			task = pickle.load(taskFile)
			taskFile.close()
			
			ARC = pyARC.Ui(user)
			result = ARC.submit(os.path.join(jobDir,"job.xrsl"))
			
			self.setFormStatus(result[0])
			self.writeBody()
						
		else:
			self.setFormStatus("A job must be selected.")
			self.writeBody()
			
	def deleteJob(self):
		if self.request().hasValue("chkJob"):
			self.confirm("Are you sure?", "Delete job", "", "")
			self.session().setValue("getjob_deletejob", self.request().value("chkJob"))
			self.writeBody()
		else:
			self.setFormStatus("A job must be selected.")
			self.writeBody()
		
	def deleteJobYes(self):
		
		if self.session().hasValue("getjob_deletejob"):
			
			jobName = self.session().value("getjob_deletejob")
			
			self.session().delValue("getjob_deletejob")
			
			if jobName == "":
				return
			
			# Get user dir
			
			user = Lap.User(self.session().value('authenticated_user'))
			userDir = user.getDir();
			jobDir = os.path.join(userDir, "job_%s" % jobName)
			
			# Delete job directory
			
			try:
				for filename in os.listdir(jobDir):
					os.remove(os.path.join(jobDir, filename))
				os.rmdir(jobDir)
			except:
				pass
			
			self.writeBody()
			
			
	def deleteJobNo(self):
		if self.session().hasValue("getjob_deletejob"):
			self.session().delValue("getjob_deletejob")
		self.writeBody()
	
	def setFormStatus(self, status):
		self.session().setValue("getjob_status", status)
		
	def confirm(self, question, title, yesPage, noPage):
		self.session().setValue("getjob_confirm", question)
		self.session().setValue("getjob_confirm_title", title)
		self.session().setValue("getjob_confirm_yes_page", yesPage)
		self.session().setValue("getjob_confirm_no_page", noPage)
	
	def actions(self):
		return SecurePage.actions(self) + ["getJobFiles",
			 "killJob",
			 "cleanJob"]
