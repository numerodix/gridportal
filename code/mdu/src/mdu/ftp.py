"""
  Author:     Martin Matusiak <numerodix@gmail.com>
  Program:    mpiBlast database update script
  Date:       Nov. 3, 2005

  This file is subject to the GNU General Public License (GPL)
  (http://www.gnu.org/copyleft/gpl.html)
"""

import re, os
from conf_user import Config
from conf_sys import ConfigSys
from ftplib import FTP
from urlparse import urlparse


globals()['flag_debug'] = False

def getDB(dbname, url, localpath, debug=False):
	globals()['flag_debug'] = debug

	(protocol, host, path) = parseUrl(url)
	getfiles = []
	
	printMsg('Establishing connection to ftp server')
	ftp = FTP(host)
	
	printMsg('Logging into ftp server')
	ftp.login()
	
	printMsg('Changing cwd to ' + path)
	ftp.cwd(path)
	
	printMsg('Print file list for ' + path)
	printMsg(ftp.retrlines('LIST'))
	
	files = ftp.nlst()
	printMsg('Files selected for download:')
	
	for file in files:
		if re.search('^' + dbname, file):
			getfiles.append(file)
			size = ftp.size(file) / (1024*1024)
			printMsg(" + " + file + " (" + str(size) + " MB)")
	
	printMsg('Closing connection to ftp server')
	ftp.close()
	
	for file in getfiles:
		url = protocol + '://' + host + path + file
#		getWget(url, file, localpath, debug)
		getCurl(url, file, localpath, debug)


def getCurl(url, file, localpath, debug):
	invoke = "cd " + localpath + " && "
	invoke += "rm -rf " + file + " && "
	invoke += ConfigSys.curl + " -O -s"
	if (debug):
		invoke += " -v"
	invoke += " " + url
	printMsg("running: " + invoke)
	os.system(invoke)


def getWget(url, file, localpath, debug):
	invoke = "cd " + localpath + " && "
	invoke += "rm -rf " + file + " && "
	invoke += ConfigSys.wget + " -nc"
	if (not debug):
		invoke += " -nv"
	invoke += " " + url
	printMsg("running: " + invoke)
	os.system(invoke)


def parseUrl(url):
	(protocol, host, path, params, query, fragment) = urlparse(url)
	return (protocol, host, path)


def printMsg(msg):
	if globals()['flag_debug']:
		print msg


#getDB('nr', 'ftp://ftp.ncbi.nlm.nih.gov/blast/db/')
#getDB('FC4-i386-DVD.iso',
#	'ftp://mirror.linux.duke.edu/pub/fedora/linux/core/4/i386/iso')

"""
#server = 'ftp.ncbi.nlm.nih.gov'
server = 'mirror.linux.duke.edu'

#path = 'blast/db'
path = 'pub/fedora/linux/core/4/i386/iso'

#file = 'refseq_protein'
#file = 'nt'
#file = 'swissprot'
file = 'FC4-i386-DVD.iso'

ftp = FTP(server)
print ftp.login()
print ftp.cwd(path)
print ftp.nlst()
files = ftp.nlst()
for i in files:
#	if file in i:
#		print i
	if re.search('^' + file, i):
		print i
#print ftp.retrlines('LIST')
#print file
print ftp.size(file)
print ftp.retrbinary('RETR ' + file, open(file, 'wb').write)
print ftp.quit()
"""