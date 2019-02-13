#!/usr/local/bin/perl
# stopComp.pl
#
# Stop component from $DOMAIN_HOME/bin/stopComponent
# Usage:  perl stopComp.pl <ohsName>
#
# Copyright (c) 2001, 2006, Oracle. All rights reserved.  
#

$OHS_NAME    = $ARGV[0];
$DOMAIN_HOME = $ENV{DOMAIN_HOME};
$twork       = $ENV{T_WORK};

#if (!$OHS_NAME) {
#   print "ERROR:  It appears an OHS instance name was not provided!\n";
#   exit 1;
#}

# Set up platform specific stuff
$platform = $ENV{'OSTYPE'};
if ($platform =~ /MSWin32/) { $platform = "nt"; }

if ($platform eq "nt") {
  $SD  = "\\";
  $scriptPath = $DOMAIN_HOME . $SD . "bin" . $SD . "stopComponent.cmd";
} else {
  $SD  = "\/";
  $scriptPath = $DOMAIN_HOME . $SD . "bin" . $SD . "stopComponent.sh";
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
