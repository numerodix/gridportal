#!/usr/bin/python

"""
  Author:     Martin Matusiak <numerodix@gmail.com>
  Program:    mpiBlast database update script
  Date:       Oct. 12, 2005

  This file is subject to the GNU General Public License (GPL)
  (http://www.gnu.org/copyleft/gpl.html)
"""

import sys, os, shutil, output, time
import dbfasta, dbasn1, dbgeneric
from conf_user import Config

class UpdateDatabase:

	appVersion = "1.0.0"
	appName = "[m]piBlast [D]atabase [U]pdater (mdu)"
	
	args = {
		"--dry-run": "Abort before installing databases",
		"--start": "Start update of databases declared in conf_user.py",
		"--skip-reorder": "Do not reorder database",
		"--debug": "Turn on debug output",
		"--keep-downloaded": "Do not delete downloaded files",
		"--use-downloaded": "Use files from prior download if found",
		"--no-color": "Turns off color ouput",
		"--help": "Displays this info"
	}
	
	initflags = {}
	initflags["flag_debug"] = False
	initflags["flag_dryrun"] = False
	initflags["flag_keep_downloadfiles"] = False
	initflags["flag_use_downloadfiles"] = False
	initflags["flag_nocolor"] = False
	initflags["flag_skipreorder"] = False
	
	key_width = 22
	flag_width = 30
	
	wait_sec = 10		# flash info screen for number of seconds
	
	
	def __init__(self):
		self.setupPrinter()
		self.parseArgs()

		
	def run(self):
		self.printAppName()
		
		print output.white("\nDatabases selected for install/update:")
		for db in Config.update_dbs:
			print output.green(" * ") + db
			
		self.printFlags()
		
		print output.green("\nI'm giving you " + str(self.wait_sec) + " seconds to change your mind..\n")
		time.sleep(self.wait_sec)
		
		for db in Config.update_dbs:
			
			update_db = None

			prot = False
			if Config.databases[db].has_key("protein"):
				prot = True
			
			# database is fasta
			if Config.databases[db].has_key("fasta"):
				update_db = dbfasta.FastaDatabase(
					db,
					Config.databases[db]["segments"],
					protein=prot,
					url=Config.ncbi_fasta,
					flags=self.initflags
				)
			
			# database is pre-formatted
			else:
				update_db = dbasn1.Asn1Database(
					db,
					Config.databases[db]["segments"],
					protein=prot,
					flags=self.initflags
				)
				
			update_db.startUpdate()
		
		
	def parseArgs(self):

		# print help if no arguments
		if len(sys.argv) < 2:
			self.printUsage()

		# disable color output
		if "--no-color" in sys.argv:
			output.nocolor()
			self.initflags["flag_nocolor"] = True
			if len(sys.argv) == 2:
				self.printUsage()

		# display help
		if "--help" in sys.argv:
			self.printUsage()
			
		# turn on debug output
		if "--debug" in sys.argv:
			self.initflags["flag_debug"] = True
			
		# make it a dry run, no install
		if "--dry-run" in sys.argv:
			self.initflags["flag_dryrun"] = True
			
		# keep downloaded files
		if "--keep-downloaded" in sys.argv:
			self.initflags["flag_keep_downloadfiles"] = True
			
		# don't download files if they exist
		if "--use-downloaded" in sys.argv:
			self.initflags["flag_use_downloadfiles"] = True
			
		# skip reorder
		if "--skip-reorder" in sys.argv:
			self.initflags["flag_skipreorder"] = True
			
		# start database update
		if "--start" in sys.argv:
				self.run()

	
	def printUsage(self):
		self.printAppName()
		print output.green("\nUsage: ") + output.teal(sys.argv[0]) + " [" + output.white("args") + "]\n"
		keys = self.args.keys()
		keys.sort()
		for i in keys:
			print "  " + output.white(i) + " "*(self.key_width-len(i)) + self.args[i]
		sys.exit()
		
		
	def printFlags(self):
		print output.white("\nFlags set:")
		keys = self.initflags.keys()
		keys.sort()
		for key in keys:
			if self.initflags[key]:
				print key + " "*(self.flag_width-len(str(key))) + output.green("On")
			else:
				print key + " "*(self.flag_width-len(str(key))) + output.red("Off")


	def printAppName(self):
		print output.white(self.appName + " " + self.appVersion)
		

	def setupPrinter(self):
		""" It's necessary to flush the stdout/stderr buffers for every print() because
			mdu executes external shell commands and these would otherwise be buffered
			separately, making the ouput from mdu difficult to understand """
		sys.stdout = RedirectStream(sys.stdout)
		sys.stderr = RedirectStream(sys.stderr)



class RedirectStream:
	""" Helper class to override sys.stdout/sys.stderr print routine """
	
	def __init__(self, instream):
		self.old_stream = instream
		self.stream = instream

	def write(self, s):
		self.stream.write(s)
		self.old_stream.flush()



if __name__ == "__main__":
	upload_db = UpdateDatabase()

