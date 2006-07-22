import string, types, os, stat
from MiscUtils.Configurable import Configurable
from LunarcPage import LunarcPage
from MiscUtils.Funcs import uniqueId

import pyARC
import pyMyProxy
import Lap

class SecurePage(LunarcPage, Configurable):
	"""
	This class is an example of how to implement username and password-based
	security using WebKit.  Use a SecurePage class like this one as the
	base class for any pages that you want to require login.  Modify
	the isUserNameAndPassword method to perform validation in whatever
	way you desire, such as a back-end database lookup.  You might also
	want to modify loginUser so that it automatically brings in additional
	information about the user and stores it in session variables.

	You can turn off security by creating a config file called SecurePage.config
	in the Configs directory with the following contents:

		{
			'RequireLogin': 0
		}

	To-Do: Integrate this functionality with the upcoming UserKit.
		   Make more of the functionality configurable in the config file.

	"""

	def __init__(self):
		LunarcPage.__init__(self)
		Configurable.__init__(self)

	def awake(self, trans):
		
		# Awaken our superclass
		
		LunarcPage.awake(self, trans)

		if self.setting('RequireLogin'):
			
			# Handle four cases: logout, login attempt, already logged in, and not already logged in.
			
			session = trans.session()
			request = trans.request()
			app = trans.application()
			
			# Get login id and immediately clear it from the session
			
			loginid = session.value('loginid', None)
			print loginid
			if loginid:
				session.delValue('loginid')
			
			# Are they logging out?
			
			if request.hasField('logout'):

				print "logout"				
				
				# They are logging out.  Clear all session variables and take them to the
				# Login page with a "logged out" message.
				
				session.values().clear()
				request.setField('extra', 'You have been logged out.')
				request.setField('action', string.split(request.urlPath(), '/')[-1])
				app.forward(trans, 'LoginPage')
			
			elif request.hasField('login') and request.hasField('name') and request.hasField('password'):

				print "login"				
				
				# They are logging in.  Clear session
				
				session.values().clear()
				
				# Get request fields
				
				name = request.field('name')
				password = request.field('password')
				#f = request.field("proxy")
				#proxy = f.file.read()
				
				print "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
				print "+++", request.field('name'), request.field('password'), "+++"
				print "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
				
				# Check if they can successfully log in.  The loginid must match what was previously
				# sent.
				
				if request.field('loginid', 'nologin')==loginid and self.loginUser(name, password):
					print "succesful login"
					# Successful login.
					# Clear out the login parameters
					request.delField('name')
					request.delField('password')
				else:
					print "failed login"
					# Failed login attempt; have them try again.
					request.setField('extra', 'Login failed.  Please try again.')
					print "goto login page"
					app.forward(trans, 'LoginPage')
	
			# They aren't logging in; are they already logged in?
			
			elif not self.getLoggedInUser():

				print "goto login page"				
				# They need to log in.
				
				session.values().clear()
				
				# Send them to the login page
				
				app.forward(trans, 'LoginPage')
		else:
			# No login is required
			session = self.session()
			request = self.request()
			# Are they logging out?
			if request.hasField('logout'):
				# They are logging out.  Clear all session variables.
				session.values().clear()
			# write the page
			LunarcPage.writeHTML(self)


	def respond(self, trans):
		"""
		If the user is already logged in, then process this request normally.  Otherwise, do nothing.
		All of the login logic has already happened in awake().
		"""
		if self.getLoggedInUser():
			LunarcPage.respond(self, trans)


	def loginUser(self, name, password):
		
		# Check out proxy from MyProxy

		UID = uniqueId()

		filename = "/tmp/lap_%s" % (UID)
		
		myproxy = pyMyProxy.MyProxyStore()
		print name, password, filename
		myproxy.proxyGetDelegation(name, password, proxyfile=filename)
		
		os.chmod(filename, stat.S_IRUSR)
		
		tempFile = file(filename, "r")
		
		proxyContent = tempFile.readlines()
		
		tempFile.close()
		
		proxy = pyARC.Proxy(filename)
		DNString = proxy.getDN()
		timeLeft = proxy.getTimeleft()

		os.remove(filename)

		# Check to make sure that the proxy has not expired

		if timeLeft>0:

			# Create a user (and directory)

			user = Lap.User(DNString)
			#user.createDir()

			# Transfer proxy to user directory

			#proxyFile = file("./%s/lap_proxy" % user.getDir(), "w")
			proxyFile = file(user.getProxy(), "w")

			for line in proxyContent:
				proxyFile.write(line)

			proxyFile.close()
			proxyContent = []

			# Make sure it is not world-readable			

			#os.chown(user.getProxy(), 0, 0)
#			os.chmod(user.getProxy(), stat.S_IRUSR)
			os.chmod(user.getProxy(), 0600)
			
			# Ok, user is authenticated

			self.session().setValue('authenticated_user', DNString)

			return 1

		else:
			print "proxy has expired"
			self.session().setValue('authenticated_user', None)
			return 0

	def getLoggedInUser(self):
		# Gets the name of the logged-in user, or returns None if there is
		# no logged-in user.
		return self.session().value('authenticated_user', None)

	def defaultConfig(self):
		return {'RequireLogin': 1}

	def configFilename(self):
		return 'Configs/SecurePage.config'
