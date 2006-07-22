from SecurePage import SecurePage
from MiscUtils.Funcs import uniqueId
import string, types

import LapWeb

class LoginPageDummy(SecurePage):
	def title(self):
		return 'Logged in'

	def writeContent(self):

		LapWeb.messageBox(self, "You have been logged in.")

				

