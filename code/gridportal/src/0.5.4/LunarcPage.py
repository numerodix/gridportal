from WebKit.Page import Page

import string, os

from HyperText.HTML import *

import Lap
import LapWeb

class LunarcPage(Page):
	
	def awake(self, transaction):
		Page.awake(self, transaction)

	def writeStyleSheet(self):
		self.writeln(LINK(rel="stylesheet",href="css/lap.css"))
		self.writeln(LINK(rel="stylesheet",href="smartmenu/smartmenu.css"))
		
	def htBodyArgs(self):
		return ""
		pass
		
	def writeBodyParts(self):
		self.writeln('<script type="text/javascript" language="JavaScript1.2" src="smartmenu/s_loader.js"></script>');
		self.writeln('<div style="position: absolute; top:120px; left:0px; width:100%; background-color:#eff3ff; padding-top:5px; padding-bottom:5px; z-index:0;">&nbsp;</div>');
		
		self.writeln(IMG(src="images/logo2.jpg",style="position:absolute; left:0px; top:0px; width: 554px; height: 120px;",alt="GRIDportal logo"))
		self.writeln('<DIV id="workarea">')
		
		
		# Verify that servlet is accessed through https
		# This is really a bad hack to check for a secure connection,
		# should be handled through http server configuration instead
		
		# get request environment variables
		request = self.request()
		env = request.environ()
			
		# request not through https
		if (not self.isHttps(env)):
			
			self.writeln("<strong>Woops</strong>")
			self.writeln("<p>This page must be accessed through a secure connection (https). Click the link below to proceed.</p>")
			
# 			url = "https://" + env["SERVER_NAME"] + env["REQUEST_URI"]
			url = "https://" + request.serverURL()
			
			self.writeln("<a href=" + url + ">" + url + "</a>")
			
		# request through https
		else:

			self.writeContent()
			
		
		self.writeln('</DIV>')

#		self.writeln('<DIV id="messagearea">')
#		self.writeln("LAP Version %d.%d.%d" % (Lap.majorVersion, Lap.minorVersion, Lap.releaseVersion))
#		self.writeln('</DIV>')

	def isHttps(self, env):

		# more ways to check for https connection:
# 		self.writeln(env)
		
		if (env["SERVER_PORT"] != "443"):
			return 0
			self.writeln("not https")

		return 1