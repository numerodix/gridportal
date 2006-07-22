from LunarcPage import LunarcPage

from HyperText.HTML import *

import Lap

class DocIntroduction(LunarcPage):

	def title(self):
		return 'Getting Started :: Introduction'

	def writeContent(self):
	
		wr = self.writeln
		w = self.write
		
		content = """
		
		<h2>Getting Started :: Introduction</h2>
		
		<p><span id="accs">If you are a new to the grid, you may want to read this to get acquainted with some concepts which will be helpful to you later on.</span>
		</p>
		
		<p><b>What's a grid?</b>
		</p>
		
		<p>You have probably heard that the grid can allow you to complete a lot of work in a relatively short period of time. But what is a grid? A grid is a <span id="accs">network of computer resources</span>. Each resource can provide a considerable amount of computational power by itself, but connected together, they form a national, international or even global network of resources, able to undertake great scientific problems.
		</p>
		
		<p>While it is possible to run jobs across the entire grid, in most cases jobs are run on just one computer resource, for instance a computer cluster. But because that resource is part of the grid, grid rules and concepts apply.
		</p>
		
		<p><span id="accs">GRIDportal</span> is a way to access the grid (but not the only way).
		</p>
		
		<p><b>The grid and you</b>
		</p>
		
		<p>As a newbie to the grid, you probably just want to know how you can get work done. The first thing to understand is how the grid handles users and user accounts. Because the grid is such a wide scale network, it would be problematic to keep track of users for every computer resource, across national borders. Instead, the grid uses the concept of a <span id="accs">user certificate</span>. The user certificate (or simply 'certificate'), identifies a user uniquely across the entire grid, it is your <span id="accs">passport</span> on the grid.
		</p>
		
		<p>Needless to say, to access the grid you will need a certificate. Certificates are created by users themselves, but they have to be <span id="accs">signed</span> by a <span id="accs">Certificate Authority (CA)</span> before they are valid. (Imagine you make your passport at home but you have to get it stamped at the police station before it is valid.) The purpose is that someone whom we all trust, the <span id="accs">authority</span> (be it the CA or the police), agrees that you are who you say you are.
		</p>
		
		<p>Once you have a <span id="accs">signed certitificate</span>, you can access the grid.
		</p>
		
		<p><b>What's in a certificate?</b>
		</p>
		
		<p>A certificate consists of two files, a <span id="accs">certificate</span> file (<span id="accs">usercert.pem</span>), and a <span id="accs">private key</span> file (<span id="accs">userkey.pem</span>). Both these files are created at the same time and required to access the grid. The difference between them is that the certificate is <span id="accs">public</span>, while the private key is <span id="accs">private</span> (meaning you should not show it to anyone).
		</p>
		
		<p>An <span id="accs">unsigned certiticate</span> is also known as a <span id="accs">certificate request</span> (in the sense that you are <span id="accs">requesting</span> approval). It is stored in a file called <span id="accs">usercert_request.pem</span>. The only purpose of this file is to request a signature. Once you have received a signature (which means you now have a file <span id="accs">usercert.pem</span>), you no longer need the certificate request file.
		</p>
		
		<p>A <span id="accs">signed certiticate</span> is valid for a set period of time, normally <span id="accs">one year</span>. After that period, you can no longer use it, but you can request <span id="accs">renewal</span>, which will extend its lifetime for another year. There is no practical difference between requesting renewal on an expired certitificate and that of creating a new certiticate once the old one expires.
		</p>
		
		<p><b>How can I get a certificate?</b>
		</p>
		
		<p>You can <span id="accs"><a href="DocCreateCert">create a certificate</a></span> with the <span id="accs"><a href="DocUsingToolkit">GridPortalToolkit</a></span>.
		</p>
		
		<p><b>Is that all?</b>
		</p>
		
		<p>Not quite. Before you can use <span id="accs">GRIDportal</span>, you have to <span id="accs"><a href="DocRegister">register an account</a></span> using <span id="accs"><a href="DocUsingToolkit">GridPortalToolkit</a></span>. To do that, you will need a signed certificate.
		</p>

		<p>&nbsp;</p>
		<p><a href="DocToolkit"><< Return to Getting Started Main Menu</a></p>

		<p>&nbsp;</p>
		"""
		
		w(content)
