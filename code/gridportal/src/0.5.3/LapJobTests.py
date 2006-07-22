from LapJob import *

import unittest


class XRSLFileTest(unittest.TestCase):
	def testFile(self):
		task = Task()
		task.printAttributes()
		
		xrsl = XRSLFile(task)
		xrsl.write()
		
		#assert xrsl["gmlog"] == "gmlog", "wrong string"
		
		

def main(self):
	xrsltest = XRSLFileTest("testFile")


if __name__ == "__main__":
		unittest.main()