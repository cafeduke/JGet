#!/usr/local/bin/perl
# startComp.pl
#
# Start component from $DOMAIN_HOME/bin.  Will automatically set up the
# keyfiles if not already done.  Requires that the appropriate keyfiles
# be in T_WORK.
# Usage:  perl startComp.pl <ohsName>
#
# Copyright (c) 2001, 2006, Oracle. All rights reserved.  
#

$OHS_NAME    = $ARGV[0];
$DOMAIN_HOME = $ENV{DOMAIN_HOME};
$DOMAIN_NAME = $ENV{DOMAIN_NAME};
$twork       = $ENV{T_WORK};

#if (!$OHS_NAME) {
#   print "ERROR:  It appears an OHS instance was not provided!\n";
#   exit 1;
#}

# Set up platform specific stuff
$platform = $ENV{'OSTYPE'};
if ($platform =~ /MSWin32/) { $platform = "nt"; }

if ($platform eq "nt") {
  $SD  = "\\";
  $scriptPath = $DOMAIN_HOME . $SD . "bin" . $SD . "startComponent.cmd";
} else {
  $SD  = "\/";
  $scriptPath = $DOMAIN_HOME . $SD . "bin" . $SD . "startComponent.sh";
}

# Check to be sure DOMAIN_HOME is good
if (!-e $scriptPath) {
  print "ERROR:  Unable to find $scriptPath.\nExiting...";
  exit 1;
}

# Assemble command
$cmd = $scriptPath . " " . $OHS_NAME;
print "Running:\n$cmd\n";
system($cmd);
