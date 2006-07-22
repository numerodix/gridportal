from LunarcPage import LunarcPage
from time import *
import os
import sys
import pwd
import time

from HyperText.HTML import *

import pyARC
import Lap
import LapWeb

class DocumentationPage(LunarcPage):
	
	def title(self):
		return "LAP Documentation"
	
	def writeContent(self):

		table = LapWeb.Table(self, 4, 2)

		table.setItem(0,0,"Documentation")
		table.setItem(0,1,"Description")
		table.setItem(1,0,"Getting a certificate")
		table.setItem(1,1,"Describes the procedure for getting a signed certificate that can be used to run jobs on the grid")
		table.setItem(2,0,"LAP User's guide")
		table.setItem(2,1,"Describes how to run and manage jobs using the LUNARC Application portal.")
		table.setItem(3,0,"LAP Programmers's guide")
		table.setItem(3,1,"Describes how to develop job plugins for extending the functionality of the portal.")

		table.render()
	
