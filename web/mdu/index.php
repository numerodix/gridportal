<html>
<head>
	<title>[m]piBlast [D]atabase [U]pdater (mdu)</title>

<link rel="stylesheet" href="/styles.css" type="text/css">
<link rel="stylesheet" href="_scripts/styles.css" type="text/css">
</head>

<body>

<?      include ("../menu.php");        ?>

	<h1>[m]piBlast [D]atabase [U]pdater (mdu)</h1>

	<div class="img_r">
		<a href="_images/mdu_sshot.png">
			<img src="_images/mdu_sshot_t.png" width="300"
				alt="mdu screenshot" border="0">
		</a>
	</div>
	[m]piBlast [D]atabase [U]pdater, written in Python, is a small command line based application used to update or install (since the process is identical in both cases) of databases for use with mpiBLAST. It attempts to streamline the process, so that mdu can be submitted as a job to a cluster machine and run unattended.<br/>
	<br/>
	The difference between using mdu and doing manual updates is that with mdu, you can set which databases should be updated, decide how it should be carried out and start the application. Then let mdu do all the work (and tedious waiting for downloads and formatting), while you watch an episode of <a href="http://en.wikipedia.org/wiki/Seinfeld">Seinfeld</a>. You can even set mdu to run as a <a href="http://en.wikipedia.org/wiki/Cron">cron job</a>.
	<br/><br/>
	mdu is distributed under the <a href="http://www.gnu.org/copyleft/gpl.html">GPL licence</a>.
	<br/><br/>
	Martin Matusiak - mdu project manager
	

	<h2>Download</h2>

	<h3>Dependencies</h3>
	<ul>
		<li>Linux [could probably work on a POSIX operating system with gnu]
		<li><a href="http://www.python.org/">Python</a>
		<li><a href="http://mpiblast.lanl.gov/">mpiBLAST</a> (preferably 1.4+)
		<li><a href="http://www.ncbi.nlm.nih.gov/BLAST/">BLAST</a>
	</ul>
	mdu also depends on a range of gnu tools, like..
	<ul>
		<li>cp, mv, rm
		<li>bash
		<li>curl
		<li>gunzip
		<li>sed
		<li>tar
		<li>wget
	</ul>
	
	<h3>Download</h3>
	Download a release:
	<ul>
		<li><a href="https://sourceforge.net/project/showfiles.php?group_id=171912&package_id=198082">download from sourceforge.net</a>
	</ul>
	<br/>
	To download the latest version of mdu, use <a href="http://subversion.tigris.org/">subversion</a> to check out the files from the repository.
	<br/><br/>
	<div class="sh">$ svn checkout https://svn.sourceforge.net/svnroot/grid-portal/code/mdu/src/mdu</div>
	
	<h3>Source code repository</h3>
	To browse the source code repository, use one of the following methods:
	<ul>
		<li><a href="http://svn.sourceforge.net/viewcvs.cgi/grid-portal/code/mdu">viewcvs :: repository browsing</a></li>
	</ul>



	<h2>Documentation</h2>
	
	<h3>README</h3>
	<pre><? @include("docs/README"); ?></pre>


	<div class="copy">
		&copy; 2005 <a href="mailto:numerodix(|)gmail.com">Martin Matusiak</a>. All content on this website is licenced under a <a href="http://creativecommons.org/licenses/by-nc-sa/2.5/">Creative Commons License</a>.
	</div>

</body>
</html>
