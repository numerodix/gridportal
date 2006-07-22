from LunarcPage import LunarcPage

from HyperText.HTML import *

import Lap

class Empty(LunarcPage):

	def title(self):
		return 'Welcome!'

	def writeContent(self):
	
		wr = self.writeln
		w = self.write
		
		welcomeMessage = """<strong>Welcome to GRIDportal, the NTNU Application Portal!</strong>
		<p>This portal provides easy access to commonly found applications at NTNU clusters.
		Please use the top menu to access the functionality of this portal.
		<p>Please note that all functions require a valid grid-proxy."""
		
		table = TABLE()
		tableBody = TBODY()
		table.append(tableBody)
		tableBody.append(TR(TD(welcomeMessage)))
		tableBody.append(TR(TD(BR())))
		if Lap.textVersion!="":
			tableBody.append(TR(TD("GRIDportal Version %d.%d.%d (%s)" % (Lap.majorVersion, Lap.minorVersion, Lap.releaseVersion, Lap.textVersion))))
		else:
			tableBody.append(TR(TD("GRIDportal Version %d.%d.%d " % (Lap.majorVersion, Lap.minorVersion, Lap.releaseVersion))))
		tableBody.append(TR(TD("%s" % Lap.fork)))
		tableBody.append(TR(TD("%s" % Lap.copyright)))
		tableBody.append(TR(TD("Distributed under the %s" % Lap.license)))
		tableBody.append(TR(TD("Written by: %s" % Lap.author)))
		tableBody.append(TR(TD(BR())))
		tableBody.append(TR(TD(STRONG("Credits:"))))
		#tableBody.append(TR(TD(BR())))
		tableBody.append(TR(TD(Lap.credits1)))
		tableBody.append(TR(TD(Lap.credits2)))
		tableBody.append(TR(TD(Lap.credits3)))
		tableBody.append(TR(TD(Lap.credits4)))
		tableBody.append(TR(TD(Lap.credits5)))
		                                                                
	
				    
			
