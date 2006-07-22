from SecurePage import SecurePage
from MiscUtils.Funcs import uniqueId

import os, sys, shutil, string, types, time, mimetypes

from HyperText.HTML import *

import Lap, LapWeb, LapUtils, pyARC

jobRunningColor   = "51, 204, 0"
jobQueuingColor   = "255, 204, 0"
jobAcceptedColor  = "0, 204, 204"
jobDeletedColor   = "204, 0, 0"
jobFinishingColor = "51, 204, 0"
jobFinishedColor  = "51, 204, 0"

class ViewFilesPage(SecurePage):
	def title(self):
		return 'File view'

	def writeContent(self):
		
		
		if self.session().hasValue("viewfiles_status"):

			# Show any form message boxes
			
			if self.session().value("viewfiles_status")<>"":
				LapWeb.messageBox(self, self.session().value("viewfiles_status"), "Message", "ViewFilesPage")

			self.session().delValue("viewfiles_status")
			
		else:
		
			# Get user information
		
			user = Lap.User(self.session().value('authenticated_user'))
			userDir = user.getDir();
			
			mode = ""
			jobName = ""
			viewMode = ""
			
			if self.session().hasValue("viewfiles_mode"):
				viewMode = self.session().value("viewfiles_mode")
				
				
			if self.session().hasValue("viewfiles_jobname"):
				jobName = self.session().value("viewfiles_jobname")
				
			if viewMode == "view":
				
				if self.session().hasValue("viewfiles_entry"):
					
					jobDir = os.path.join(userDir, "job_%s" % jobName)
					sessionDir = os.path.join(jobDir, self.session().value("viewfiles_sessiondir"))
					subDir = self.session().value("viewfiles_dir")
					browseDir = os.path.join(sessionDir, subDir)
					fileEntry = self.session().value("viewfiles_entry")
					
					#filename = os.path.join(browseDir, fileEntry)
					filename = fileEntry
					
					if os.path.isfile(filename):
						
						viewFile = file(filename, "r")
						content = viewFile.readlines()
						viewFile.close()
						
						# Create a form for viewing the file
						
						form = LapWeb.Form(self, "frmFileManager", "", os.path.basename(fileEntry), 80, 1, 3)
						form.setWindowID("formViewWindow")
						form.setContentID("formViewContent")
						form.setTableLayout(True)
						form.setAction("ViewFilesPage")

						pre = PRE()
						
						for line in content:
							pre.append(line)
						
						form.addNormalText(pre, 0, 1)
						form.addFormButton("Back", "_action_goBack")
						form.render()
	
			if viewMode == "file":
				
				if self.session().hasValue("viewfiles_sessiondir"):
							
					jobDir = os.path.join(userDir, "job_%s" % jobName)
					sessionDir = os.path.join(jobDir, self.session().value("viewfiles_sessiondir"))
					subDir = self.session().value("viewfiles_dir")
					browseDir = os.path.join(sessionDir, subDir)
					
					fileList = []
					dirList = []
					
					for entry in os.listdir(browseDir):
						atime = os.path.getatime(os.path.join(browseDir,entry))
						mtime = os.path.getmtime(os.path.join(browseDir,entry))
						fsize = os.path.getsize(os.path.join(browseDir,entry))
						if os.path.isdir(os.path.join(browseDir,entry)):
							dirList.append((entry, atime, mtime, fsize, os.path.join(browseDir,entry)))
						else:
							fileList.append((entry, atime, mtime, fsize, os.path.join(browseDir,entry)))
							
					# Check for files
					
					if len(fileList)==0 and len(dirList)==0:
						LapWeb.messageBox(self, "No files to display", "View files", "ManageJobPage")
						return
		
					# Create form for viewing session directories
					
					form = LapWeb.Form(self, "frmFileManager", "", "Downloaded job files", 50, len(dirList)+len(fileList)+1, 6)
					form.setTableLayout(True)
					form.setAction("ViewFilesPage")
					
					row = 0
					
					form.addNormalText("Type", row, 1)
					form.addNormalText("File", row, 2)
					form.addNormalText("Size", row, 3)
					form.addNormalText("Last accessed", row, 4)
					form.addNormalText("Last modified", row, 5)
					
					row = row + 1 
					
					for dirEntry in dirList:
						form.addRadio("", "chkEntry", dirEntry[4], row, 0)
						form.addNormalText(IMG(src="icons/small/dir.gif"), row, 1)
						form.addNormalText(dirEntry[0], row, 2)
						form.addNormalText(dirEntry[3], row, 3)
						form.addNormalText(time.asctime(time.localtime(dirEntry[1])), row, 4)
						form.addNormalText(time.asctime(time.localtime(dirEntry[2])), row, 5)
						row = row + 1
						
					for fileEntry in fileList:
						form.addRadio("", "chkEntry", fileEntry[4], row, 0)
						form.addNormalText(IMG(src="icons/small/generic.gif"), row, 1)
						form.addNormalText(fileEntry[0], row, 2)
						form.addNormalText(fileEntry[3], row, 3)
						form.addNormalText(time.asctime(time.localtime(fileEntry[1])), row, 4)
						form.addNormalText(time.asctime(time.localtime(fileEntry[2])), row, 5)
						row = row + 1

					form.addFormButton("View", "_action_viewFile")
					form.addFormButton("Download", "_action_downloadFile")
					form.addFormButton("Download all (tar.gz)", "_action_downloadAll")
					form.addFormButton("Back", "_action_goBack")
					
					form.setHaveSubmit(False)
						
					form.render()
							
						
			if viewMode == "session":
				
				# Get job directory
	
				jobDir = os.path.join(userDir, "job_%s" % jobName)
				
				# Check for session directories
				
				sessionDirs = []
				
				for entry in os.listdir(jobDir):
					if os.path.isdir(os.path.join(jobDir,entry)):
						atime = os.path.getatime(os.path.join(jobDir,entry))
						mtime = os.path.getmtime(os.path.join(jobDir,entry))
						sessionDirs.append((entry, atime, mtime))
						
				if len(sessionDirs)==0:
					LapWeb.messageBox(self, "No downloaded results to display", "View files", "ManageJobPage")
					return
				
				# Intialise file viewing
				
				self.session().setValue("viewfiles_dir", "")
	
				# Create form for viewing session directories
				
				form = LapWeb.Form(self, "frmFileManager", "", "Downloaded job files", 50, len(sessionDirs)+1, 4)
				form.setTableLayout(True)
				form.setAction("ViewFilesPage")
				
				row = 0
				
				form.addNormalText("JobID", row, 1)
				form.addNormalText("Last accessed", row, 2)
				form.addNormalText("Last modified", row, 3)
				
				row = row + 1 
				
				for sessionDir in sessionDirs:
					form.addRadio("", "chkDir", sessionDir[0], row, 0)
					form.addNormalText(sessionDir[0], row, 1)
					form.addNormalText(time.asctime(time.localtime(sessionDir[1])), row, 2)
					form.addNormalText(time.asctime(time.localtime(sessionDir[2])), row, 3)
					row = row + 1
					
				form.addFormButton("View", "_action_viewDir")
				form.addFormButton("Delete", "_action_deleteDir")
				form.addFormButton("Back", "_action_goBack")
				
				form.setHaveSubmit(False)
					
				form.render()
			
	def viewDir(self):
		
		if self.request().hasValue("chkDir"):
			
			sessionDir = self.request().value("chkDir")
		
			self.session().setValue("viewfiles_sessiondir", sessionDir)
			self.session().setValue("viewfiles_mode", "file")
			
			self.writeBody()
		else:
			self.setFormStatus("A directory must be selected.")
			self.writeBody()

	
	def deleteDir(self):
		
		# Get session dir
			
		jobName = self.session().value("viewfiles_jobname")
		user = Lap.User(self.session().value('authenticated_user'))
		userDir = user.getDir();
		jobDir = os.path.join(userDir, "job_%s" % jobName)
	
		# Check for session directories
		
		for entry in os.listdir(jobDir):
			if os.path.isdir(os.path.join(jobDir,entry)):
				sessionDir = os.path.join(jobDir,entry)
				shutil.rmtree(sessionDir, True)
				
		# Print delete confirmation
		
		self.setFormStatus("Job files deleted.")
		self.writeBody()		
	
	def downloadFile(self):
		if self.request().hasValue("chkEntry"):
			
			jobName = self.session().value("viewfiles_jobname")
			user = Lap.User(self.session().value('authenticated_user'))
			userDir = user.getDir();
			jobDir = os.path.join(userDir, "job_%s" % jobName)
			sessionDir = os.path.join(jobDir, self.session().value("viewfiles_sessiondir"))
			subDir = self.session().value("viewfiles_dir")
			browseDir = os.path.join(sessionDir, subDir)
			fileEntry = self.request().value("chkEntry")
			
			
			print "downloadFile() start = ", fileEntry
			
			if os.path.isdir(fileEntry):
				self.setFormStatus("A file must be selected.")
				self.writeBody()
				return
			
			self.session().setValue("viewfiles_downloadfile", fileEntry)
			self.forward("FileDownloadPage")
		else:
			self.setFormStatus("A file/directory must be selected.")
			self.writeBody()

	
	def downloadAll(self):
		
		jobName = self.session().value("viewfiles_jobname")
		user = Lap.User(self.session().value('authenticated_user'))
		userDir = user.getDir();
		jobDir = os.path.join(userDir, "job_%s" % jobName)
		sessionDir = os.path.join(jobDir, self.session().value("viewfiles_sessiondir"))
		subDir = self.session().value("viewfiles_dir")
		browseDir = os.path.join(sessionDir, subDir)
		
		if subDir!="":
			filename = "%s_%s_%s" % (jobName, os.path.basename(sessionDir), subDir)
		else:
			filename = "%s_%s" % (jobName, os.path.basename(sessionDir))
		
		oldDir = os.getcwd()
		os.chdir(browseDir)
		os.system("tar cvzf /tmp/%s.tar.gz ." % (filename))
		os.chdir(oldDir)
		
		fullFilename = "/tmp/"+filename + ".tar.gz"
		
		self.session().setValue("viewfiles_downloadfile", fullFilename)
		self.forward("FileDownloadPage")
	
	def viewFile(self):

		if self.request().hasValue("chkEntry"):
			
			jobName = self.session().value("viewfiles_jobname")
			user = Lap.User(self.session().value('authenticated_user'))
			userDir = user.getDir();
			jobDir = os.path.join(userDir, "job_%s" % jobName)
			sessionDir = os.path.join(jobDir, self.session().value("viewfiles_sessiondir"))
			subDir = self.session().value("viewfiles_dir")
			browseDir = os.path.join(sessionDir, subDir)
			
			fileEntry = self.request().value("chkEntry")
			
			if os.path.isdir(fileEntry):
				subDir = os.path.join(subDir, os.path.basename(fileEntry))
				self.session().setValue("viewfiles_dir", subDir)
			else:
				self.session().setValue("viewfiles_entry", fileEntry)
				self.session().setValue("viewfiles_mode", "view")
			
			self.writeBody()
		else:
			self.setFormStatus("A file/directory must be selected.")
			self.writeBody()


	def goBack(self):
		
		viewMode = self.session().value("viewfiles_mode")
		
		if viewMode == "session":
			self.sendRedirectAndEnd("ManageJobPage")
			return
		
		if viewMode == "file":
			
			orgDir = self.session().value("viewfiles_dir")
			newDir = os.path.dirname(orgDir)
			
			if orgDir!="":
				self.session().setValue("viewfiles_mode", "file")
				self.session().setValue("viewfiles_dir", newDir)
			else:
				self.session().setValue("viewfiles_mode", "session")
				self.session().setValue("viewfiles_dir", "")
				
			self.writeBody()
			
		if viewMode == "view":
			self.session().setValue("viewfiles_mode", "file")
			
			self.writeBody()
			
	def actions(self):
		return SecurePage.actions(self) + ["viewDir",
										   "deleteDir",
										   "downloadFile",
										   "downloadAll",
										   "viewFile",
										   "goBack"]
		
	def setFormStatus(self, status):
		self.session().setValue("viewfiles_status", status)


