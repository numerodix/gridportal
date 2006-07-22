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
import LapMolcas
import LapUtils

class MolcasJobPage(JobPage):

	def onCreateNewTask(self):
	
		LapUtils.log.msg("PovRayJobPage2", "onCreateNewTask")		

		return LapMolcas.MolcasTask()

	def onCreateNewJobForm(self, task):
	
		LapUtils.log.msg("PovRayJobPage2", "onCreateNewJobForm")
		
		form = LapWeb.Form(self, "testform", "MolcasJobPage", "Create a MOLCAS job")
		
		attribs = task.getAttributes()
		xrslAttribs = task.getXRSLAttributes()
	
		form.addFile("Input file", "inputFile", "")
		form.addSeparator()
		form.addText("Job name", "jobName", xrslAttribs["jobName"])
		form.addText("CPU time (s)", "cputime", attribs["cputime"])
		form.addText("Number of CPU:s", "cpus", attribs["cpus"])
		form.addText("Memory (Mb)", "memory", attribs["memory"])
		form.addText("Disk (Mb)", "disk", attribs["disk"])
		form.addText("Email notification", "email", attribs["email"])
	
		return form	

	def onCreateEditJobForm(self, task):
	
		LapUtils.log.msg("PovRayJobPage", "onCreateEditJobForm")

		form = LapWeb.Form(self, "testform", "PovRayJobPage", "Edit POVRay job")
		
		attribs = task.getAttributes()
		xrslAttribs = task.getXRSLAttributes()
					
		form.addFile("Input file", "inputFile", attribs["inputFile"])
		form.addReadonlyText("Current file", "prevFile", attribs["inputFile"])
		form.addSeparator()
                form.addText("Job name", "jobName", xrslAttribs["jobName"])
                form.addText("CPU time (s)", "cputime", attribs["cputime"])
                form.addText("Number of CPU:s", "cpus", attribs["cpus"])
                form.addText("Memory (Mb)", "memory", attribs["memory"])
                form.addText("Disk (Mb)", "disk", attribs["disk"])
                form.addText("Email notification", "email", attribs["email"])
		
		form.addHidden("", "oldJobName", xrslAttribs["jobName"])
		return form
		
	def onValidate(self, request):
	
		LapUtils.log.msg("PovRayJobPage", "onValidate")
		
		field = {}
		
		field["inputFile"] = self.getField(request, 'inputFile')
		field["prevFile"] = self.getField(request, 'prevFile')
		field["jobName"] = self.getField(request, 'jobName')
		field["cputime"] = int(self.getField(request, 'cputime'))
		field["cpus"] = int(self.getField(request, 'cpus'))
		
		field["memory"] = int(self.getField(request, 'memory'))
		field["disk"] = int(self.getField(request, 'disk'))
		field["email"] = self.getField(request, 'email')
		
		return ("", field)
	
	def onHandleFileTransfers(self, request, destDir):
	
		LapUtils.log.msg("PovRayJobPage", "onHandleFileTransfers")

		if self.request().hasField("inputFile"):
			ok = True
			try:
				f = self.request().field("inputFile")
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
		attribs["cputime"] = field["cputime"]
		attribs["cpus"] = field["cpus"]
		attribs["memory"] = field["memory"]
		attribs["disk"] = field["disk"]
		attribs["email"] = field["email"]
		
		if self.request().hasField("inputFile"):

			f = self.request().field("inputFile")
			
			try:
				filename = f.filename
			except:
				filename = field["prevFile"]
			
			LapUtils.log.msg("PovRayJobPage", "onAssignTaskAttribs", "f.filename = %s" % filename)
			
			if filename!="":
				task.getAttributes()["inputFile"] = filename
		

		

