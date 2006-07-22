"""
  Author:     Martin Matusiak <numerodix@gmail.com>
  Program:    mpiBlast database update script
  Date:       Oct. 12, 2005

  This file is subject to the GNU General Public License (GPL)
  (http://www.gnu.org/copyleft/gpl.html)
"""

######################################################################
# System configuration file for mdu
######################################################################


class ConfigSys:

	### paths for commands used by mdu ###
	#
	# if the path is set for the command, just the command name will
	# suffice, e.g.:
	# perl = "perl"
	#
	# if the path is not set, you may need to declare it
	# usually, just calling '`which perl`' (observe the back quotes) in
	# a shell is sufficient, for instance:
	# perl = `which perl`
	#
	# otherwise set the full path to the command, like so:
	# perl = "/usr/bin/perl"
	
	bash = "bash"
	cat = "cat"
	chmod = "chmod"
	cp = "cp"
	curl = "curl"
	gunzip = "gunzip"
	mv = "mv"
	rm = "rm"
	sed = "sed"
	tar = "tar"
	wget = "wget"
	
	fastacmd = "/opt/blast/bin/fastacmd"
	mpiformatdb = "mpiformatdb"
	
	# in some environments, the correct mpi module must be loaded before
	# mpiblast is run. This is also the case for mpiformatdb. include any
	# required shell commands here, these will run just before mpiformatdb 
	# is run
	
	loadmpi = "source /opt/modules/3.1.6/init/bash"
	loadmpi += "; module load mpi"
	loadmpi += "; module unload intelcomp"
	loadmpi += "; module load Intelcomp.8.0"
	loadmpi += "; module load mpiblast/1.4"

# endfile
