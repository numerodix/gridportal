from LunarcPage import LunarcPage
from MiscUtils.Funcs import uniqueId
import string, types

import Lap
import LapWeb

class LoginPage(LunarcPage):
	def title(self):
		return 'Log In'

	def htBodyArgs(self):
		return LunarcPage.htBodyArgs(self) + ' onload="document.loginform.password.focus();"' % locals()

	def writeContent(self):

		# Any messages to display ? 		

		extra = self.request().field('extra', None)
		
		if not extra and self.request().isSessionExpired() and not self.request().hasField('logout'):
			extra = 'You have been automatically logged out due to inactivity.'
		
		if extra:
			LapWeb.messageBox(self, self.htmlEncode(extra))
			self.writeln("<br>")
			self.writeln("<br>")

		# Create unique loginid			
		
		loginid = uniqueId(self)
		self.session().setValue('loginid', loginid)

		action = self.request().field('action', '')
		
		print "LoginPage " + action

		# Create login form		

		print "Creating login form"
		form = LapWeb.Form(self, "loginform", action, "Login")

#		form.addFile("Proxy file", "proxy")
		form.addText("Username", "name")
		form.addPassword("Password", "password")
		form.addHidden("", "loginid", loginid)
		form.setAction(action)
		form.setSubmitButton("login", "Login")

		# Forward any passed in values to the user's intended page after successful login,
		# except for the special values used by the login mechanism itself
		
		for name, value in self.request().fields().items():
			if name not in 'login loginid proxy extra logout name password'.split():
				if isinstance(value, types.ListType):
					for valueStr in value:
						form.addHidden("", name, valueStr)
				else:
					form.addHidden("", name, value)

		print "Rendering login form"

		form.render()
