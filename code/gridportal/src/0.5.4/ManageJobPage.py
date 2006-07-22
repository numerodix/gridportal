from SecurePage import SecurePage
from time import *
import os
import sys
import pwd
import string
import pickle
import shutil
import time

import pyARC
from conf_main import *

import Lap
import LapWeb
import LapUtils

class ManageJobPage(SecurePage):

	def writeHead(self):

		if self.session().hasValue("managejob_metarefresh"):
			
			self.writeln("""<META HTTP-EQUIV="REFRESH" CONTENT="0;ManageJobPage">""")
			self.session().delValue("managejob_metarefresh")

		SecurePage.writeHead(self)

	def writeContent(self):
		
		if self.session().hasValue("managejob_status"):

			# Show any form message boxes
			
			if self.session().value("managejob_status")<>"":
				LapWeb.messageBox(self, self.session().value("managejob_status"), "Message", "ManageJobPage")

			self.session().delValue("managejob_status")

		elif self.session().hasValue("managejob_wait"):

			message = self.session().value("managejob_wait")
			self.session().delValue("managejob_wait")

			LapWeb.pleaseWaitBox(self, message)			
			
		elif self.session().hasValue("managejob_confirm"):
			
			# Show form confirmation dialogs
			
			LapWeb.messageBoxYesNo(self,
								self.session().value("managejob_confirm"),
								self.session().value("managejob_confirm_title"),
								"_action_deleteJobYes",
								"_action_deleteJobNo"
								)
			
			self.session().delValue("managejob_confirm")
			self.session().delValue("managejob_confirm_title")
			self.session().delValue("managejob_confirm_no_page")
			self.session().delValue("managejob_confirm_yes_page")
			
		else:		
		
			wl = self.writeln
			w = self.write
			
			user = Lap.User(self.session().value('authenticated_user'))
			userDir = user.getDir();
			
			# Check for job directories in user dir
			
			jobCount = 0
			jobs = []
			
			for entry in os.listdir(userDir):
				if os.path.isdir(os.path.join(userDir,entry)):
					if entry[0:4] == "job_":
						jobName = entry[4:]
						jobCount = jobCount + 1
						jobs.append(jobName)
						
			if len(jobs)==0:
				
				# No job dirs exists => no jobs to manage
				
				LapWeb.messageBox(self, "No jobs to manage", "Manage jobs")
			else:
				
				# Create form for managing jobs
				
				form = LapWeb.Form(self, "frmJobManager", "", "Manage jobs")
				
				form.setAction("ManageJobPage")
				
				for job in jobs:
					form.addRadio(job, "chkJob", job)
					
				form.addFormButton("Edit", "_action_editJob")
				form.addFormButton("View files", "_action_viewJobFiles")
				form.addFormButton("Submit", "_action_submitJob")
				form.addFormButton("Delete", "_action_deleteJob")
				
				form.setHaveSubmit(False)
					
				form.render()
			
	def editJob(self):
		
		if self.request().hasValue("chkJob"):
			
			# Get selected jobname
			
			jobName = self.request().value("chkJob")
			
			# Get user dir
			
			user = Lap.User(self.session().value('authenticated_user'))
			userDir = user.getDir();
			
			# Set session variable edit job to tell the job page
			# that we want to edit the job instead of creating and
			# assign the job name as value

			self.session().setValue("editjob", jobName)
			
			jobDir = os.path.join(userDir, "job_%s" % jobName)
			
			# Read the job task 
			
			taskFile = file(os.path.join(jobDir,"job.task"), "r")
			task = pickle.load(taskFile)
			taskFile.close()
			
			# Forward to the requested edit page
			
			print task.getTaskEditPage()
			self.forward(task.getTaskEditPage())				
		else:
			self.setFormStatus("A job must be selected.")
			self.writeBody()
		
		
	def viewJobFiles(self):

		if self.request().hasValue("chkJob"):
			
			# Get selected jobname
			
			jobName = self.request().value("chkJob")
			
			# Get user dir
			
			user = Lap.User(self.session().value('authenticated_user'))
			userDir = user.getDir();
			
			self.session().setValue("viewfiles_mode", "session")
			self.session().setValue("viewfiles_jobname", jobName)
					
			self.forward("ViewFilesPage")

		else:
			self.setFormStatus("A job must be selected.")
			self.writeBody()
	
	def submitJob(self):

		if self.request().hasValue("chkJob"):
			
			# Get selected jobname
			
			jobName = self.request().value("chkJob")
			
			# Get user dir
			
			user = Lap.User(self.session().value('authenticated_user'))
			userDir = user.getDir();
			jobDir = os.path.join(userDir, "job_%s" % jobName)
			
			# Read the job task 
			
			taskFile = file(os.path.join(jobDir,"job.task"), "r")
			task = pickle.load(taskFile)
			taskFile.close()

			self.pleaseWait("Submitting job...")
			
			ARC = pyARC.Ui(user)
			result = ARC.submit(os.path.join(jobDir,"job.xrsl"))

			self.setFormStatus(result[0])
		else:
			self.setFormStatus("A job must be selected.")
			self.writeBody()
			
	def deleteJob(self):
		if self.request().hasValue("chkJob"):
			self.confirm("Are you sure?", "Delete job", "", "")
			self.session().setValue("managejob_deletejob", self.request().value("chkJob"))
			self.writeBody()
		else:
			self.setFormStatus("A job must be selected.")
			self.writeBody()
		
	def deleteJobYes(self):
		
		if self.session().hasValue("managejob_deletejob"):
			
			jobName = self.session().value("managejob_deletejob")
			
			self.session().delValue("managejob_deletejob")
			
			if jobName == "":
				return
			
			# Get user dir
			
			user = Lap.User(self.session().value('authenticated_user'))
			userDir = user.getDir();
			jobDir = os.path.join(userDir, "job_%s" % jobName)
			
			# Delete job directory
			
			shutil.rmtree(jobDir, True)
			
			self.writeBody()
			
			
	def deleteJobNo(self):
		if self.session().hasValue("managejob_deletejob"):
			self.session().delValue("managejob_deletejob")
		self.writeBody()
	
	def setFormStatus(self, status):
		self.session().setValue("managejob_status", status)

	def pleaseWait(self, message):
		self.session().setValue("managejob_wait", message)
		self.session().setValue("managejob_metarefresh", "")
		self.writeHead()
		self.writeBody()
		self.response().flush()

		
	def confirm(self, question, title, yesPage, noPage):
		self.session().setValue("managejob_confirm", question)
		self.session().setValue("managejob_confirm_title", title)
		self.session().setValue("managejob_confirm_yes_page", yesPage)
		self.session().setValue("managejob_confirm_no_page", noPage)
	
	def actions(self):
		return SecurePage.actions(self) + ["editJob",
			 "viewJobFiles",
			 "submitJob",
			 "deleteJob",
			 "deleteJobYes",
			 "deleteJobNo"]
