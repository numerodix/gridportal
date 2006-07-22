from LunarcPage import LunarcPage
from MiscUtils.Funcs import uniqueId

import os, sys, string, types, time, mimetypes

from HyperText.HTML import *

import Lap, LapWeb, pyARC

class SubmitJobPage(LunarcPage):
	def title(self):
		return 'Submitting job'

	def writeContent(self):

		if self.request().hasField("Job"):
			
			jobName = self.request().field("Job")

			# Get user dir
			
			user = Lap.User(self.session().value('authenticated_user'))
			userDir = user.getDir();
			jobDir = os.path.join(userDir, "job_%s" % jobName)
		
			# Read the job task 
			
			taskFile = file(os.path.join(jobDir,"job.task"), "r")
			task = pickle.load(taskFile)
			taskFile.close()
		
			ARC = pyARC.Ui(user)
			result = ARC.submit(os.path.join(jobDir,"job.xrsl"))
			
			LapWeb.messageBox(self, result[0])
	
