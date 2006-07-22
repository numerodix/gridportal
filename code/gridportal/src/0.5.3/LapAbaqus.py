import os, sys, string

#import Lap
import LapUtils
import LapJob

abaqusShellTemplate = """#!/bin/sh
/opt/abaqus/Commands/abaqus job=%(jobIdentifier)s interactive
"""

abaqusEnvFileTemplate = """abaquslm_license_file="@%(licenseServer)s" 
"""
abaqusEnvFileTemplate = ""

class AbaqusTask(LapJob.Task):
	def __init__(self):
		LapJob.Task.__init__(self)

		self.setDescription("ABAQUS")
		self.setTaskEditPage("AbaqusJobPage")

		# Task specific attributes

		attribs = self.getAttributes()
		attribs["inputFile"] = "sample.inp"
		attribs["licenseServer"] = "norgrid.ntnu.no"

		# XRSL specific attributes		

		xrslAttribs = self.getXRSLAttributes()
		xrslAttribs["executable"] = "/bin/sh"
		xrslAttribs["arguments"] = "./run.sh"
#		xrslAttribs["runTimeEnvironment"] = "ABAQUS"

		self.addInputFile("run.sh")
		self.addInputFile("abaqus_v6.env")

		self.addOutputFile("/")
		
	def setInputFile(self, filename):
	
		self.getAttributes()["inputFile"] = filename
		
	def setup(self):

		# Get directory and attributes	
		
		taskDir = self.getDir()
		attribs = self.getAttributes()

		self.addInputFile(attribs["inputFile"])
		
		# Create abaqus env file 
		
		envFile = file(os.path.join(taskDir,"abaqus_v6.env"), "w")
		envFile.write(abaqusEnvFileTemplate % {"licenseServer":attribs["licenseServer"]})
		envFile.close()

		# Create shell script
		
		jobIdentifier, ext = os.path.splitext(attribs["inputFile"])
		
		LapUtils.log.msg("AbaqusTask", "setup", "jobIdentifier = %s, ext = %s" % (jobIdentifier, ext))		

		shellFile = file(taskDir+"/run.sh", "w")
		shellFile.write(abaqusShellTemplate %  {"jobIdentifier":jobIdentifier})
		shellFile.close()

		# Create XRSL file

		xrslFile = LapJob.XRSLFile(self)
		xrslFile.setFilename(taskDir+"/job.xrsl")
		xrslFile.write()

	def clean(self):
		if self.getDir()!="":
			for filename in os.listdir(self.getDir()):
				os.remove(os.path.join(self.getDir(), filename))
