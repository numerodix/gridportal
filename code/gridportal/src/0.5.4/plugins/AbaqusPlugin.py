#!/usr/bin/python

import os
import sys
import string

import Grid

abaqusInputfileTemplate = """*HEADING
  BARREL VAULT (100 X 204 MESH)
*NODE ,  SYSTEM=C,NSET=CORNER
1 , 300.
201 , 300. ,,300.
102001 , 300. , 40.
102201 , 300. , 40. , 300.
*NGEN,LINE=C,NSET=WALL
1 ,102001 , 250
*NGEN,LINE=C, NSET=SYMM
201 ,102201 , 250 ,,,, 300.
*NGEN,NSET=APEX
1,201
*NFILL
WALL,SYMM,200,1
*ELEMENT,TYPE=S8R5   ,ELSET=ROOF1
1,  1,3,503,501,  2,253,502,251
*ELGEN,ELSET=ROOF
1,100,2,1,204,500,400
*SHELL SECTION,MATERIAL=R1,ELSET=ROOF
3.0, 3
*MATERIAL,NAME=R1
*ELASTIC
3.E6,
*BOUNDARY
APEX,2
APEX,4
APEX,6
SYMM,3,5
WALL,1,2
WALL,6
*PRE PRINT,MODEL=NO,HISTORY=NO
*STEP,PERTURBATION
 SELF-WEIGHT
*STATIC
*PRINT,SOLVE=YES
*DLOAD
ROOF, BX, -0.20833
*EL PRINT ,FREQUENCY=0
*NODE FILE,NSET=APEX
U,
*NSET, NSET=NODBOUT
1,
*OUTPUT,FIELD, FREQUENCY=999
*NODE OUTPUT,NSET=NODBOUT
U,
*END STEP
"""

class Task(Grid.Task):
	def __init__(self):
		Grid.Task.__init__(self)
		self.setExecutableShell("abaqus.sh")
		self.setRemoteExecutable(True)
		self.jobid = ""
		# self.setRuntimeEnvironment("ABAQUS-6.4")

		# define plugin behavior

		self.setNeedExecutable(False)
		self.setNeedArguments(False)
		self.setNeedInputFiles(True)
		self.setNeedExtraInputFiles(False)

		self.setInputFileExt("inp")
		self.setInputFileSelectionType(self.IFS_SINGLE_INPUT_FILE)
		self.setOutputFileSelectionType(self.OFS_AUTO)

	def getDescription(self):
		return "Abaqus 6.4 Task"

	def onSelectInputFiles(self, inputFiles):
		if len(inputFiles)>=1:
			modifiedFiles = []
			modifiedFiles.append(inputFiles[0])
			temp = string.split(inputFiles[0], ".")
			self.setJobIdentifier(temp[0])
			return modifiedFiles
		else:
			return inputFiles

	def createTemplate(self, path):
		abaqusInputFile = open(path + "/t1-std.inp", "w")
		abaqusInputFile.write(abaqusInputfileTemplate)
		abaqusInputFile.close()

	def update(self):
		self.setArguments("/opt/abaqus/6.4-1/exec/abq641 job=%s interactive" % self.jobid)
		self["outputFiles"].clear()
		self["outputFiles"][self.jobid+".dat"] = ""
		self["outputFiles"][self.jobid+".fil"] = ""
		self["outputFiles"][self.jobid+".msg"] = ""
		self["outputFiles"][self.jobid+".odb"] = ""
		self["outputFiles"][self.jobid+".prt"] = ""
		self["outputFiles"][self.jobid+".sta"] = ""
	
	def setJobIdentifier(self, jobid):
		self.jobid = jobid
		self.setJobName(self.jobid)
		self.update()

def main():
	abaqusTask = Task()
	abaqusTask.setJobIdentifier("t1-std")
	taskFile = Grid.XRSLFile(abaqusTask)
	taskFile.setFilename("test.xrsl")
	taskFile.write()

if __name__ == "__main__":
	main()


