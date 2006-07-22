#!/usr/bin/python

"""
  Author:     Martin Matusiak <numerodix@gmail.com>
  Program:    certificate generation utility
  Date:       Nov. 28, 2005

  This file is subject to the GNU General Public License (GPL)
  (http://www.gnu.org/copyleft/gpl.html)

	generatecerts.py will generate a number of fictional certificates, signed
	by a fictional CA, so as to provide a way to create a number of temporary
	certificates for use with GRIDportal. myproxyreg.py will store these
	certificates on the myproxy host, which grants access to GRIDportal.
	
	generatecerts.py creates certificates for users John. A Doe, where the
	middle name initial A changes for every user. The password for each
	certificate is "demopassA", where A changes for every user to match the
	middle name initial, ie.
	
	username: John A. Doe
	password: demopassA
	
	The CA will not be re-created if it exists, but certificates will be.
	
	NOTE: this script will work on a vanilla distribution of OpenSSL, but not
	with Globus' version of OpenSSL.
	
	Running this script still leaves a few steps to be done manually:
	+ transfer dist/ to frontend
	+ copy certificate files <cert>.0 and <cert>.signing_policy to
		/etc/grid-security/certificates
	+ cat map-gridfile >> /etc/grid-security/certificates/local-grid-mapfile
	+ run /opt/nordugrid/sbin/nordugridmap
	+ run myproxyreg.py to store certs on myproxy host
"""

import os, glob


CAdir = "demoCA"
CAcert = "cacert.pem"
CAkey = "private/cakey.pem"
dist = "dist"

mapuser = "marmat"
mapfile = "grid-mapfile"

DNbase = "/O=Grid/O=ARCDEMO"

names = {}
for i in range(65,91):		# A-Z = 65,91
	names["John"+chr(i)] = "John " + chr(i) + ". Doe"


def main():
	makeCA()
	distCA()
		
 	for i in names:
 		makeCert(names[i], i + ".cert", i + ".key", "demopass"+str(i[4]))
 		signCert(i + ".cert", i + ".key", "demopass"+str(i[4]))
 		distCert(i + ".cert", i + ".key")


################################################################
###	CA functions
################################################################

def makeCA():
	makeDirs()
	if os.path.exists(CAdir + "/" + CAcert):
		print " * Will not create CA certificate, exists in file " + CAcert + ":"
		invoke = "cd " + CAdir
		invoke += "; openssl x509 -noout -subject -dates -in " + CAcert
		os.system(invoke)
	else:
		print " * Creating CA certificate:"
		invoke = "cd " + CAdir
		invoke += "; openssl req -x509 -nodes -days 1825"
		invoke += " -subj '" + DNbase + "/CN=ARCDEMO CA'"
		invoke += " -newkey rsa:2048 -keyout " + CAkey + " -out " + CAcert
	#	print invoke
		os.system(invoke)


def resetCAdb():
	invoke = "cd " + CAdir + "; rm serial* index.txt newcerts/*"
	invoke += "; touch index.txt; echo '01' > serial"
	os.system(invoke)


def makeDirs():
	if not os.path.exists(CAdir):
		invoke = "mkdir " + CAdir
		invoke += "; cd " + CAdir
		invoke += "; mkdir certs crl newcerts private"
		invoke += "; echo '01' > serial"
		invoke += "; cp /dev/null index.txt"
		os.system(invoke)

	if not os.path.exists(dist):
		os.system("mkdir " + dist);


def distCA():
	os.system("rm " + dist + "/*")
	os.system("cp " + CAdir + "/" + CAcert + " " + dist)
	
	# rename certfile to hash
	invoke = "cd " + dist + " && openssl x509 -noout -hash -in " + CAcert
	invoke += " | xargs mv " + CAcert
	os.system(invoke)
	
	# rename certfile to hash.0
	thisdir = os.getcwd()
	os.chdir(dist)
	cert = glob.glob("*")[0]
#	print cert
	os.system("mv " + cert + " " + cert + ".0")
	os.chdir(thisdir)
	
	# write policy file
	policy = "#\n"
	policy += "access_id_CA\tX509\t'" + DNbase + "/CN=ARCDEMO CA'\n"
	policy += "pos_rights\tglobus\tCA:sign\n"
	policy += "cond_subjects\tglobus\t'\"" + DNbase + "/*\"'\n"
	policyfile = open(dist + "/" + cert + ".signing_policy", "w")
	policyfile.write(policy)
	policyfile.close()
	
	# create grid-mapfile
	mappings = ""
	for i in names:
		mappings += "\"" + DNbase + "/OU=arc.demo/CN=" + names[i] + "\" " + mapuser + "\n"
	mfile = open(dist + "/" + mapfile, "w")
	mfile.write(mappings)
	mfile.close()
	
	# copy myproxyreg.py to dist dir
	os.system("cp myproxyreg.py " + dist)


################################################################
###	cert functions
################################################################

def makeCert(name, cert, key, password):
	print " * Making certificate for " + name
	invoke = "cd " + CAdir
	invoke += "; openssl req -new -x509 -keyout " + key + " -out " + cert
	invoke += " -passout pass:" + password
	invoke += " -days 365 -subj '" + DNbase + "/OU=arc.demo/CN=" + name + "'"
	print invoke
	os.system(invoke)


def signCert(cert, key, password):
	print " * Converting certificate to request"
	invoke = "cd " + CAdir
	invoke += "; openssl x509 -x509toreq -in " + cert + " -signkey " + key
	invoke += " -passin pass:" + password
	invoke += " -out tmp.pem"
#	print invoke
	os.system(invoke)
	
	print " * Signing certificate with CA certificate"
	invoke = "openssl ca -policy policy_anything -batch"
	invoke += " -cert " + CAdir + "/" + CAcert
	invoke += " -keyfile " + CAdir + "/" + CAkey
	invoke += " -in " + CAdir + "/tmp.pem -out " + CAdir + "/" + cert
#	print invoke
	os.system(invoke)
	
	invoke = "cd " + CAdir + "; rm tmp.pem"
	os.system(invoke)
	
	resetCAdb()


def distCert(cert, key):
	os.system("cp " + CAdir + "/" + cert + " " + dist)
	os.system("cp " + CAdir + "/" + key + " " + dist)




if __name__ == "__main__":
	main()