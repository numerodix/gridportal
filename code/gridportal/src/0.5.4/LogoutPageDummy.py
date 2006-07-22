from LunarcPage import LunarcPage
from MiscUtils.Funcs import uniqueId
import string, types

class LoginPageDummy(LunarcPage):
	def title(self):
		return 'Logged in'

	def writeContent(self):

		self.write("You have been logged in.!")

				

