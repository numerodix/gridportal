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
import PL_MatlabJob
import LapUtils

class PL_MatlabJobPage(JobPage):

	def onCreateNewTask(self):
	
		LapUtils.log.msg("PL_MatlabJobPage", "onCreateNewTask")		

		return PL_MatlabJob.MatlabTask()

	def onCreateNewJobForm(self, task):
	
		LapUtils.log.msg("PL_MatlabJobPage", "onCreateNewJobForm")
		
		form = LapWeb.Form(self, "testform", "PL_MatlabJobPage", "Create a Matlab job")
		
		attribs = task.getAttributes()
		xrslAttribs = task.getXRSLAttributes()
	
		form.addFile("Input file", "inputFile", "")
#		form.addText("License server", "licenseServer", attribs["licenseServer"])
		form.addSeparator()
		form.addText("CPU time (m)", "cpuTime", xrslAttribs["cpuTime"])
		form.addText("Job name", "jobName", xrslAttribs["jobName"])
		form.addText("Email notification", "email", xrslAttribs["notify"])
#		form.addText(os.getcwd(), "cwd", "")
	
		return form	

	def onCreateEditJobForm(self, task):
	
		LapUtils.log.msg("PL_MatlabJobPage", "onCreateEditJobForm")

		form = LapWeb.Form(self, "testform", "PL_MatlabJobPage", "Edit Matlab job")
		
		attribs = task.getAttributes()
		xrslAttribs = task.getXRSLAttributes()
					
		form.addFile("Input file", "inputFile", attribs["inputFile"])
		form.addReadonlyText("Current file", "prevFile", attribs["inputFile"])
#		form.addText("License server", "licenseServer", attribs["licenseServer"])
		form.addSeparator()
		form.addText("CPU time (min)", "cpuTime", xrslAttribs["cpuTime"])
		form.addText("Job name", "jobName", xrslAttribs["jobName"])
		form.addText("Email notification", "email", xrslAttribs["notify"])
		form.addHidden("", "oldJobName", xrslAttribs["jobName"])
		
		return form
		
	def onValidate(self, request):
	
		LapUtils.log.msg("PL_MatlabJobPage", "onValidate")
		
		field = {}
		
		field["prevFile"] = self.getField(request, 'prevFile')
#		field["licenseServer"] = self.getField(request, 'licenseServer')
		
		field["cpuTime"] = int(self.getField(request, 'cpuTime'))
		field["jobName"] = self.getField(request, 'jobName')
		field["oldJobName"] = self.getField(request, 'oldJobName')
		field["email"] = self.getField(request, 'email')
		
		return ("", field)
	
	def onHandleFileTransfers(self, request, destDir):
	
		LapUtils.log.msg("PL_MatlabJobPage", "onHandleFileTransfers")

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
	
		LapUtils.log.msg("PL_MatlabJobPage", "onAssignTaskAttribs")
	
		attribs = task.getAttributes()
		
#		attribs["licenseServer"] = field["licenseServer"]
		
		if self.request().hasField("inputFile"):
	
			f = self.request().field("inputFile")
			
			try:
				filename = f.filename
			except:
				filename = field["prevFile"]
				
			LapUtils.log.msg("PL_MatlabJobPage", "onAssignTaskAttribs", "f.filename = %s" % filename)
			
			task.setInputFile(filename)
		

		

