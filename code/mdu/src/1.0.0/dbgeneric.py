"""
  Author:     Martin Matusiak <numerodix@gmail.com>
  Program:    mpiBlast database update script
  Date:       Oct. 12, 2005

  This file is subject to the GNU General Public License (GPL)
  (http://www.gnu.org/copyleft/gpl.html)
"""

import sys, os, shutil, glob, output
from conf_user import Config
from conf_sys import ConfigSys

class BlastDatabase:

	flag_debug = False
	flag_dryrun = False
	flag_keep_downloadfiles = False
	flag_use_downloadfiles = False
	flag_skipreorder = False
	
	name = ""
	protein = False
	segments = 1
	
	downloaded_filemask = None
	unpacked_filemask = None
	delete_filemask = None
	
	script_cwd = None
	workdir = None
	temp_shared = None
	temp_local = None
	
	conf_file = "mpiformatdb.conf"
	

	def __init__(self, name, segments, protein=False, flags=None):
		self.script_cwd = os.getcwd()
		self.workdir = self.getAbsPath(Config.workdir)
		self.temp_shared = self.getAbsPath(Config.temp_shared)
		self.temp_local = self.getAbsPath(Config.temp_local)
		self.conf_file = self.script_cwd + "/" + self.conf_file

		self.name = name
		self.segments = segments
		self.protein = protein
		if (not flags == None):
			if (flags.has_key("flag_debug")):
				self.flag_debug = flags["flag_debug"]
			if (flags.has_key("flag_dryrun")):
				self.flag_dryrun = flags["flag_dryrun"]
			if (flags.has_key("flag_keep_downloadfiles")):
				self.flag_keep_downloadfiles = flags["flag_keep_downloadfiles"]
			if (flags.has_key("flag_use_downloadfiles")):
				self.flag_use_downloadfiles = flags["flag_use_downloadfiles"]
			if (flags.has_key("flag_skipreorder")):
				self.flag_skipreorder = flags["flag_skipreorder"]

	
	def startUpdate(self):
		pass
		self.printBanner("Updating " + self.name)
		self.setupConfFile()
		self.createTmp()
		self.downloadUnpackDb()
		self.formatDb()
		if (not self.flag_dryrun):
			self.installDb()
			self.cleanTmp()
		
	
	def setupConfFile(self):
		self.printMessage("Setting up config file for use with mpiformatdb")
		content = self.temp_shared + "\n"		# shared storage path
		content += self.temp_local + "\n"		# local storage path
		file = open(self.conf_file, 'w')
		file.write(content)
		file.close()

			
	def downloadUnpackDb(self):
		# check if extracted database files exist
		if self.flag_use_downloadfiles and self.filesExist(self.unpacked_filemask):
			self.printMessage("Non-zero unpacked file(s) found: " + self.unpacked_filemask + ", will not download")

		# no? perhaps it was downloaded already and just needs unpacking
		elif self.flag_use_downloadfiles and self.filesExist(self.downloaded_filemask):
			self.printMessage("Non-zero downloaded file(s) found: " + self.downloaded_filemask + ", will not download")
			self.unpack()

		# download and unpack files
		else:
			self.download()
			self.unpack()

	
	def download(self):
		self.printMessage("Downloading " + self.name)

		
	def filesExist(self, mask):
		files = self.myglob(mask, self.workdir)

#		print files, os.getcwd()
#		print mask

		result = True
		# no files found
		if len(files) < 1:
			result = False
		
		# files found, but at least one is empty
		for i in files:
			if self.mysize(i, self.workdir) == 0:
				result = False
		
		# files seem to be intact
		return result
	

	def unpack(self):
		self.printMessage("Unpacking " + self.name)


	def formatDb(self):
		self.printMessage("Formatting " + self.name)


	def installDb(self):
		self.printMessage("Installing " + self.name + " into " + Config.mpiblast_shared)
		invoke = ConfigSys.mv
		if self.flag_debug:
			invoke += " -v"
		invoke += " " + self.temp_shared + "/* " + Config.mpiblast_shared
		self.invokeCmd(invoke)
		
		
	def resetDbFormatPath(self):
		ext = ".nal"
		if self.protein:
			ext = ".pal"
		file = self.name + ext
		
		invoke = ConfigSys.sed + " -i \"s/"
		invoke += self.temp_shared.replace("/", "\/") + "/"
		invoke += Config.mpiblast_shared.replace("/", "\/") + "/g\""
		invoke += " " + file
		self.invokeCmd(invoke, self.temp_shared)

		
	def createTmp(self):
		self.printMessage("Setting up temporary storage")
		self.createRemoveTmp(1)

		
	def cleanTmp(self):
		self.printMessage("Cleaning temporary files")
		self.createRemoveTmp(0)
		
		
	def createRemoveTmp(self, boolean):
		# create
		if (boolean):
			for dir in (self.temp_shared, self.temp_local):
				if os.path.exists(dir):
					shutil.rmtree(dir)
				os.makedirs(dir)
			if (not os.path.exists(self.workdir)):
				os.makedirs(self.workdir)
			self.setEnvPath("MPIBLAST_SHARED", Config.temp_shared)
			self.setEnvPath("MPIBLAST_LOCAL", Config.temp_local)
#			print os.environ["MPIBLAST_SHARED"]
#			print os.environ["MPIBLAST_LOCAL"]

		# remove
		else:
			for dir in (self.temp_shared, self.temp_local):
				if os.path.exists(dir):
					shutil.rmtree(dir)
			if (self.delete_filemask != None):
				invoke = ConfigSys.rm + " -rf"
				if (self.flag_debug):
					invoke += " -v"
				invoke += " " + self.workdir + "/" + self.delete_filemask
				self.invokeCmd(invoke)
			if (not self.flag_keep_downloadfiles):
				if os.path.exists(self.workdir):
					shutil.rmtree(self.workdir)
			self.setEnvPath("MPIBLAST_SHARED", Config.mpiblast_shared)
			self.setEnvPath("MPIBLAST_LOCAL", Config.mpiblast_local)
#			print os.environ["MPIBLAST_SHARED"]
#			print os.environ["MPIBLAST_LOCAL"]


	######################################################################
	# Shell helper methods
	######################################################################
		
	def invokeCmd(self, cmd, path=None):
		if path == None:
			path = self.workdir
		
		cmd = "cd " + self.getAbsPath(path) + "; " + cmd
		
		# print every command before executing if debug flag set
		if (self.flag_debug):
			print output.blue("running: ") + cmd
		
		# command returns error
		if (os.system(cmd)):
			self.printStatus(0)
#			sys.exit(1)

		# command completes without error
		else:
			self.printStatus(1)
			

	def setEnvPath(self, env, path):
		os.environ[env] = self.getAbsPath(path)
			
			
	def getAbsPath(self, path):
		# path is absolute
		if (path[0] == "/"):
			return path
		# path is relative to current working dir
		else:
			return self.script_cwd + "/" + path
		
		
	def myglob(self, mask, path):
		os.chdir(self.getAbsPath(path))
		files = glob.glob(mask)
		os.chdir(self.script_cwd)		# reset path to script cwd
		return files
		
		
	def mysize(self, file, path):
		os.chdir(self.getAbsPath(path))
		size = os.path.getsize(file)
		os.chdir(self.script_cwd)		# reset path to script cwd
		return size
		
		
	def mysplitext(self, file, path):
		os.chdir(self.getAbsPath(path))
		(root, ext) =os.path.splitext(file)
		os.chdir(self.script_cwd)		# reset path to script cwd
		return (root, ext)
		
		
	######################################################################
	# Methods dealing with output
	######################################################################

	def printBanner(self, text):
		linewidth = 76
		print output.white("+"*linewidth)
		print output.white("+++ ") + output.green(text)
		print output.white("+"*linewidth)

		
	def printMessage(self, text):
		print output.green(" * ") + output.white(text) + " .."

		
	def printStatus(self, success):
		if (success):	
			print " "*70 + output.white("[") + output.green("done") + output.white("]")
		else:
			print " "*70 + output.white("[") + output.red("failed") + output.white("]")



if __name__ == "__main__":
	update_db = BlastDatabase("nr", "http", 20)
	update_db.startUpdate()