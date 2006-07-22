#!/usr/bin/python

"""
  Author:     Martin Matusiak <numerodix@gmail.com>
  Program:    myproxy register utility
  Date:       Nov. 28, 2005

  This file is subject to the GNU General Public License (GPL)
  (http://www.gnu.org/copyleft/gpl.html)

  myproxyreg.py is meant for use with generatecerts.py. It looks for
  files named NameA.cert and NameA.key, then stores each pair on the
  myproxy host.

  After completion, the user should be able to log into GRIDportal with
  username the name portion of NameA.cert, ie:

  username: NameA
  password; demopassA
"""

import os, glob

myproxy_host = "norgrid.ntnu.no"

certs = glob.glob("*.cert")
keys = glob.glob("*.key")

certs.sort()
keys.sort()

for i in range(0, len(certs)):
	(name, ext) = certs[i].split('.')
# 	print name
	invoke = "myproxy-store -s " + myproxy_host + " -l " + name
	invoke += " -c " + certs[i] + " -y " + keys[i]
	print invoke
	os.system(invoke)
