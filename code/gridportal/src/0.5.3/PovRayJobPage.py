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
import LapPovRay
import LapUtils

class PovRayJobPage(JobPage):

	def onCreateNewTask(self):
	
		LapUtils.log.msg("PovRayJobPage2", "onCreateNewTask")		

		return LapPovRay.PovRayTask()

	def onCreateNewJobForm(self, task):
	
		LapUtils.log.msg("PovRayJobPage2", "onCreateNewJobForm")
		
		form = LapWeb.Form(self, "testform", "PovRayJobPage", "Create a POVRay job")
		
		attribs = task.getAttributes()
		xrslAttribs = task.getXRSLAttributes()
	
		form.addText("Initial frame ", "initialFrame", attribs["initialFrame"])
		form.addText("End frame", "endFrame", attribs["finalFrame"])
		form.addText("Image width", "imageWidth", attribs["imageWidth"])
		form.addText("Image height", "imageHeight", attribs["imageHeight"])
		form.addSeparator()
		form.addFile("Input file", "povFile", "")
		form.addSeparator()
		form.addText("CPU time (s)", "cpuTime", xrslAttribs["cpuTime"])
		form.addText("Job name", "jobName", xrslAttribs["jobName"])
		form.addText("Email notification", "email", xrslAttribs["notify"])
	
		return form	

	def onCreateEditJobForm(self, task):
	
		LapUtils.log.msg("PovRayJobPage", "onCreateEditJobForm")

		form = LapWeb.Form(self, "testform", "PovRayJobPage", "Edit POVRay job")
		
		attribs = task.getAttributes()
		xrslAttribs = task.getXRSLAttributes()
					
		form.addText("Initial frame ", "initialFrame", attribs["initialFrame"])
		form.addText("End frame", "endFrame", attribs["finalFrame"])
		form.addText("Image width", "imageWidth", attribs["imageWidth"])
		form.addText("Image height", "imageHeight", attribs["imageHeight"])
		form.addSeparator()
		form.addFile("Input file", "povFile", attribs["povFile"])
		form.addReadonlyText("Current file", "prevFile", attribs["povFile"])
		form.addSeparator()
		form.addText("CPU time (s)", "cpuTime", xrslAttribs["cpuTime"])
		form.addText("Job name", "jobName", xrslAttribs["jobName"])
		form.addText("Email notification", "email", xrslAttribs["notify"])
		form.addHidden("", "oldJobName", xrslAttribs["jobName"])
		
		return form
		
	def onValidate(self, request):
	
		LapUtils.log.msg("PovRayJobPage", "onValidate")
		
		field = {}
		
		field["initialFrame"] = int(self.getField(request, 'initialFrame'))
		field["endFrame"] = int(self.getField(request, 'endFrame'))
		field["imageWidth"] = int(self.getField(request, 'imageWidth'))
		field["imageHeight"] = int(self.getField(request, 'imageHeight'))
		field["prevFile"] = self.getField(request, 'prevFile')
		
		field["cpuTime"] = int(self.getField(request, 'cpuTime'))
		field["jobName"] = self.getField(request, 'jobName')
		field["oldJobName"] = self.getField(request, 'oldJobName')
		field["email"] = self.getField(request, 'email')
		
		return ("", field)
	
	def onHandleFileTransfers(self, request, destDir):
	
		LapUtils.log.msg("PovRayJobPage", "onHandleFileTransfers")

		if self.request().hasField("povFile"):
			ok = True
			try:
				f = self.request().field("povFile")
				fileContent = f.file.read()
				povRayFile = file(os.path.join(destDir, f.filename), "w")
				povRayFile.write(fileContent)
				povRayFile.close()
			except:
				ok = False
				pass
			
			return ok
		else:
			return False
		
	def onAssignAttribs(self, task, field, request):
	
		LapUtils.log.msg("PovRayJobPage", "onAssignTaskAttribs")
	
		attribs = task.getAttributes()
		attribs["initialFrame"] = field["initialFrame"]
		attribs["finalFrame"] = field["endFrame"]
		attribs["imageWidth"] = field["imageWidth"]
		attribs["imageHeight"] = field["imageHeight"]
		
		if self.request().hasField("povFile"):

			f = self.request().field("povFile")
			
			try:
				filename = f.filename
			except:
				filename = field["prevFile"]
			
			LapUtils.log.msg("PovRayJobPage", "onAssignTaskAttribs", "f.filename = %s" % filename)
			
			if filename!="":
				task.setPovrayFile(filename)
		

		

