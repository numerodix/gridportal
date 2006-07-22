############## Blast configuration ###################

# full path to blastall executable binary
env_blastall = "blastall"

blast_template = """#!/bin/sh
""" + env_blastall + """ -p %(blastType)s %(params)s -i %(jobIdentifier)s -o blast_out.QUERY%(html)s
"""

blast_dbpath = "/home/blast/db/"

desc_base_url = "https://norgrid.ntnu.no/gridportal/FormDescPage"

blast_params = {

	# this is the http form context. All parameters to be visible on
	# the html input form must be declared here with element type and
	# label

	"params": {
				"seq": {
					"type": "textarea",
					"label": "* Enter query sequence(s)",
					"desc": desc_base_url+"#seq"
					},
					
				"seqInputFile": {
					"type": "file",
					"label": "Sequences input file",
					"desc": desc_base_url+"#seqInputFile"
					},
	
				"e_value": {
					"type": "select",
					"label": "Expectation value (E)",
					"desc": desc_base_url+"#e_value"
					},
					
				"html_output": {
					"type": "select",
					"label": "HTML format on output",
					"desc": desc_base_url+"#html_output"
					},
					
				"filter": {
					"type": "select",
					"label": "Filter query sequence",
					"desc": desc_base_url+"#filter"
					},
					
				"scores": {
					"type": "select",
					"label": "Number of scores/descriptions reported",
					"desc": desc_base_url+"#scores"
					},
				
				"alignments": {
					"type": "select",
					"label": "Number of alignments reported",
					"desc": desc_base_url+"#alignments"
					},

				"location": {
					"type": "text",
					"label": "Location on query sequence [10,400]",
					"desc": desc_base_url+"#location"
					},
															
				"adv_params": {
					"type": "text",
					"label": "Advanced parameters",
					"desc": desc_base_url+"#adv_params"
					},
					
				"wordsize": {
					"type": "select",
					"label": "Word Size",
					"desc": desc_base_url+"#wordsize"
					},				
					
				"matrix": {
					"type": "select",
					"label": "Matrix",
					"desc": desc_base_url+"#matrix"
					},
					
				"query_database": {
					"type": "select",
					"label": "Database",
					"desc": desc_base_url+"#query_database"
					},
					
				"gi_sequence": {
					"type": "textarea",
					"label": "Restrict search of database to list of GI's",
					"desc": desc_base_url+"#gi_sequence"
					},
					
#				"giInputFile": {
#					"type": "file",
#					"label": "gi input file"
#					}

		}, # /params

	"btypes": (
							"common", 
							"blastp",
							"blastn",
							"blastx",
							"tblastn",
							"tblastx",
							"megablast"
							),

	# order in which parameters are displayed on html form _AND_
	# order in which they are written to execution script						

	"order": (
							"seq",
							"seqInputFile",
							"location",
							"query_database",
							"filter",
							"e_value",
							"wordsize",
							"matrix",
							"html_output",
							"alignments",
							"scores",
							"gi_sequence",
#							"giInputFile",
							"adv_params"
							),


	# this is the shell execution context. All parameters declared above
	# must be assigned list of possible values _and_ default value (indicated 
	# by the "1" in the final column
	
	# "common" declares parameters common for all blast programs,
	# program specific parameters are declared below this section
		
	"common": {
				"seq": [
					("", "", 1)
					],
					
				"seqInputFile": [
					("", "-i", 1)
					],
	
				"e_value": [
					("1000.0", "-e 1000.0", 0),
					("100.0", "-e 100.0", 0),
					("10.0", "-e 10.0", 1),
					("1.0", "-e 1.0", 0),
					("0.1", "-e 0.1", 0),
					("1.0E-03", "-e 1.0E-03", 0),
					("1.0E-05", "-e 1.0E-05", 0),
					("1.0E-08", "-e 1.0E-08", 0),
					("1.0E-16", "-e 1.0E-16", 0)
					],
	
				"html_output": [
					("Yes", "-T T", 0),
					("No", "-T F", 1),
					],
					
				"filter":	[
					("Yes", "-F T", 1),
					("No",	"-F F", 0)
					],
					
				"scores": [
					("0", "-v 0", 0),
					("10", "-v 10", 0),
					("25", "-v 25", 0),
					("50", "-v 50", 0),
					("100", "-v 100", 0),
					("200", "-v 200", 0),
					("500", "-v 500",	1),
					("1000", "-v 1000", 0),
					("5000", "-v 5000", 0)
					],
					
				"alignments": [
					("0", "-b 0", 0),
					("10", "-b 10", 0),
					("25", "-b 25", 0),
					("50", "-b 50", 0),
					("100", "-b 100", 0),
					("250", "-b 250", 1),
					("500", "-b 500", 0),
					("1000", "-b 1000", 0),
					("5000", "-b 5000", 0)
					],
					
				"location": [
					("0,100", "-L", 1)
					],
					
				"adv_params": [
					("", "", 1)
					],
					
				"wordsize": [
					("2", "-W 2", 0),
					("3", "-W 3", 1)
					],
					
				"gi_sequence": [
					("", "-l", 1)
					],
					
#				"giInputFile": [
#					("", "-l", 1)
#					]
				
		}, # /common
		
	"blastp": {
				"matrix": [
					("BLOSUM45", "-M BLOSUM45", 0),
					("BLOSUM62", "-M BLOSUM62", 1),
					("BLOSUM80", "-M BLOSUM80", 0),
					("PAM30", "-M PAM30", 0),
					("PAM70", "-M PAM70", 0)
					],
					
				"query_database": [
					("refseq_protein", "-d "+blast_dbpath+"refseq_protein", 1),
					]
	}, # /blastp
	
	"blastn": {
				"wordsize": [
					("7", "-W 7", 0),
					("11", "-W 11", 1),
					("15", "-W 15", 0)
					],
					
				"query_database": [
					("refseq_genomic", "-d "+blast_dbpath+"refseq_genomic", 1),
					]
	}, # /blastn
	
	"blastx": {
				"query_database": [
					("refseq_protein", "-d "+blast_dbpath+"refseq_protein", 1),
					]
	}, # /blastx	
	
	"tblastn": {
					"query_database": [
					("refseq_genomic", "-d "+blast_dbpath+"refseq_genomic", 1),
					]
	}, # /tblastn		
	
	"tblastx": {
					"query_database": [
					("refseq_genomic", "-d "+blast_dbpath+"refseq_genomic", 1),
					]
	}, # /tblastx
	
	"megablast": {
				"wordsize": [
					("11", "-W 11", 0),
					("12", "-W 12", 0),
					("16", "-W 16", 0),
					("20", "-W 20", 0),
					("24", "-W 24", 0),
					("28", "-W 28", 1),
					("32", "-W 32", 0),
					("48", "-W 48", 0),
					("64", "-W 64", 0)
					],
					
				"query_database": [
					("refseq_genomic", "-d "+blast_dbpath+"refseq_genomic", 1)
					]
	} # /megablast	
}

#print blast_params
#print len(blast_params["common"]["e_value"])
#print (blast_params["common"]["e_value"][0][0])
#print blast_params["common"]["e_value"][0][0]

#attribs = []
#bp = blast_params
#for btype in bp["btypes"]:
#	for param in bp[btype]:
#		for j in range(0, len(bp[btype][param])):
#			option = bp[btype][param][j]
			#print(param, option[0])
#			if (option[2] == 1): print(option[0])

bp = blast_params			
atts = []
for btype in ("common", "blastp"):
	for param in bp[btype]:
		atts.append(bp[btype][param])
		
#print atts
		

for i in range(0, len(bp["order"])):
	oparam = bp["order"][i]
	#print(oparam)
	if oparam in atts:
		pass
		#print(oparam)

