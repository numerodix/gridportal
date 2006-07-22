#!/bin/sh


### project/package info

PROJECT=grid-portal

SRCPATH=code

MDUPATH=code/mdu


### where to install this script

INS_PATH=/usr/local/bin
INS_EX=gp


### sf.net info

SVNURI=https://svn.sourceforge.net/svnroot/$PROJECT
SVNUSER=numerodix

SHELLHOST=shell.sf.net
SHELLUSER=numerodix

RELUPCMD=ncftpput
RELHOST=upload.sourceforge.net
RELPATH=incoming

WEBPATH=/home/groups/g/gr/$PROJECT/htdocs


### do not edit below this line

CO_PATH=


install() {
	# check if I'm being run from outside my directory
	if [[ `dirname $0` != "." ]]
	then
		echo "Please run me from my directory when installing."
		exit 1
	else
		cat $0 | sed "s|^CO_PATH=.*$|CO_PATH="$PWD"|g" > $INS_PATH/$INS_EX
		chmod +x $INS_PATH/$INS_EX
	fi
}


remove() {
	rm $INS_PATH/$INS_EX
}


pathcheck() {
	if [[ $CO_PATH == "" ]] 
	then
		echo "\$CO_PATH [ie. install path] not set, run install function."
		exit 1
	else
		cd $CO_PATH
	fi
}


clean() {
	pathcheck
	find . -name '*.pyc' -print -exec rm {} \;
	find . -name '*~' -print -exec rm {} \;
	find . -name '*autosave' -print -exec rm {} \;
}


sh() {
	ssh -l $SHELLUSER $SHELLHOST
}


### svn functions

svnco() {
	pathcheck
	svn co $SVNURI .
}


svnupd() {
	pathcheck
	svn update
}


svnci() {
	pathcheck
	clean
	svn --username $SVNUSER ci
}


### web functions

webup() {
	pathcheck
	clean
	scp * $SHELLUSER@$SHELLHOST:$WEBPATH
}


### per-package build & publish functions

dist() {
	# usage: gp dist <package>
	pathcheck
	cd $SRCPATH/$1
	ant
}


distclean() {
        # usage: gp distclean <package>
        pathcheck
        cd $SRCPATH/$1
        ant distclean
}


relup() {
	# usage: gp relup <package> <release-version>
	pathcheck
	$RELUPCMD $RELHOST $RELPATH $SRCPATH/$1/dist/$1-$2*
}



$*

exit 0
