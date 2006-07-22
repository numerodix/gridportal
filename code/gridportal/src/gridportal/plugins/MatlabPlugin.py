#!/usr/bin/python

import os
import string

import Grid

matlabInputfileTemplate = """bench
"""

class Task(Grid.Task):
	def __init__(self):
		Grid.Task.__init__(self)
		self.setExecutableShell("matlab.sh")
		self.setRemoteExecutable(True)
		self.jobid = ""
		self.setRuntimeEnvironment("MATLAB-6.5")

		self.setNeedExecutable(False)
		self.setNeedArguments(False)
		self.setNeedMainInputFile(True)
		self.setNeedInputFiles(True)
		self.setNeedExtraInputFiles(True)

		self.setInputFileExt("m")
		self.setInputFileSelectionType(IFS_INPUT_FILES)
		self.setOutputFileSelectionType(OFS_MANUAL)

	def getDescription(self):
		return "MATLAB 6.5 Task"

	def createTemplate(self, path):
		matlabInputFile = open(path + "/matlab_example.m", "w")
		matlabInputFile.write(matlabInputfileTemplate)
		matlabInputFile.close()

	def onSetMainInputFile(self, mainFile):
		self.setArguments("matlab -nodisplay -nosplash -nojvm < %s" % mainFile)

def main():
	abaqusTask = Task()
	abaqusTask.setJobIdentifier("t1-std")

    taskFile = Grid.XRSLFile(abaqusTask)
    taskFile.setFilename("test.xrsl")
    taskFile.write()

if __name__ == "__main__":
	main()


