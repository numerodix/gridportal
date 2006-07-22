from JobPage import JobPage
from time import *
import os
import sys
import pwd
import string
import pickle

import pyARC
from conf_main import *
from conf_blast import *

import Lap
import LapWeb
import LapBlast
import LapUtils

class BlastJobPage(JobPage):
	
	def __init__(self, blastType):
		JobPage.__init__(self)
		self.blastType = blastType

	def onCreateNewTask(self):
	
		LapUtils.log.msg(self.blastType+"JobPage", "onCreateNewTask")		
		
		return LapBlast.BlastTask(self.blastType)

	def onCreateNewJobForm(self, task):
	
		LapUtils.log.msg(self.blastType+"JobPage", "onCreateNewJobForm")
		
		form = LapWeb.Form(self, "testform", "BlastJobPage"+self.blastType, "Create a "+self.blastType+" job")
		
		attribs = task.getAttributes()
		xrslAttribs = task.getXRSLAttributes()
	
		form.addText("* CPU time (min)", "cpuTime", xrslAttribs["cpuTime"], grid_form["cpuTime"]["desc"])
		form.addText("* Job name", "jobName", xrslAttribs["jobName"], grid_form["jobName"]["desc"])
		form.addText("Email notification", "email", xrslAttribs["notify"], grid_form["email"]["desc"])
		form.addSeparator()

		# special blast fields
		
		p = LapUtils.log.msg

		# Print special Blast fields taken from task.attribs
		# Sort by order defined in config module
		
		bp = blast_params
		for i in range(0, len(bp["order"])):
			key = bp["order"][i]
			if key in attribs:
			
				if (bp["params"][key]["type"] == "select"):
					if (key in bp[self.blastType]):
						param = bp[self.blastType][key]
					else:
						param = bp["common"][key]
					
					options = []
					selected = []
					for j in range(0, len(param)):
						option = param[j]
						options.append((option[0], option[0]))
					selected.append(attribs[key])
						
					form.addSelect(bp["params"][key]["label"], key, options, selected, bp["params"][key]["desc"])
					
				elif (bp["params"][key]["type"] == "text"):
					form.addText(bp["params"][key]["label"], key, attribs[key], bp["params"][key]["desc"])
				elif (bp["params"][key]["type"] == "textarea"):
					form.addTextArea(bp["params"][key]["label"], key, attribs[key], bp["params"][key]["desc"])
				elif (bp["params"][key]["type"] == "file"):
					form.addFile(bp["params"][key]["label"], key, "", bp["params"][key]["desc"])

		form.addSeparator()						
		form.addNormalText("Fields marked with an asterisk (*) must be filled in.")
		form.addNormalText("Note that query sequences must be supplied either in the input box or in an input file.")
		form.addSeparator()		
		form.addNormalText("Defaults displayed on this page reflect the defaults of NCBI BLAST.")
								
		return form	

	def onCreateEditJobForm(self, task):
	
		LapUtils.log.msg(self.blastType+"JobPage", "onCreateEditJobForm")
	
		form = LapWeb.Form(self, "testform", "BlastJobPage"+self.blastType, "Edit "+self.blastType+" job")
		
		attribs = task.getAttributes()
		xrslAttribs = task.getXRSLAttributes()
					
		form.addText("* CPU time (m)", "cpuTime", xrslAttribs["cpuTime"], grid_form["cpuTime"]["desc"])
		form.addText("* Job name", "jobName", xrslAttribs["jobName"], grid_form["jobName"]["desc"])
		form.addText("Email notification", "email", xrslAttribs["notify"], grid_form["email"]["desc"])
		form.addHidden("", "oldJobName", xrslAttribs["jobName"])

		# special blast fields
		
		p = LapUtils.log.msg

		# Print special Blast fields taken from task.attribs
		# Sort by order defined in config module		
		
		bp = blast_params
		for i in range(0, len(bp["order"])):
			key = bp["order"][i]
			if key in attribs:
			
				if (bp["params"][key]["type"] == "select"):
					if (key in bp[self.blastType]):
						param = bp[self.blastType][key]
					else:
						param = bp["common"][key]
					
					options = []
					selected = []
					for j in range(0, len(param)):
						option = param[j]
						options.append((option[0], option[0]))
					selected.append(attribs[key])
						
					form.addSelect(bp["params"][key]["label"], key, options, selected, bp["params"][key]["desc"])
					
				elif (bp["params"][key]["type"] == "text"):
					form.addText(bp["params"][key]["label"], key, attribs[key], bp["params"][key]["desc"])
				elif (bp["params"][key]["type"] == "textarea"):
					form.addTextArea(bp["params"][key]["label"], key, attribs[key], bp["params"][key]["desc"])
				elif (bp["params"][key]["type"] == "file"):
					form.addFile(bp["params"][key]["label"], key, attribs[key], bp["params"][key]["desc"])
					if attribs[key] != "blast.in":
						form.addReadonlyText("current " + bp["params"][key]["label"], key+"prevFile", attribs[key])
						
		form.addSeparator()						
		form.addNormalText("Fields marked with an asterisk (*) must be filled in.")
		form.addNormalText("Note that query sequences must be supplied either in the input box or in an input file.")
		form.addSeparator()		
		form.addNormalText("Defaults displayed on this page reflect the defaults of NCBI BLAST.")
					
		return form
		
	def onValidate(self, request):

		LapUtils.log.msg(self.blastType+"JobPage", "onValidate")

		field = {}

		field["cpuTime"] = int(self.getField(request, 'cpuTime'))
		field["jobName"] = self.getField(request, 'jobName')
		field["oldJobName"] = self.getField(request, 'oldJobName')
		field["email"] = self.getField(request, 'email')

		# No task variable, must first compile list of fields from config module

		attribs = {}
		bp = blast_params
		for btype in ("common", self.blastType):
			for param in bp[btype]:
				attribs[param] = ""

		# Populate form fields with params from list created above

		for param in attribs:
			field[param] = self.getField(request, param)

		# Build list of fields of type "file"

		atts = []
		bp = blast_params
		for btype in ("common", self.blastType):
			for param in bp[btype]:
				if bp["params"][param]["type"] == "file":
					atts.append(param)

		# Assign fields for previous files
			
		for i in range(0, len(atts)):
			if self.getField(request, atts[i]+"prevFile") != "":
				field[atts[i]+"prevFile"] = self.getField(request, atts[i]+"prevFile")

		return ("", field)
	
	def onHandleFileTransfers(self, request, destDir):
	
		LapUtils.log.msg(self.blastType+"JobPage", "onHandleFileTransfers")

		# No task variable, must first compile list of fields from config module

		attribs = {}
		bp = blast_params
		for btype in ("common", self.blastType):
			for param in bp[btype]:
				attribs[param] = ""

		# Populate form fields with params from list created above

		for param in attribs:
			attribs[param] = self.getField(request, param)
		
		# Build list of parameters of type "file"
		
		atts = []
		bp = blast_params
		for btype in ("common", self.blastType):
			for param in bp[btype]:
				if bp["params"][param]["type"] == "file":
					atts.append(param)
			
		# Process all files
		ok = False
		for i in range(0, len(atts)):
			
			# check if input file also has text field to be read if
			# no file is supplied
			
			par = ""
			try:
				par = string.splitfields(atts[i], "InputFile")[0]
			except:
				pass

			# file supplied is tranferred				

			s = self.request()

			if attribs[par] != "":
				ok = True
				
			elif s.hasField(atts[i]) and s.field(atts[i]) != "":
				try:
					ok = True
					f = self.request().field(atts[i])
					fileContent = f.file.read()
					inputFile = file(os.path.join(destDir, f.filename), "w")
					inputFile.write(fileContent)
					inputFile.close()
				except:
					ok = False

		return ok
		
	def onAssignAttribs(self, task, field, request):
	
		LapUtils.log.msg(self.blastType+"JobPage", "onAssignTaskAttribs")

		attribs = task.getAttributes()
		
		for i in field:
			attribs[i] = field[i]
			
		# File params must not be pickle'd, set them to void

		bp = blast_params
		for param in attribs:
			if param in bp["params"]:
				if bp["params"][param]["type"] == "file":
					attribs[param] = ""

		# Build list of file params

		atts = []
		for btype in ("common", self.blastType):
			for param in bp[btype]:
				if bp["params"][param]["type"] == "file":
					atts.append(param)
					
		for i in range(0, len(atts)):
			if self.request().hasField(atts[i]):
		
				f = self.request().field(atts[i])
				
				try:
					filename = f.filename
				except KeyError:
					filename = field[atts[i]+"prevFile"]
				except:		# no input file, read from text field instead
					filename = ""
					
				LapUtils.log.msg(self.blastType+"JobPage", "onAssignTaskAttribs", "f.filename = %s" % filename)
				
				task.setInputFile(filename)
		