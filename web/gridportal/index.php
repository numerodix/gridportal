<html>
<head>
	<title>GRIDportal project</title>
	
<link rel="stylesheet" href="/styles.css" type="text/css">	
<link rel="stylesheet" href="_scripts/styles.css" type="text/css">	
</head>

<body>

<? 	include ("../menu.php");	?>

	<h1>GRIDportal project</h1>

	<div class="img_r">
		<a href="_images/gridportal_sshot.png">
			<img src="_images/gridportal_sshot_t.png" width="300"
				alt="gridportal screenshot" border="0">
		</a>
	</div>
	GRIDportal is a web based application portal which acts as a frontend to GRID computing. Its aim is to make common GRID applications like Abaqus, Matlab or BLAST more accessible to the user. Use of GRIDportal does not require any knowledge of Unix nor GRID computing whatsoever, all the user needs to know is how to use the given application, so the step from desktop computing to GRID computing should thus become a much smaller one than it otherwise would be.
	<br/><br/>
	GRIDportal, written in Python and based on Webware for Python, is meant to be modular, which allows quick development of plugins for new applications.
	<br/><br/>
	GRIDportal is built on top of NorduGrid/ARC middleware and depends on the middleware to function. This relationship between GRIDportal and NorduGrid/ARC also allows a user to access the entire NorduGrid network through GRIDportal (provided the user has the necessary credentials), thus making GRIDportal a gateway to a whole range of different GRID sites.
	<br/><br/>
	GRIDportal is developed by the <a href="http://hpc.ntnu.no/">HPC Project</a> at the <a href="http://www.ntnu.no/">Norwegian University of Science and Technology</a>. It was forked from Jonas Lindemann's LUNARC Application Portal and is distributed under the <a href="http://www.gnu.org/copyleft/gpl.html">GPL licence</a>.
	<br/><br/>
	Martin Matusiak - GRIDportal project manager
	

	<h2>Deployment</h2>
	
	For a live example of GRIDportal:

	<ul>
		<li><a href="http://norgrid.ntnu.no/gridportal/">gridportal.norgrid</a>
	</ul>


	<h2>Download</h2>

	<h3>Dependencies</h3>
	<ul>
		<li>Linux
		<li><a href="http://www.webwareforpython.org/">Webware for Python</a>
		<li><a href="http://www.apache.org/">Apache http server</a>
		<li><a href="http://www.nordugrid.org/">NorduGrid/ARC middleware</a>
		<li><a href="http://www-itg.lbl.gov/gtg/projects/pyGlobus/">pyGlobus</a>
	</ul>
	
	<h3>Download</h3>
	Download a release:
	<ul>
		<li><a href="https://sourceforge.net/project/showfiles.php?group_id=171912&package_id=198108">download from sourceforge.net</a>
	</ul>
	<br/>
	To download the latest version of GRIDportal, use <a href="http://subversion.tigris.org/">subversion</a> to check out the files from the repository.
	<br/><br/>
	<div class="sh">$ svn checkout https://svn.sourceforge.net/svnroot/grid-portal/code/gridportal/src/gridportal</div>
	
	<h3>Source code repository</h3>
	To browse the source code repository, use one of the following methods:
	<ul>
		<li><a href="http://svn.sourceforge.net/viewcvs.cgi/grid-portal/code/gridportal">viewcvs :: repository browsing</a></li>
	</ul>



	<h2>Documentation</h2>
	
	<h3>System documents</h3>
	<ul>
		<li>GRIDportal whitepaper :: [<a href="docs/sys01-whitepaper-tex/gridportal-white_paper.pdf">pdf</a>]</li>
		<li>GRIDportal test report :: [<a href="docs/sys02-test-tex/gridportal-test_report.pdf">pdf</a>]</li>
	</ul>

	<h3>Publications</h3>
	<ul>
		<li>Presentation at <a href="http://www.medisin.ntnu.no/">Medisinsk teknisk forskningssenter</a> (NTNU), Nov 10 2005 :: [<a href="docs/pub03-mtfs05/gridportal_mtfs05.pdf">pdf</a>] </li>
		<li><a href="http://www.fys.uio.no/epf/nordic-network/xi/1stNGNconf.htm">1st Nordic Grid Neighbourhood Conference 2005</a> presentation :: [<a href="docs/pub02-ngn05/gridportal_ngn05.pdf">pdf</a>] </li>
		<li><a href="http://www.notur.no/notur2005/">NOTUR 2005</a> conference poster :: [<a href="docs/pub01-notur05/gridportal_notur05.pdf">pdf</a>] </li>
	</ul>
	
	<h3>Tutorials</h3>
	<ul>
		<li>Practical GRIDportal with mpiBLAST, Nov 30 2005 :: [<a href="docs/tut01_bioinf05-tex/gridportal-tut_bioinf05.pdf">pdf</a>] </li>
	</ul>
	
	
	
	<h2>GridPortalToolkit</h2>
	
	To get started with using GRIDportal, you will need to use the <a href="/gptoolkit">GridPortalToolkit</a>.
	
	

	<h2>Development</h2>

	<h3>Requests for comments</h3>
	<ul>
		<li>rfc01 - authentication
		<ul>
			<li><a href="docs/rfc01-auth/v1.1">v1.1</a> :: July 08, 2005
			<li><a href="docs/rfc01-auth/v1.0">v1.0</a> :: July 06, 2005
		</ul>
		<li>rfc02 - [mpi]blast runtime environment
		<ul>
			<li><a href="docs/rfc02-blast_runtime_env/v1.0">v1.0</a> :: July 22, 2005
		</ul>
		
	</ul>

	
	<h3>ChangeLog</h3>
	<pre><? @include("docs/ChangeLog"); ?></pre>


	<div class="copy">
		&copy; 2005 <a href="mailto:numerodix(|)gmail.com">Martin Matusiak</a>. All content on this website is licenced under a <a href="http://creativecommons.org/licenses/by-nc-sa/2.5/">Creative Commons License</a>.
	</div>

</body>
</html>
