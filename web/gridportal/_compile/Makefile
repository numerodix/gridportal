PATH_SCRIPTS = /home/alex/zcool/06/hp/svn/www/_scripts
PDF := $(patsubst %.lyx,%.pdf,$(wildcard *.lyx))
HTML := $(patsubst %.lyx,%.html,$(wildcard *.lyx))
EPS_PDF := $(patsubst %.eps,%.pdf,$(wildcard *.eps))
EPS_HTML := $(patsubst %.eps,%.png,$(wildcard *.eps))

all: images pdf_install clean

images: ${EPS_PDF}
	
%.png: %.eps
	convert -scale 600 $< $@

%.pdf: %.eps
	epstopdf $<

pdf_install: ${PDF}
	-cp ${PDF} ..

html_install: ${HTML}
	-cp ${HTML} ..
	-mv *.png ..
	-mv *.jpg ..

%.tex: %.lyx
	lyx -e latex $<

%.pdf: %.tex
	pdflatex $<
	pdflatex $<

%.html: %.tex
	latex2html -split 0 -no_navigation -info 0 -no_subdir \
		-html_version 4.0,unicode -address 0 \
		-antialias -antialias_text -white \
		-init_file ${PATH_SCRIPTS}/latex2html-init \
		$<
	sed -e "s/$*.css/..\/..\/_scripts\/web.css/g" $*.html > $*.htmla
	mv $*.htmla $*.html
	sed -e "s/img1.png/logo.png/g" $*.html > $*.htmla
	mv $*.htmla $*.html
	-rm img1.png
	
force:
	touch *.lyx
	
clean:
	-rm *.html *.pdf labels.pl internals.pl images.pl WARNINGS *.css \
	*.tex *.log *.aux *.txt *.dvi *.ps *~ *.sgml *.toc *.lot *.lof \
	.out *out *.old