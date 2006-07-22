import string
import os
import sys
import pickle
from conf_main import *

import pyARC
import LapUtils
import LapTemplates

majorVersion   = 0
minorVersion   = 5
releaseVersion = 3
textVersion    = ""

description    = "GRIDportal, the NTNU Application Portal"
website				= "(<a href='http://gridportal.dynalias.org/'>http://gridportal.dynalias.org/</a>)"
fork					= "A fork of Jonas Lindemann's \"LUNARC Application Portal\""
copyright      = "Copyright &#169 2005 High Performance Computing Project, NTNU"
license	       = "GNU Public License version 2 or later"
author         = "Martin Matusiak"

credits1	   = "LUNARC Application Portal by Jonas Lindemann"
credits2	   = "Web application developed in WebWare for Python"
credits3	   = "Grid access through NorduGrid/ARC middleware (client)"
credits4	   = "MyProxy integration through pyGlobus"
credits5	   = "HyperText HTML code generation library by John A. (Andy) Dustman"
credits6	   = "DHTML Menus (SmartMenus) by Vasil Dinkov (www.smartmenus.org)"


"""Class for handling the user setup structure

The class will create the necessary directory structure for
storing the user proxy, job directories etc. The directory
structure is created from information in the DN. For example:

/{Organisation}/{Org.unit}/{Username}
"""
class User:
	def __init__(self, DNString):

		os.chdir(env_homepath)
		DN = pyARC.DN(DNString)		
		self.setDN(DN)

		self.prefs = {}

		self.prefs["preferredCluster"] = "norgrid.ntnu.no"
		self.prefs["defaultMail"] = ""
		self.prefs["submitTimeout"] = 4

		self.createDir()

		# Load preferences if they exist

		self.loadPrefs()

	def setDN(self, DN):
		self.DN = DN
		
		# Make sure we don't have any spaces in the directory name
		
		self.userDirname = string.replace(self.DN.getName(), " ", "_")
		
		# Create the directory
		
		self.userDir = os.getcwd()+"/%s/%s/%s" % (self.DN.getOrganisation2(), self.DN.getOrganisationalUnit(), self.userDirname)

	def checkAndMkdir(self, directory):
		if not os.path.exists(directory):
			os.mkdir(directory)

	def createDir(self):
		if self.DN!=None:
			if not os.path.exists(self.userDir):
				self.checkAndMkdir(os.getcwd()+"/%s" % self.DN.getOrganisation2())
				self.checkAndMkdir(os.getcwd()+"/%s/%s" % (self.DN.getOrganisation2(), self.DN.getOrganisationalUnit()))
				self.checkAndMkdir(os.getcwd()+"/%s/%s/%s" % (self.DN.getOrganisation2(), self.DN.getOrganisationalUnit(), self.userDirname))
				self.savePrefs()



	def exists(self):
		return os.path.isdir(os.getcwd()+"/%s/%s/%s" % (self.DN.getOrganisation2(), self.DN.getOrganisationalUnit(), self.userDirname))

	def getDir(self):
		return self.userDir

	def getProxy(self):
		return self.userDir+"/lap_proxy"
		
	def setPreferredCluster(self, cluster):
		self.prefs["preferredCluster"] = cluster
		
	def getPreferredCluster(self):
		return self.prefs["preferredCluster"]
		
	def setDefaultMail(self, mail):
		self.prefs["defaultMail"] = mail
		
	def getDefaultMail(self):
		return self.prefs["defaultMail"]
		
	def setSubmitTimeout(self, timeout):
		self.prefs["submitTimeout"] = timeout
		
	def getSubmitTimeout(self):
		return self.prefs["submitTimeout"]
		
	def savePrefs(self):
		
		userPrefFile = file(os.path.join(self.userDir, "user.prefs"), "w")
		pickle.dump(self.prefs, userPrefFile)
		userPrefFile.close()
		
	
	def loadPrefs(self):

		if os.path.exists(os.path.join(self.userDir, "user.prefs")):
			userPrefFile = file(os.path.join(self.userDir, "user.prefs"), "r")
			self.prefs = pickle.load(userPrefFile)
			userPrefFile.close()
		else:
			self.savePrefs()
		
