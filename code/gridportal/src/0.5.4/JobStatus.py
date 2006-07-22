from SecurePage import SecurePage
from MiscUtils.Funcs import uniqueId
import string, types, time

import Lap, LapWeb, pyARC

jobRunningColor   = "51, 204, 0"
jobQueuingColor   = "255, 204, 0"
jobAcceptedColor  = "0, 204, 204"
jobDeletedColor   = "255, 100, 100"
jobFinishingColor = "51, 204, 0"
jobFinishedColor  = "51, 204, 0"

class JobStatus(SecurePage):
	def title(self):
		return 'Job status'

	def writeContent(self):
		user = Lap.User(self.session().value('authenticated_user'))
		ui = pyARC.Ui(user)
		jobs = ui.jobStatus()
		
		if jobs==None:
			LapWeb.messageBox(self, "grid-proxy is about to expire please create a new proxy.", "Manage running jobs")
		
		elif len(jobs)==0:
			
			# No job dirs exists => no jobs to manage
			
			LapWeb.messageBox(self, "No jobs found on grid.", "Get jobs")
		
		else:
	
			# if user loads this page before ARC is done processing it, an error
			# occurs because the information is not yet available
		
			try:
				for key in jobs.keys():
					name = jobs[key]["name"]
			except KeyError:
				err = "Job information not yet available, please wait"
				err += " a moment and then synchronize job list."
				LapWeb.messageBox(self, err, "Manage running jobs")
				return

			table = LapWeb.Table(self,len(jobs)+1,4)
			table.setItem(0, 0, "ID")
			table.setItem(0, 1, "Name")
			table.setItem(0, 2, "Status")
			table.setItem(0, 3, "Error")
	
			row = 1		
	
			for key in jobs.keys():
				table.setItem(row,0,key)
				table.setItem(row,1,jobs[key]["name"])
				if jobs[key]["status"] == "INLRMS:Q":
					table.setColor(row,2, jobQueuingColor)
				if jobs[key]["status"] == "INLRMS:wait":
					table.setColor(row,2, jobQueuingColor)
				if jobs[key]["status"] == "INLRMS:R":
					table.setColor(row,2, jobRunningColor)
				if jobs[key]["status"] == "ACCEPTED":
					table.setColor(row,2, jobAcceptedColor)
				if jobs[key]["status"] == "DELETED":
					table.setColor(row,2, jobDeletedColor)
				if jobs[key]["status"] == "FINISHING":
					table.setColor(row,2, jobFinishingColor)
				if jobs[key]["status"][0:8] == "FINISHED":
					table.setColor(row,2, jobFinishedColor)
				table.setItem(row,2,jobs[key]["status"])
				if jobs.has_key("error"):
					table.setItem(row,3,jobs[key]["error"])
				row = row + 1
			
			table.render()

