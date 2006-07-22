from LunarcPage import LunarcPage

from HyperText.HTML import *

import Lap

class DocVirtualOrganisation(LunarcPage):

	def title(self):
		return 'Getting Started :: Virtual Organisations'

	def writeContent(self):
	
		wr = self.writeln
		w = self.write
		
		content = """
		
		<h2>Getting Started :: Virtual Organisations</h2>
		
		<p>Alright, so now you can log into GRIDportal and you have access to the grid, now what? Although you have access to the grid as a whole, you have not been alloted access to any specific computer resource. If you submit a job to the grid, the grid will try to find a computer resource where the job can be run, but it will ultimately be rejected by every resource.
		</p>
		
		<p>On the grid, the access to resources is governed by <span id="accs">Virtual Organisations (VO)</span>. A Virtual Organisation is a group of users who are tied together under a common banner (often this has to do with funding). In order to start submitting jobs to the grid, you have to apply to a Virtual Organisation, which in itself will determine which resources you are authorized to use.
		</p>
		
		<table>
		<tr>
		<td valign=top>
		
		Consult the list of VOs at the bottom of <span id="accs"><a href="http://www.nordugrid.org/NorduGridVO/index.php#volist">this page</a></span>. By clicking on <span id="accb">Details</span> next to a VO, you will find the person responsible for the VO (with an email address listed). You should then contact this person and ask to be included in the Virtual Organisation.
		
		<p><strong>NOTE:</strong> The contact person of the VO will need to know your <span id="accs">Distinguished Name (DN)</span> You can look this up by using <span id="accs"><a href="DocUsingToolkit">GridPortalToolkit</a></span>. On the <span id="acc">Summary page</span>, click <span id="accb">Detect</span> and then <span id="accb">View certificate</span>. Now, simply copy the segment labeled  <span id="accs">Subject</span> into your email message.
		</p>
		
		<p>Now you should be able to work on the grid. If you run into any problems, consult the <span id="accs"><a href="DocUserGuide">User's Guide</a></span>. Good luck!
		</p>

		<p>&nbsp;</p>
		<p><a href="DocToolkit"><< Return to Getting Started Main Menu</a></p>		

		</td>
		<td>&nbsp;&nbsp;</td>
		<td valign=top>
		
		<img src="images/start/vo_find01.png"><br><br>
		<img src="images/start/vo_find02.png"><br><br>
		<img src="images/start/vo_dn01.png"><br><br>
		<img src="images/start/vo_dn02.png"><br>

		</td>
		
		</tr>
		</table>

		<p>&nbsp;</p>
		"""
		
		w(content)
