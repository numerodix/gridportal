<html>
<head>
	<title>GridPortalToolkit</title>
	
<link rel="stylesheet" href="/styles.css" type="text/css">
<link rel="stylesheet" href="_scripts/styles.css" type="text/css">
</head>

<body>

<?      include ("../menu.php");        ?>

	<h1>GridPortalToolkit</h1>

	<div class="img_r">
		<table>
			<tr>
				<td>
					<a href="_images/gptoolkit_sshot_winxp.png">
						<img src="_images/gptoolkit_sshot_winxp_t.png" width="100"
							alt="GridPortalToolkit running on Windows XP" border="0">
						<br>Windows XP
					</a>
				</td>
				<td>
					<a href="_images/gptoolkit_sshot_linux.png">
						<img src="_images/gptoolkit_sshot_linux_t.png" width="100"
							alt="GridPortalToolkit running on Linux" border="0">
						<br>Linux
					</a>
				</td>
			</tr>
			<tr>
				<td>
					<a href="_images/gptoolkit_sshot_solaris.png">
						<img src="_images/gptoolkit_sshot_solaris_t.png" width="100"
							alt="GridPortalToolkit running on Solaris" border="0">
						<br>Solaris
					</a>
				</td>
				<td>
					<a href="_images/gptoolkit_sshot_macosx.png">
						<img src="_images/gptoolkit_sshot_macosx_t.png" width="100"
							alt="GridPortalToolkit running on MacOSX" border="0">
						<br>Mac OS X
					</a>
				</td>
			</tr>
		</table>
	</div>
	<br/>
	GridPortalToolkit is a client application in support of <a href="/gridportal">GRIDportal</a>. The toolkit aids a user through the process of obtaining access to GRIDportal by supplying the following functions:
	<ul>
		<li>user certificate generation
		<li>certificate signature request
		<li>MyProxy registration
	</ul>
	<br/>
	The client is implemented in java and should therefore run on just about any platform. GridPortalToolkit is based heavily on the <a href="http://www.cogkit.org/">cog-jglobus</a> library.
	<br/><br/>
	GridPortalToolkit is distributed under the <a href="http://www.gnu.org/copyleft/gpl.html">GPL licence</a>.
	<br/><br/>
	Martin Matusiak - GridPortalToolkit project manager
	

	<h2>Download</h2>

	<h3>Dependencies</h3>
	<ul>
		<li><a href="http://java.sun.com/">Java JRE 1.4.2+</a> (to run)
		<li><a href="http://java.sun.com/">Java JDK 1.4.2+</a> (to build from source)
		<li><a href="http://ant.apache.org/">Apache Ant</a> (to build from source)
	</ul>
	
	<h3>Download</h3>
	Download a binary release:
	<ul>
		<li><a href="https://sourceforge.net/project/showfiles.php?group_id=171912&package_id=198107">download from sourceforge.net</a>
	</ul>
	<br/>
	To download the latest source version of GridPortalToolkit, use <a href="http://subversion.tigris.org/">subversion</a> to check out the files from the repository.
	<br/><br/>
	<div class="sh">$ svn checkout https://svn.sourceforge.net/svnroot/grid-portal/code/gptoolkit/src/gptoolkit</div>
	
	<h3>Source code repository</h3>
	To browse the source code repository, use one of the following methods:
	<ul>
		<li><a href="http://svn.sourceforge.net/viewcvs.cgi/grid-portal/code/gptoolkit">viewcvs :: repository browsing</a></li>
	</ul>



	<h2>Documentation</h2>
	
	<h3>API documentation</h3>
	<ul>
		<li>GRIDportal api documentation :: [<a href="docs/api">html</a>]</li>
	</ul>
	

	<h2>Development</h2>

	<h3>ChangeLog</h3>
	<pre><? @include("docs/ChangeLog"); ?></pre>


	<div class="copy">
		&copy; 2005 <a href="mailto:numerodix(|)gmail.com">Martin Matusiak</a>. All content on this website is licenced under a <a href="http://creativecommons.org/licenses/by-nc-sa/2.5/">Creative Commons License</a>.
	</div>

</body>
</html>
