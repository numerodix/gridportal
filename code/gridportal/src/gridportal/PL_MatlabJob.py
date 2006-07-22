import os, sys, string
from conf_main import *
#import Lap
import LapUtils
import LapJob

matlabShellTemplate = """#!/bin/sh
""" + env_matlab + """ -nodisplay -nosplash -nojvm < %(jobIdentifier)s
"""

matlabEnvFileTemplate = """abaquslm_license_file="@%(licenseServer)s" 
"""
matlabEnvFileTemplate = ""

class MatlabTask(LapJob.Task):
	def __init__(self):
		LapJob.Task.__init__(self)

		self.setDescription("MATLAB")
		self.setTaskEditPage("PL_MatlabJobPage")

		# Task specific attributes

		attribs = self.getAttributes()
		attribs["inputFile"] = "sample.inp"
		attribs["licenseServer"] = ""

		# XRSL specific attributes		

		xrslAttribs = self.getXRSLAttributes()
		xrslAttribs["executable"] = "/bin/sh"
		xrslAttribs["arguments"] = "./run.sh"
		xrslAttribs["jobName"] = "MatlabJob"
		
		RTEs = ["APPS/MATH/MATLAB-7.0.0-1.0"]
		self.setRuntimeEnvironment(RTEs)

		self.addInputFile("run.sh")
#		self.addInputFile("abaqus_v6.env")

		self.addOutputFile("/")
		
	def setInputFile(self, filename):
	
		self.getAttributes()["inputFile"] = filename
		
	def setup(self):

		# Get directory and attributes	
		
		taskDir = self.getDir()
		attribs = self.getAttributes()

		self.addInputFile(attribs["inputFile"])
		
		# Create abaqus env file 
		
#		envFile = file(os.path.join(taskDir,"abaqus_v6.env"), "w")
#		envFile.write(abaqusEnvFileTemplate % {"licenseServer":attribs["licenseServer"]})
#		envFile.close()

		# Create shell script
		
		jobIdentifier, ext = os.path.splitext(attribs["inputFile"])
		
		LapUtils.log.msg("MatlabTask", "setup", "jobIdentifier = %s, ext = %s" % (jobIdentifier, ext))		

		shellFile = file(taskDir+"/run.sh", "w")
		shellFile.write(matlabShellTemplate %  {"jobIdentifier":attribs["inputFile"]})
		shellFile.close()

		# Create XRSL file

		xrslFile = LapJob.XRSLFile(self)
		xrslFile.setFilename(taskDir+"/job.xrsl")
		xrslFile.write()

	def clean(self):
		if self.getDir()!="":
			for filename in os.listdir(self.getDir()):
				os.remove(os.path.join(self.getDir(), filename))
