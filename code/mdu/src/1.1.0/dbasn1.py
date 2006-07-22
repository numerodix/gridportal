"""
  Author:     Martin Matusiak <numerodix@gmail.com>
  Program:    mpiBlast database update script
  Date:       Oct. 12, 2005

  This file is subject to the GNU General Public License (GPL)
  (http://www.gnu.org/copyleft/gpl.html)
"""

import sys, os, shutil, glob, ftp
from dbgeneric import BlastDatabase
from conf_user import Config
from conf_sys import ConfigSys

class Asn1Database(BlastDatabase):
	
	volumes = 1

	
	def __init__(self, name, segments, flags=None, protein=False):
		BlastDatabase.__init__(self, name, segments, protein, flags)
		self.downloaded_filemask = name + "*.tar.gz"
		self.unpacked_filemask = name + "*.[p,n]??"
		self.delete_filemask = self.downloaded_filemask
		

	def download(self):
		BlastDatabase.download(self)
		ftp.getDB(self.name, Config.ncbi_formatted, self.workdir, self.flag_debug)

		
	def unpack(self):
		BlastDatabase.unpack(self)
		
		files = self.myglob(self.downloaded_filemask, self.workdir)
		for i in files:
			invoke = ConfigSys.tar + " zxf"
			if (self.flag_debug):
				invoke += "v"
			invoke += " " + i
			self.invokeCmd(invoke)

			
	def formatDb(self):
		invoke = ""
		if (self.flag_skipreorder):
			invoke += self.getCombinedFormatString(self.name)
		else:
			self.dumpToFasta(self.name)
			invoke += self.getSingleFormatString(self.name)

		# declare protein or nucleotide
		# only necessary when piping from fastacmd to mpiformatdb, but
		# making it a general requirement for consistency
		if self.protein:
			invoke += " -p T"
		else:
			invoke += " -p F"

		BlastDatabase.formatDb(self)
		self.invokeCmd(invoke)
		
		self.resetDbFormatPath()
		
		
	def dumpToFasta(self, dbname):
		self.printMessage("Dumping " + dbname + " into fasta format")
		invoke = ConfigSys.fastacmd + " -D -d " + dbname
		invoke += " -o " + dbname
		self.invokeCmd(invoke)
		
	
	def getSingleFormatString(self, dbname):
		invoke = ConfigSys.loadmpi + "; "
		invoke += self.getCommonFormatDBstring(dbname)
		invoke += " -i " + dbname
		return invoke
			
		
	def getCombinedFormatString(self, dbname):
		invoke = ConfigSys.loadmpi + "; "
		invoke += ConfigSys.fastacmd + " -D T -d " + dbname + " | "
		invoke += self.getCommonFormatDBstring(dbname)
		invoke += " -i stdin"
		invoke += " --skip-reorder"
		return invoke
	
	
	def getCommonFormatDBstring(self, dbname):
		invoke = ConfigSys.mpiformatdb + " -N " + str(self.segments)
		invoke += " --config-file " + self.conf_file
		invoke += " -t " + dbname
		return invoke
		


if __name__ == "__main__":
	update_db = Asn1Database("nr", 20, protein=True)
#	update_db.startUpdate()