"""
  Author:     Martin Matusiak <numerodix@gmail.com>
  Program:    mpiBlast database update script
  Date:       Oct. 12, 2005

  This file is subject to the GNU General Public License (GPL)
  (http://www.gnu.org/copyleft/gpl.html)
"""

import sys, os, shutil, glob
from dbgeneric import BlastDatabase
from conf_sys import ConfigSys

class FastaDatabase(BlastDatabase):
	
	url = None
	
	
	def __init__(self, name, segments, protein=False, flags=None, url=None):
		BlastDatabase.__init__(self, name, segments, protein, flags)
		self.downloaded_filemask = name + ".gz"
		self.unpacked_filemask = name
		self.delete_filemask = None		# don't delete any files
		self.url = url
		
	
	def download(self):
		BlastDatabase.download(self)
		# wget chokes on files >2GB
		# invoke = ConfigSys.wget + " -nc"		# -nc to overwrite existing file with same name
		invoke = ConfigSys.curl + " -O -s "
		if (not self.flag_debug):
			invoke += " -nv"
		invoke += " " + self.url + "/" + self.downloaded_filemask
		self.invokeCmd(invoke)
	

	def unpack(self):
		BlastDatabase.unpack(self)
		invoke = ConfigSys.gunzip + " -f"
		if (self.flag_debug):
			invoke += " -v"
		invoke += " " + self.downloaded_filemask
		self.invokeCmd(invoke)

	
	def formatDb(self):
		BlastDatabase.formatDb(self)
		invoke = ConfigSys.loadmpi + "; "
		invoke += ConfigSys.mpiformatdb + " -N " + str(self.segments)
		invoke += " --config-file " + self.conf_file
		invoke += " -i " + self.name
		if (self.flag_skipreorder):
			invoke += " --skip-reorder"
			
		# declare protein or nucleotide (not necessary to specify but why not)
		if self.protein:
			invoke += " -p T"
		else:
			invoke += " -p F"
			
		self.invokeCmd(invoke)
		
		self.resetDbFormatPath()



if __name__ == "__main__":
	initflags = {}
	initflags["flag_debug"] = True
#	initflags["flag_keep_downloadfiles"] = True
	update_db = FastaDatabase("nr", "http", 20, flags=initflags)
	update_db.startUpdate()