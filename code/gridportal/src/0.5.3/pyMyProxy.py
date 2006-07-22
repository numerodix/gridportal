from pyGlobus.myProxy import MyProxy
from pyGlobus.security import grid_proxy_destroy, grid_proxy_init, grid_proxy_info

from conf_main import *

class MyProxyStore:

	def proxyGetDelegation(self, username, password, host=env_myproxy_host, 
			proxyfile=env_proxy_file, duration=env_proxy_lifetime):
		_proxy = MyProxy()
		_proxy.set_username(username)
		_proxy.set_passphrase(password)
		print username, password, host, proxyfile, duration
		_proxy.get_delegation(host, outFile=proxyfile, lifetime=duration, 
			interactive=False)
