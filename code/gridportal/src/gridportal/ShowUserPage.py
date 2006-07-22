from SecurePage import SecurePage
from time import *
import os
import sys
import pwd
import time

from HyperText.HTML import *

import pyARC
import Lap
import LapWeb

class ShowUserPage(SecurePage):

	def writeContent(self):

		DNString = self.session().value('authenticated_user')

		DN = pyARC.DN(DNString)	

		user = Lap.User(DNString)
		proxy = pyARC.Proxy(user.getProxy())

		table = LapWeb.Table(self, 4, 2, "User information")

		table.setItem(0,0,"Name")
		table.setItem(0,1,DN.getName())
		table.setItem(1,0,"DN")
		table.setItem(1,1,DNString)
		table.setItem(2,0,"Proxy remaining")
		table.setItem(2,1,time.strftime("%H:%M:%S",time.gmtime(proxy.getTimeleft())))

		table.render()
