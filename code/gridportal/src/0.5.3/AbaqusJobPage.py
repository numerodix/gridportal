from JobPage import JobPage
from time import *
import os
import sys
import pwd
import string
import pickle

import pyARC

import Lap
import LapWeb
import LapAbaqus
import LapUtils

class AbaqusJobPage(JobPage):

	def onCreateNewTask(self):
	
		LapUtils.log.msg("AbaqusJobPage", "onCreateNewTask")		

		return LapAbaqus.AbaqusTask()

	def onCreateNewJobForm(self, task):
	
		LapUtils.log.msg("AbaqusJobPage", "onCreateNewJobForm")
		
		form = LapWeb.Form(self, "testform", "AbaqusJobPage", "Create a Abaqus job")
		
		attribs = task.getAttributes()
		xrslAttribs = task.getXRSLAttributes()
	
		form.addFile("Input file", "inputFile", "")
		form.addText("License server", "licenseServer", attribs["licenseServer"])
		form.addSeparator()
		form.addText("CPU time (s)", "cpuTime", xrslAttribs["cpuTime"])
#		form.addText("Job name", "jobName", xrslAttribs["jobName"])
		form.addText("Job name", "jobName", "AbaqusJob")
		form.addText("Email notification", "email", xrslAttribs["notify"])
#		form.addText(os.getcwd(), "cwd", "")
	
		return form	

	def onCreateEditJobForm(self, task):
	
		LapUtils.log.msg("AbaqusJobPage", "onCreateEditJobForm")

		form = LapWeb.Form(self, "testform", "AbaqusJobPage", "Edit Abaqus job")
		
		attribs = task.getAttributes()
		xrslAttribs = task.getXRSLAttributes()
					
		form.addFile("Input file", "inputFile", attribs["inputFile"])
		form.addReadonlyText("Current file", "prevFile", attribs["inputFile"])
		form.addText("License server", "licenseServer", attribs["licenseServer"])
		form.addSeparator()
		form.addText("CPU time (s)", "cpuTime", xrslAttribs["cpuTime"])
		form.addText("Job name", "jobName", xrslAttribs["jobName"])
		form.addText("Email notification", "email", xrslAttribs["notify"])
		form.addHidden("", "oldJobName", xrslAttribs["jobName"])
		
		return form
		
	def onValidate(self, request):
	
		LapUtils.log.msg("AbaqusJobPage", "onValidate")
		
		field = {}
		
		field["prevFile"] = self.getField(request, 'prevFile')
		field["licenseServer"] = self.getField(request, 'licenseServer')
		
		field["cpuTime"] = int(self.getField(request, 'cpuTime'))
		field["jobName"] = self.getField(request, 'jobName')
		field["oldJobName"] = self.getField(request, 'oldJobName')
		field["email"] = self.getField(request, 'email')
		
		return ("", field)
	
	def onHandleFileTransfers(self, request, destDir):
	
		LapUtils.log.msg("AbaqusJobPage", "onHandleFileTransfers")

		if self.request().hasField("inputFile"):
			ok = True
			try:
				f = self.request().field("inputFile")
				fileContent = f.file.read()
				inputFile = file(os.path.join(destDir, f.filename), "w")
				inputFile.write(fileContent)
				inputFile.close()
			except:
				ok = False
				pass
			
			return ok
		else:
			return False
		
	def onAssignAttribs(self, task, field, request):
	
		LapUtils.log.msg("AbaqusJobPage", "onAssignTaskAttribs")
	
		attribs = task.getAttributes()
		
		attribs["licenseServer"] = field["licenseServer"]
		
		if self.request().hasField("inputFile"):
	
			f = self.request().field("inputFile")
			
			try:
				filename = f.filename
			except:
				filename = field["prevFile"]
				
			LapUtils.log.msg("AbaqusJobPage", "onAssignTaskAttribs", "f.filename = %s" % filename)
			
			task.setInputFile(filename)
		

		

