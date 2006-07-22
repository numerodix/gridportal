"""
  Author:     Martin Matusiak <numerodix@gmail.com>
  Program:    mpiBlast database update script
  Date:       Oct. 12, 2005

  This file is subject to the GNU General Public License (GPL)
  (http://www.gnu.org/copyleft/gpl.html)
"""

######################################################################
# Configuration file for mdu
######################################################################


class Config:

	### spec of databases ###
	#
	# a database must be supplied with a number of parameters
	# (some optional)
	#
	# example:
	#		"swissprot": {		# name
	#				"segments": 20,		# number of segments (required)
	#				"protein": True,	# is it a protein database? (include _only_ if true)
	#				"fasta": True			# is it in fasta format? (include _only_ if true)
	#				},
	
	default_segments = 20
	
	databases = {
	
		"est_human": {
				"segments": default_segments,
				"fasta": True
				},

		"est_mouse": {
				"segments": default_segments,
				"fasta": True
				},

		"est_others": {
				"segments": default_segments,
				"fasta": True
				},
				
		"nr": {
				"segments": default_segments,
				"protein": True
				},
				
		"nt": {
				"segments": default_segments,
				},

		"refseq_protein": {
				"segments": default_segments,
				"protein": True
				},
	
		"refseq_genomic": {
				"segments": default_segments
				},
	
		"swissprot": {
				"segments": default_segments,
				"protein": True,
				"fasta": True
				},
			
		}
	
	### selection of databases to be installed/updated ###
	#
	# note: _all_ entries, including the last entry, _must_ have a
	# trailing comma, like so:
	# "nr",
	#
	# NOTE: Every database listed here must be declared above in 'databases'
	
	update_dbs = (
#		"est_human",
#		"est_mouse",
#		"est_others",
#		"nr",
		"nt",
#		"refseq_genomic",
#		"refseq_protein",
#		"swissprot",
		)
	
	
	### declaration of temporary storage paths ###
	#
	# mdu uses two different file paths for temporary storage, first 'workdir',
	# for downloading and unpacking, then 'temp_shared' for segmentation
	# with mpiformatdb. 'temp_local' currently has no function
	#
	# each of these can be relative or absolute paths, permissions permitting
	# mdu will create them
	
	workdir = "/lwork/mdu"
	
	temp_shared = workdir + "/shared"
	temp_local = workdir + "/local"
	
	### declaration of permanent storage paths ###
	#
	# 'mpiblast_shared' is where your database files go
	# 'mpiblast_local' is your local mpiblast storage path
	#
	# 'mpiblast_shared_chmod' is the permissions to set on the database
	# (usually "777", giving read/write/exec access to all users)
	
	mpiblast_shared = "/home/blast/db"
	mpiblast_local = "/lwork/mpiblast-local"

	mpiblast_shared_chmod = "777"


	### file ftp locations ###
	#
	# end every path with a trailing slash, like so:
	# path = "ftp://www.domain.com/path/to/files/"
	
	ncbi_formatted = "ftp://ftp.ncbi.nlm.nih.gov/blast/db/"
	ncbi_fasta = "ftp://ftp.ncbi.nlm.nih.gov/blast/db/FASTA/"


# endfile
