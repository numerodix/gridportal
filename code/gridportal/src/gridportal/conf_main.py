#	gridportal configuration file
#
# System specific settings go in here. Note that paths are given
# with trailing slash, e.g.:
# path = "/path/to/file/"


############## Grid administration software ###################

# full path to NorduGrid executables
env_nordugrid = "/opt/nordugrid/bin/"

# name of cluster to be used, must be in the form
# "-c cluster.domain.com" (for use with nordugrid)
env_cluster = "-c norgrid.ntnu.no"

# timeout in minutes for job submission process, heavy restrictions will
# conflict with tranfers of big files
env_arctimeout = 5

# full path to Globus executables
env_globus = "/opt/globus/bin/"



############## Unix environment ###################

# homedir path for the user who runs webkit. without setting this
# explicitly, gridportal tends to freak out
env_homepath = "/home/gridportal"



############## MyProxy host ###################

# fully qualified domain for the myproxy host
env_myproxy_host = "norgrid.ntnu.no"

# name of the proxy file
env_proxy_file = "lap_proxy"

# lifetime of the proxy in hours
env_proxy_lifetime = 12



############## Applications ###################

# full path to matlab executable binary
env_matlab = "/opt/matlab/bin/matlab"



############## Parameters for common form fields ###################

desc_base_url = "http://norgrid.ntnu.no/gridportal/FormDescPage"

grid_form = {

	# this is the http form context. All parameters to be visible on
	# the html input form must be declared here with element type and
	# label

		"cpuTime": {
			"type": "text",
			"label": "CPU time (m)",
			"desc": desc_base_url+"#cpuTime"
			},
			
		"jobName": {
			"type": "text",
			"label": "Job name",
			"desc": desc_base_url+"#jobName"
			},

		"email": {
			"type": "text",
			"label": "Email notification",
			"desc": desc_base_url+"#email"
			}
}