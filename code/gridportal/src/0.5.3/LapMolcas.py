import os, sys, string

import LapJob

class MolcasTask(LapJob.Task):
	def __init__(self):
		LapJob.Task.__init__(self)

		self.setDescription("MOLCAS")
		self.setTaskEditPage("MolcasJobPage")

		# Task specific attributes

		xrlsAttribs = self.getXRSLAttributes()
		xrlsAttribs["jobName"] = "MolcasJob"

		attribs = self.getAttributes()
		attribs["inputFile"] = "default.input"
		attribs["cputime"] = 5
		attribs["cpus"] = 1
		attribs["memory"] = 1000
		attribs["disk"] = 2000
		attribs["extrafiles"] = []
		attribs["resultfiles"] = []
		attribs["clusters"] = ""
		attribs["email"] = ""

	def setup(self):

		# Get directory and attributes	
		
		taskDir = self.getDir()
		attribs = self.getAttributes()
		xrslAttribs = self.getXRSLAttributes()

		if len(attribs["extrafiles"])>0:
			
			extraFile = file(os.path.join(taskDir,"extrafiles"), "w")
			
			idx = 0
			numberOfFiles = len(attribs["extrafiles"])

			while idx<len(numberOfFiles):
				extraFile.write(attribs["extrafiles"][idx])

				if idx<numberOfFiles-1:
					extraFile.write(",")

			extraFile.close()

		if len(attribs["resultfiles"])>0:

                        resultFile = file(os.path.join(taskDir,"resultfiles"), "w")

                        idx = 0
                        numberOfFiles = len(attribs["resultfiles"])

                        while idx<len(numberOfFiles):
                                resultFile.write(attribs["resultfiles"][idx])

                                if idx<numberOfFiles-1:
                                        resultFile.write(",")

                        resultFile.close()

		# Create XRSL file

		resultFilesSwitch = " -upload=extrafiles" 
		extraFilesSwitch  = " -result=resultfiles"
		clustersSwitch    = " -clusters=%s"

		commandLine = "/sw/lap/molcas-nordugrid -input=%(inputFile)s -project=%(project)s -cputime=%(cputime)d -cpus=%(cpus)d -molcasmem=%(molcasmem)d  -molcasdisk=%(molcasdisk)d -email=%(email)s"

		if len(attribs["extrafiles"])>0:
			commandLine = commandLine + extraFilesCommandLine

		if len(attribs["resultfiles"])>0:
			commandLine = commandLine + resultFilesCommandLine

		if attribs["clusters"]!="":
			
			File = file(os.path.join(taskDir,"extrafiles"), "w")

                        idx = 0
                        numberOfFiles = len(attribs["extrafiles"])

                        while idx<len(numberOfFiles):
                                extraFile.write(attribs["extrafiles"][idx])

                                if idx<numberOfFiles-1:
                                        extraFile.write(",")

                        extraFile.close()

			commandLine = commandLine + clustersSwitch

		print commandLine %{"inputFile":attribs["inputFile"],
                         "project":xrslAttribs["jobName"],
                         "cpus":attribs["cpus"],
                         "cputime":attribs["cputime"],
                         "molcasmem":attribs["memory"],
                         "molcasdisk":attribs["disk"],
                         "email":attribs["email"]}
		currDir = os.getcwd()
		os.chdir(taskDir)
		os.system(commandLine % 
			{"inputFile":attribs["inputFile"],
			 "project":xrslAttribs["jobName"],
			 "cpus":attribs["cpus"],
			 "cputime":attribs["cputime"],
			 "molcasmem":attribs["memory"],
			 "molcasdisk":attribs["disk"],
			 "email":attribs["email"]}
		)
		os.chdir(currDir)

		# Rename molcas xrsl to fit LAP

		orgXRSLFile = os.path.join(taskDir, "molcas.xrsl")
		newXRSLFile = os.path.join(taskDir, "job.xrsl")
	
		if os.path.isfile(orgXRSLFile):
			os.rename(orgXRSLFile, newXRSLFile)

	def clean(self):
		if self.getDir()!="":
			for filename in os.listdir(self.getDir()):
				os.remove(os.path.join(self.getDir(), filename))
