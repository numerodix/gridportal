import os, sys, string

import LapJob

povRayIniTemplate = """Display=off
Initial_Frame=%(initialFrame)s
Final_Frame=%(finalFrame)s
Input_File_Name=%(povFile)s
Cyclic_Animation=On
Pause_when_Done=Off
Output_File_Type=N
+W%(imageWidth)s +H%(imageHeight)s;

Antialias=Off
+am2
Antialias_Threshold=0.25
Antialias_Depth=1
Test_Abort_Count=100
"""

povRayShellTemplate = """#!/bin/sh
mkdir images
cp *.ini images
cp *.pov images
cd images
povray %(iniFile)s
cd ..
tar czf images.tar.gz images
"""

class PovRayTask(LapJob.Task):
	def __init__(self):
		LapJob.Task.__init__(self)

		self.setDescription("POVRay")
		self.setTaskEditPage("PovRayJobPage")

		# Task specific attributes

		self.povFile = ""

		attribs = self.getAttributes()
		attribs["initialFrame"] = 0
		attribs["finalFrame"] = 1
		attribs["imageWidth"] = 1024
		attribs["imageHeight"] = 768
		attribs["povFile"] = "pov.pov"
		attribs["iniFile"] = "settings.ini"

		# XRSL specific attributes		

		xrslAttribs = self.getXRSLAttributes()
		xrslAttribs["executable"] = "/bin/sh"
		xrslAttribs["arguments"] = "./run.sh"
		xrslAttribs["runTimeEnvironment"] = "POVRAY"

		self.addInputFile("run.sh")
		self.addInputFile("settings.ini")

		self.addOutputFile("images")
		self.addOutputFile("images.tar.gz")
		
	def setPovrayFile(self, filename):
	
		print "Settings povrayfile."
		self.getAttributes()["povFile"] = filename
		
	def setup(self):

		# Get directory and attributes	
		
		taskDir = self.getDir()
		attribs = self.getAttributes()

		self.addInputFile(attribs["povFile"])

		# Create a povray ini file

		iniFile = file(taskDir+"/settings.ini" , "w")
		iniFile.write(povRayIniTemplate %
					  {"initialFrame":str(attribs["initialFrame"]),
					   "finalFrame":str(attribs["finalFrame"]),
					   "imageWidth":str(attribs["imageWidth"]),
					   "imageHeight":str(attribs["imageHeight"]),
					   "povFile":attribs["povFile"]})
		iniFile.close()

		# Create shell script

		shellFile = file(taskDir+"/run.sh", "w")
		shellFile.write(povRayShellTemplate %
					  {"iniFile":"settings.ini"})
		shellFile.close()

		# Create XRSL file

		xrslFile = LapJob.XRSLFile(self)
		xrslFile.setFilename(taskDir+"/job.xrsl")
		xrslFile.write()

	def clean(self):
		if self.getDir()!="":
			for filename in os.listdir(self.getDir()):
				os.remove(os.path.join(self.getDir(), filename))
