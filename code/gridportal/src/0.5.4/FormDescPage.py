from WebKit.Page import Page

from HyperText.HTML import *

from conf_main import *
from conf_mpiblast import *

import Lap

class FormDescPage(Page):

	def title(self):
		return 'Form descriptions'

	def writeContent(self):
	
		wr = self.writeln
		w = self.write
		
		formDescriptions = """
		
		<p><a name="cpuTime"></a>
		<strong>CPU time (m)</strong>
		<br>Maximum allowed time for this job to complete (in minutes). If job exceeds this time limit, it will be terminated. Be advised that setting a very long execution time limit for a job will send it to the 
		back of the job queue because the grid gives priority to short jobs.
		</p>

		<p><a name="jobName"></a>
		<strong>Job name</strong>
		<br>Assign a special name to identify the job among others. Job name can be set to any string of alphanumeric characters [a-z,A-Z,0-9] but must <strong>not</strong> contain white spaces!
		</p>
		
		<p><a name="email"></a>
		<strong>Email notification</strong>
		<br>An optional email address to send you notifications of when the job is started and when it has completed.
		</p>
		
		<p><a name="seq"></a>
		<strong>Enter query sequence(s)</strong>
		<br>The BLAST 'Search' box accepts a number of different types of input and automatically determines the format.
		</p>
						
		<p><a name="seqInputFile"></a>
		<strong>Sequences input file</strong>
		<br>Select file containing sequences instead of inputing them in the sequences input box. Sequences must be input either with a file or through the sequences box.
		</p>
						
		<p><a name="e_value"></a>
		<strong>Expectation value (E)</strong>
		<br>The statistical significance threshold for reporting matches against database sequences; the default value is 10, meaning that 10 matches are expected to be found merely by chance, according to the stochastic model of Karlin and Altschul (1990). If the statistical significance ascribed to a match is greater than the EXPECT threshold, the match will not be reported. Lower EXPECT thresholds are more stringent, leading to fewer chance matches being reported. Increasing the threshold shows less stringent matches. Fractional values are acceptable.
		</p>
		
		<p><a name="html_output"></a>
		<strong>HTML format on output</strong>
		<br>Output results of query in html format.
		</p>
						
		<p><a name="filter"></a>
		<strong>Filter query sequence</strong>
		<br>Filter query sequence (DUST with blastn, SEG with others). Can be overridden by setting this argument in the Advanced parameters field.
		</p>
						
		<p><a name="scores"></a>
		<strong>Number of scores/descriptions reported</strong>
		<br>Restricts the number of short descriptions of matching sequences reported to the number specified; default limit is 100 descriptions. See also EXPECT.
		</p>
		
		<p><a name="alignments"></a>
		<strong>Number of alignments reported</strong>
		<br>Restricts database sequences to the number specified for which high-scoring segment pairs (HSPs) are reported; the default limit is 100. If more database sequences than this happen to satisfy the statistical significance threshold for reporting (see EXPECT), only the matches ascribed the greatest statistical significance are reported.
		</p>
						
		<p><a name="location"></a>
		<strong>Location on query sequence [10,400]</strong>
		<br>A region of the query sequences can be used to be used for BLAST searching. You can enter the range in nucleotides or protein residues in the box. For example to limit matches to the region from nucleotide 24 to nucleotide 200 of a query sequence, you would enter "24,200". If one of the limits you enter is out of range, the intersection of the [From,To] and [1,length] intervals will be searched, where length is the length of the whole query sequence.
		</p>
						
		<p><a name="adv_params"></a>
		<strong>Advanced parameters</strong>
		<br>Enter additional parameters here for customized job settings. You can also use this field to override any of the settings you have chosen on the form.
		<br>
		<br>Consult the <a href="http://bioinformatics.ubc.ca/resources/tools/index.php?name=blastall" target="_new">blastall documentation</a>.
		</p>
		
		<p><a name="wordsize"></a>
		<strong>Word Size</strong>
		<br>BLAST is a heuristic that works by finding word-matches between the query and database sequences. One may think of this process as finding "hot-spots" that BLAST can then use to initiate extensions that might lead to full-blown alignments. For nucleotide-nucleotide searches (i.e., "blastn") an exact match of the entire word is required before an extension is initiated, so that one normally regulates the sensitivity and speed of the search by increasing or decreasing the word-size. For other BLAST searches non-exact word matches are taken into account based upon the similarity between words. The amount of similarity can be varied so one normally uses just the word-sizes 2 and 3 for these searches.
		</p>
						
		<p><a name="matrix"></a>
		<strong>Matrix</strong>
		<br>A key element in evaluating the quality of a pairwise sequence alignment is the "substitution matrix", which assigns a score for aligning any possible pair of residues.  The matrix used in a BLAST search can be changed depending on the type of sequences you are searching with (see the <a href="http://www.ncbi.nlm.nih.gov/BLAST/blast_FAQs.html" target="ncbi"> BLAST Frequently Asked Questions</a>).
		<br>
    <br>More information on <a href="http://www.ncbi.nlm.nih.gov/blast/html/sub_matrix.html" target="ncbi">BLAST substitution matrices</a>
		</p>
						
		<p><a name="query_database"></a>
		<strong>Database</strong>
		<br>Choose database for query. The drop down menu reflects the databases installed on this grid.
		</p>			
		
		<p><a name="gi_sequence"></a>
		<strong>Restrict search of database to list of GI's</strong>
		<br>Enter gi numbers specifying sequences to be searched in, one per line.
		</p>				
								
		"""
		
		table = TABLE()
		tableBody = TBODY()
		table.append(tableBody)
		tableBody.append(TR(TD(formDescriptions)))
		                                                                
		w(table)

	def writeStyleSheet(self):
		self.writeln(LINK(rel="stylesheet",href="css/lap.css"))		