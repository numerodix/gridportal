import string
import os
import sys

import pyARC
import LapUtils
import LapTemplates

class PropertyList:
	def __init__(self):
		self.properties = {}

	def __len__(self):
		return len(self.properties)

	def __getitem__(self, key):
		return self.properties[key]

	def __setitem__(self, key, value):
		self.properties[key] = value

	def __delitem__(self, key):
		del self.properties[key]

	def __iter__(self):
		return self.properties

	def keys(self):
		return self.properties.keys()

	def items(self):
		return self.properties.items()

class XRSLAttributes(PropertyList):
	def __init__(self):
		PropertyList.__init__(self)
		self["executable"] = ""
		self["arguments"] = ""
		self["inputFiles"] = {}
		self["outputFiles"] = {}
		self["runTimeEnvironment"] = ""
		self["cpuTime"] = 30
		self["notify"] = ""
		self["memory"] = -1
		self["disk"] = -1
		self["stdin"] = ""
		self["stdout"] = "stdout.txt"
		self["stderr"] = "stderr.txt"
		self["jobName"] = "NoName"
		self["gmlog"] = "gmlog"
		self["cluster"] = ""
		self["startTime"] = ""
		self["rerun"] = -1
		self["architecture"] = ""
		self["count"] = -1

class Task:
	def __init__(self):
		self.xrslAttributes = XRSLAttributes()
		self.remoteExecutable = False
		self.description = "Default LAP task"
		self.attributes = {}
		self.taskDir = ""
		self.taskEditPage = ""
		self.customXRSLGeneration = False

	def setup(self):
		pass

	def clean(self):
		pass

	def setDescription(self, description):
		self.description = description

	def getDescription(self):
		return self.description

	def setJobName(self, name):
		self.xrslAttributes["jobName"] = name

	def getJobName(self):
		return self.xrslAttributes["jobName"]

	def setCpuTime(self, cpuTime):
		self.xrslAttributes["cpuTime"] = cpuTime

	def setEmail(self, email):
		self.xrslAttributes["notify"] = email
		
	def setTaskEditPage(self, page):
		self.taskEditPage = page
		
	def getTaskEditPage(self):
		return self.taskEditPage
			
	def addInputFile(self, name, location=""):
		self.xrslAttributes["inputFiles"][name] = location

	def addOutputFile(self, name, location=""):
		self.xrslAttributes["outputFiles"][name] = location

	def setDir(self, taskDir):
		self.taskDir = taskDir

	def getDir(self):
		return self.taskDir

	def getXRSLAttributes(self):
		return self.xrslAttributes

	def getAttributes(self):
		return self.attributes

	def printAttributes(self):
		for key in self.parameters.keys():
			print key + " = " + str(self.parameters[key])


class TaskDescriptionFile:
	def __init__(self, task):
		self.task = task
		self.filename = ""

	def setFilename(self, name):
		self.filename = name

	def getFilename(self):
		return self.filename

	def setTask(self, task):
		self.task = task

	def getTask(self):
		return self.task

	def write(self):
		pass

class XRSLFile(TaskDescriptionFile):
	def __init__(self, task):
		TaskDescriptionFile.__init__(self, task)

	def write(self):
		if self.getFilename()!="" and self.getTask()!=None:
			xrslFile = open(self.getFilename(), 'w')
			xrslFile.write("&\n")

			for key, value in self.getTask().getXRSLAttributes().items():

				haveValue = False

				if type(value) is str:
					if value!="":
						xrslFile.write('(' + key + ' = "' + value + '")\n')
				elif type(value) is int:
					if value!=-1:
						xrslFile.write("(" + key + " = " + repr(value) + ")\n")
				elif type(value) is dict:
					xrslFile.write('(' + key + ' = \n')
					for key2, value2 in value.items():
						xrslFile.write('\t("' + key2 + '" "' + value2 + '")\n')
					xrslFile.write(')\n')

			xrslFile.close()
