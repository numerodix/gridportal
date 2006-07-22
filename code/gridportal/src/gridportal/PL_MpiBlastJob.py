import os, sys, string
from conf_main import *
from conf_mpiblast import *
#import Lap
import LapUtils
import LapJob


class MpiBlastTask(LapJob.Task):
	def __init__(self, blastType):
		LapJob.Task.__init__(self)

		self.blastType = blastType
		
		self.setDescription("mpiBLAST")
		self.setTaskEditPage("PL_MpiBlastJobPage"+self.blastType)

		# Task specific attributes

		attribs = self.getAttributes()

		# set defaults for common blast job

		p = LapUtils.log.msg
		
		# Build list of attributes to be stored in pickle
		
		bp = blast_params
		for btype in ("common", self.blastType):
			for param in bp[btype]:
				for j in range(0, len(bp[btype][param])):
					option = bp[btype][param][j]
					if (option[2] == 1): 
						attribs[param] = option[0]
						p(param, option[0])

		# XRSL specific attributes

		xrslAttribs = self.getXRSLAttributes()
		xrslAttribs["executable"] = "/bin/sh"
#		xrslAttribs["arguments"] = "./run.sh"
		xrslAttribs["jobName"] = self.blastType+"Job"

		self.addOutputFile("/")
		
	def setInputFile(self, filename):
	
		self.getAttributes()["seqInputFile"] = filename
		
	def setup(self):

		# Get directory and attributes	
		
		taskDir = self.getDir()
		attribs = self.getAttributes()

		if attribs["seqInputFile"] == "":
			
			# write input from textbox
			if attribs["seq"] != "":
				attribs["seqInputFile"] = "blast.in"
				inputFile = file(os.path.join(taskDir,attribs["seqInputFile"]), "w")
				inputFile.write(attribs["seq"])
				inputFile.close()
			
			# use old file
			else:
				try:
					attribs["seqInputFile"] = attribs["seqInputFileprevFile"]
				except:
					pass
		
		self.addInputFile(attribs["seqInputFile"])

		# Create shell script
		
		jobIdentifier, ext = os.path.splitext(attribs["seqInputFile"])
		
		LapUtils.log.msg("MpiBlastTask", "setup", "jobIdentifier = %s, ext = %s" % (jobIdentifier, ext))

		
		params = ""
		
		p = LapUtils.log.msg
		
		# Build string of command line parameters
		
		for i in range(0, len(bp["order"])):
			key = bp["order"][i]
			if bp["order"][i] in attribs:
				if bp["order"][i] in bp["common"]:
					btype = bp["common"]
				elif bp["order"][i] in bp[self.blastType]:
					btype = bp[self.blastType]
					
				# skip fields that are input through files instead
				if key+"InputFile" in attribs:
					pass
					
				# handle select fields
				elif bp["params"][key]["type"] == "select":
					for j in range(0, len(btype[key])):
						option = btype[key][j]
						if option[0] == attribs[key]:
							params += option[1] + " "
				
				# skip files, handled below			
				elif bp["params"][key]["type"] == "file":
					pass
				
				# strings cannot span multiple lines
				elif bp["params"][key]["type"] == "textarea":
					if attribs[key] != "":			# eliminate empty input, causes syntax errors
					
						att = attribs[key]
						attribs[key] = string.replace(attribs[key], "\n", ",") 	# remove newlines
						attribs[key] = string.replace(attribs[key], "\r", "")		# remove /r
						attribs[key] = attribs[key][:-1]												# remove trailing comma
						
						params += btype[key][0][1] + " " + attribs[key] + " "
						attribs[key] = att
				
				else:
					if attribs[key] != "":
						params += btype[key][0][1] + " " + attribs[key] + " "
						
						
		html = ""
		if attribs["html_output"] == "Yes":
			html = ".html"
		
		# Set argument string got XRSL file
		
		xrslAttribs = self.getXRSLAttributes()
		xrslAttribs["arguments"] = mpiblast_template % {
			"blastType": self.blastType,
			"jobinfile": attribs["seqInputFile"],
			"html": html,
			"params": params
		}
		
		# set runtime environment(s)
		
		RTEs = []
		if RTE_dbs.has_key(attribs["query_database"]):
			RTEs.append(RTE_dbs[attribs["query_database"]])
		RTEs.append(RTE_mpiblast)
		self.setRuntimeEnvironment(RTEs)

		# Create XRSL file

		xrslFile = LapJob.XRSLFile(self)
		xrslFile.setFilename(taskDir+"/job.xrsl")
		xrslFile.write()

	def clean(self):
		if self.getDir()!="":
			for filename in os.listdir(self.getDir()):
				os.remove(os.path.join(self.getDir(), filename))

				
def main():
	n = MpiBlastTask(blastp, "blastp")
	print n.getAttributes()
	
	blastType = "blastp"
	for key in blast_params[blastType]:
		print blast_params[blastType][key]["def"]
				
if __name__ == "__main__":
	main()
				