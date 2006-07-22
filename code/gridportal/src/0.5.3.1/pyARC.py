import os, sys, string
from conf_main import *
import pyARC, LapUtils

class DN:
	def __init__(self, DN):
		self.DNString = DN
		self.process()

	def process(self):
		parts = string.split(self.DNString, "/")
		
		cleanedParts = []
			
		for part in parts:
			subPart = string.split(part,"=")
			if len(subPart)>1:
				cleanedParts.append(subPart[1])

		self.organisation1 = cleanedParts[0]
		self.organisation2 = cleanedParts[1]
		self.organisationalUnit = cleanedParts[2]
		self.name = cleanedParts[3]

	def getOrganisation1(self):
		return self.organisation1

	def getOrganisation2(self):
		return self.organisation2

	def getOrganisationalUnit(self):
		return self.organisationalUnit

	def getName(self):
		return self.name

	def setDNString(self, DNString):
		self.DNString = DNString
		self.process()

class Proxy:
	def __init__(self, filename):
		self.proxyFilename = filename
		self.DN = ""
		self.timeleft = 0

		self.query()

	def setFilename(self, filename):
		self.proxyFilename = filename
		self.query()

	def getFilename(self):
		return self.proxyFilename

	def query(self):
		os.chdir(env_homepath)
		os.environ["X509_USER_PROXY"] = self.proxyFilename
		result = LapUtils.simpleExec(env_globus + "grid-proxy-info -subject")
		
		print self.proxyFilename, ", ", result

		if len(result)>0:		
			self.DN = result[0]
			result = LapUtils.simpleExec(env_globus + "grid-proxy-info -timeleft")
			self.timeLeft = int(result[0])
		else:
			result = ""
			self.timeLeft=-1
			
		os.environ["X509_USER_PROXY"] = ""


	def getDN(self):
		
		return self.DN

	def getTimeleft(self):
		
		return self.timeLeft

class Ui:
	def __init__(self, user):
		self.user = user
		self.proxy = pyARC.Proxy(self.user.getProxy())
		self.preferredCluster = env_cluster
		self.debugLevel = 0
		self.submitTimeout = 4	
		
	def setPreferredCluster(self, cluster):
		self.preferredCluster = cluster
		
	def getPreferredCluster(self):
		return self.preferredCluster
		
	def setDebugLevel(self, debugLevel):
		self.debugLevel = debugLevel
		
	def getDebugLevel(self):
		return self.debugLevel
		
	def setSubmitTimeout(self, timeout):
		self.submitTimeout = timeout
		
	def getSubmitTimeout(self):
		return self.submitTimeout
		
	def sync(self):
		os.environ["X509_USER_PROXY"] = self.proxy.getFilename()
		oldHOME = os.environ["HOME"]
		os.environ["HOME"] = self.user.getDir()
		result = LapUtils.simpleExec(env_nordugrid + "ngsync -f -d %d %s" % (self.debugLevel, env_cluster))
		os.environ["HOME"] = oldHOME
		os.environ["X509_USER_PROXY"] = ""
		
	def get(self, jobID, downloadDir = ""):
		os.environ["X509_USER_PROXY"] = self.proxy.getFilename()
		oldHOME = os.environ["HOME"]
		os.environ["HOME"] = self.user.getDir()
		if downloadDir=="":
			result = LapUtils.simpleExec(env_nordugrid + "ngget %s" % (jobID))
		else:
			result = LapUtils.simpleExec(env_nordugrid + "ngget -dir %s %s" % (downloadDir, jobID))
		os.environ["HOME"] = oldHOME
		os.environ["X509_USER_PROXY"] = ""
		
		return result
	
	def clean(self, jobID):
		os.environ["X509_USER_PROXY"] = self.proxy.getFilename()
		oldHOME = os.environ["HOME"]
		os.environ["HOME"] = self.user.getDir()
		result = LapUtils.simpleExec(env_nordugrid + "ngclean %s" % (jobID))		
		os.environ["HOME"] = oldHOME
		os.environ["X509_USER_PROXY"] = ""
		
		return result

	def kill(self, jobID):
		os.environ["X509_USER_PROXY"] = self.proxy.getFilename()
		oldHOME = os.environ["HOME"]
		os.environ["HOME"] = self.user.getDir()
		result = LapUtils.simpleExec(env_nordugrid + "ngkill %s" % (jobID))		
		os.environ["HOME"] = oldHOME
		os.environ["X509_USER_PROXY"] = ""
		
		return result

	def jobStatus(self):
			
		os.environ["X509_USER_PROXY"] = self.proxy.getFilename()
		oldHOME = os.environ["HOME"]
		os.environ["HOME"] = self.user.getDir()
		result = LapUtils.simpleExec(env_nordugrid + "ngstat -a %s" % env_cluster)		
		os.environ["HOME"] = oldHOME
		os.environ["X509_USER_PROXY"] = ""

		jobList = {}
		job = None

		try:
			for line in result:
				print line
				splitResult = string.split(line,":")
				if len(splitResult)>0:
					property = string.strip(splitResult[0])
	
					if property=="Job gsiftp":
						if job!=None:
							jobList[jobID] = job
							
						job = {}
						splitResult2 = string.strip(line," ")
						jobID = line[4:]
	
					if property=="Jobname":
						job["name"] = string.strip(line)[9:]
	
					if property=="Status":
						job["status"] = string.strip(line)[8:]
	
					if property=="Error":
						job["error"] = string.strip(line)[7:]
	
				if job!=None:
					jobList[jobID] = job
					
		except:
			return None
									
		return jobList
	
	def submit(self, xrslFilename):
		
		if not os.path.isfile(xrslFilename):
			return False
		
		[jobDir, filename] = os.path.split(xrslFilename)
		
		oldDir = os.getcwd()
		os.chdir(jobDir)
		os.environ["X509_USER_PROXY"] = self.proxy.getFilename()
		oldHOME = os.environ["HOME"]
		os.environ["HOME"] = self.user.getDir()
 		result = LapUtils.simpleExec(env_nordugrid + "ngsub -t %s -d %s -f %s %s" % (env_arctimeout, self.debugLevel, xrslFilename, env_cluster))
		os.environ["HOME"] = oldHOME
		os.environ["X509_USER_PROXY"] = ""
		os.chdir(oldDir)
	
		return result
				
