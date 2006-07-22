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
import LapUtils

class JobPage(SecurePage):

	def writeContent(self):
	
		LapUtils.log.msg("JobPage", "writeContent")

		if self.session().hasValue("jobpage_status"):
		
			LapUtils.log.msg("JobPage", "writeContent", "Show status message")

			# -----------------------------------
			# Show any form messages
			# -----------------------------------
			
			if self.session().value("jobpage_status")<>"":
				LapWeb.messageBox(self, self.session().value("jobpage_status"), "Message", self.session().value("jobpage_return_page"))

			self.session().delValue("jobpage_status")
			self.session().delValue("jobpage_return_page")
		else:

			# -----------------------------------
			# Create job form definition here 
			# -----------------------------------

			if self.session().hasValue("editjob"):
			
				LapUtils.log.msg("JobPage", "writeContent", "Edit job")
				
				# Ok. He wants to edit the job
				
				# Get neccessary info
				
				jobName = self.session().value("editjob")
				
				user = Lap.User(self.session().value('authenticated_user'))
				userDir = user.getDir();
				jobDir = userDir+"/job_%s" % (jobName)
				
				taskFile = file(os.path.join(jobDir,"job.task"), "r")
				task = pickle.load(taskFile)
				taskFile.close()

				attribs = task.getAttributes()
				xrslAttribs = task.getXRSLAttributes()
				
				# Create the form, now with previous values
				
				form = self.onCreateEditJobForm(task)
				
				# Change the submit button
				
				form.addFormButton("Modify", "_action_modify")
				form.addFormButton("Back", "_action_back")
				form.setHaveSubmit(False)
				
			else:
			
				LapUtils.log.msg("JobPage", "writeContent", "New job")
				
				# New job
				
				# Get default values from the task class
				
				task = self.onCreateNewTask()
				form = self.onCreateNewJobForm(task)
				form.setSubmitButton("_action_create", "Create")

			form.render()
			
	def sleep(self, transaction):
		
		# Make sure that we don't edit the page all the time
		
		if self.session().hasValue("editjob"):
			self.session().delValue("editjob")
			
		# Move along
			
		SecurePage.sleep(self, transaction)

	def validate(self):
	
		LapUtils.log.msg("JobPage", "validate")
	
		req = self.request()

		# -----------------------------------
		# Form validation goes here
		# -----------------------------------

		initialFrame = int(self.getField(req, 'initialFrame'))
		endFrame = int(self.getField(req, 'endFrame'))
		imageWidth = int(self.getField(req, 'imageWidth'))
		imageHeight = int(self.getField(req, 'imageHeight'))
		prevFile = self.getField(req, 'prevFile')
		
		cpuTime = int(self.getField(req, 'cpuTime'))
		jobName = self.getField(req, 'jobName')
		oldJobName = self.getField(req, 'oldJobName')
		email = self.getField(req, 'email')

		return (initialFrame,
				endFrame,
				imageWidth,
				imageHeight,
				prevFile,
				cpuTime,
				jobName,
				oldJobName,
				email)

	def create(self):
	
		LapUtils.log.msg("JobPage", "create")

		# -------------------------------------------
		# Get validated form fields (excl. files)
		# -------------------------------------------
		
		error, field = self.onValidate(self.request())
		
		if error!="":
			self.setFormStatus(error)
			return

		# -------------------------------------------
		# Create job directory
		# -------------------------------------------

		user = Lap.User(self.session().value('authenticated_user'))
		userDir = user.getDir();
		self.jobDir = userDir+"/job_%s" % (field["jobName"])

		try:
			os.mkdir(self.jobDir)
		except:
			pass

		# -------------------------------------------
		# Handle any file transfers here
		# -------------------------------------------
		
		ok = self.onHandleFileTransfers(self.request(), self.jobDir)

		if not ok:
			self.cleanup()
			self.setFormStatus("No file was specified in the form.", "ManageJobPage")
			self.writeBody()

			return

		# -------------------------------------------
		# Create PovRay task
		# -------------------------------------------
			
		task = self.onCreateNewTask()
		self.onAssignAttribs(task, field, self.request())
		
		task.setDir(self.jobDir)
			
		task.setJobName(field["jobName"])

		if field.has_key("cpuTime"):
			task.setCpuTime(field["cpuTime"])

		if field.has_key("email"):
			task.setEmail(field["email"])
		
		task.setup()

		# Save the task for later reference

		taskFile = file(self.jobDir+"/job.task", "w")
		pickle.dump(task, taskFile)
		taskFile.close()
		
		self.setFormStatus("Job created successfully")
			
		self.writeBody()
		
	def modify(self):
	
		LapUtils.log.msg("JobPage", "modify")

		# -------------------------------------------
		# Get validated form fields (excl. files)
		# -------------------------------------------

		error, field = self.onValidate(self.request())
		
		if error!="":
			self.setFormStatus(error)
			return

		# -------------------------------------------
		# Get job directory
		# -------------------------------------------

		user = Lap.User(self.session().value('authenticated_user'))
		userDir = user.getDir();

		if field["jobName"]<>field["oldJobName"]:
                        oldJobDir = os.path.join(userDir, "job_%s" % field["oldJobName"])
                        newJobDir = os.path.join(userDir, "job_%s" % field["jobName"])
                        print oldJobDir + ", " + newJobDir
                        try:
                                os.rename(oldJobDir, newJobDir)
                        except:
                                print "We had an exception..."
		
		self.jobDir = userDir+"/job_%s" % (field["jobName"])

		# -------------------------------------------
		# Handle any file transfers here
		# -------------------------------------------

		FileUpload = True		
		ok = self.onHandleFileTransfers(self.request(), self.jobDir)

		if not ok:
			FileUpload = False
			#self.cleanup()
			#self.setFormStatus("No file was specified in the form.", "ManageJobPage")
			#self.writeBody()
			
			#return

		# -------------------------------------------
		# Create PovRay task
		# -------------------------------------------
				
		task = self.onCreateNewTask()
		self.onAssignAttribs(task, field, self.request())
		
		task.setDir(self.jobDir)
			
		task.setJobName(field["jobName"])
		task.setCpuTime(field["cpuTime"])
		task.setEmail(field["email"])

		task.setup()

		# Save the task for later reference

		taskFile = file(self.jobDir+"/job.task", "w")
		pickle.dump(task, taskFile)
		taskFile.close()
		
		task.setJobName(field["jobName"])
		task.setCpuTime(field["cpuTime"])
		task.setEmail(field["email"])
		task.setup()

		# Save the task for later reference

		taskFile = file(self.jobDir+"/job.task", "w")
		pickle.dump(task, taskFile)
		taskFile.close()
		
		# -------------------------------------------
		# Create additional job files
		# -------------------------------------------

		self.setFormStatus("Job modified successfully")
		self.writeBody()
		
	def back(self):
		self.sendRedirectAndEnd("ManageJobPage")

	def cleanup(self):
	
		LapUtils.log.msg("JobPage", "cleanup")
		
		try:
			for filename in os.listdir(self.jobDir):
				os.remove(os.path.join(self.jobDir, filename))
			os.rmdir(self.jobDir)
		except:
			pass

	def actions(self):
		return SecurePage.actions(self) + ["create", "modify", "back"]

	def setFormStatus(self, status, returnPage = "ManageJobPage"):
		self.session().setValue("jobpage_status", status)
		self.session().setValue("jobpage_return_page", returnPage)

	def getField(self, req, name):
		if req.hasField(name):
			return req.field(name)
		else:
			return ""
		
	def onCreateNewTask(self):
		pass
	
	def onCreateNewJobForm(self, task):
		pass
	
	def onCreateEditJobForm(self, task):
		pass
	
	def onValidate(self, request):
		pass
	
	def onHandleFileTransfers(self, request, destDir):
		pass
	
	def onAssignAttribs(self, task, field, request):
		pass
		
