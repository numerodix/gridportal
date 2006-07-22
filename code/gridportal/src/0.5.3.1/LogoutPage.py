from LunarcPage import LunarcPage
from MiscUtils.Funcs import uniqueId
import string, types

import LapWeb

class LogoutPage(LunarcPage):
	def title(self):
		return 'Log Out'

	def writeContent(self):

		self.session().values().clear()

		LapWeb.messageBox(self, "You have been logged out.", "Information")		

				

