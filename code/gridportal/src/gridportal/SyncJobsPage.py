from SecurePage import SecurePage
from MiscUtils.Funcs import uniqueId
import string, types

import Lap, LapWeb, pyARC

jobRunningColor   = "51, 204, 0"
jobQueuingColor   = "255, 204, 0"
jobAcceptedColor  = "0, 204, 204"
jobDeletedColor   = "204, 0, 0"
jobFinishingColor = "51, 204, 0"
jobFinishedColor  = "51, 204, 0"

class SyncJobsPage(SecurePage):
	def title(self):
		return 'Syncronising jobs'

	def writeContent(self):
		user = Lap.User(self.session().value('authenticated_user'))
		ui = pyARC.Ui(user)
		
		ui.sync()
		
		self.sendRedirectAndEnd("JobStatus")

