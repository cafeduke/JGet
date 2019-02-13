#!/usr/local/bin/perl
#
# chkNM.pl
# 
# Copyright (c) 2008, Oracle.  All rights reserved.  
#
#    NAME
#      chkNM.pl - <one-line expansion of the name>
#
#    DESCRIPTION
#      Check to see if NM is up;  if not, start it...
#
#    NOTES
#      To call:  perl chkNM.pl
#
#    MODIFIED   (MM/DD/YY)
#    kdclark     09/10/14 - Convert to 12.1.4
#    kdclark     06/19/12 - Creation

use File::Basename;
my $CURRENT_DIR = dirname(__FILE__);
require "$CURRENT_DIR/support.pl";

$platform = $ENV{'OSTYPE'};
if ($platform =~ /MSWin32/) { $platform = "nt"; }

# Set up platform specific stuff
if ($platform eq "nt") {
  $SD  = "\\";
} else {
  $SD  = "\/";
}

$DOMAIN_HOME     = $ENV{DOMAIN_HOME};
$twork           = $ENV{T_WORK};
$BADdif          = $twork . $SD . "BAD_ENVIRONMENT.dif";
$INSTALL_TYPE    = $ENV{INSTALL_TYPE};

# Exit immediately if environment is suspect
if (-e $BADdif) {
   print "ERROR:  BAD_ENVIRONMENT.dif detected in T_WORK.\nThis environment is suspect.  Exiting...\n";
   exit 0;
}

# ------------------------------
# Ping NodeManager - is a process running?

$count = 0;
if ($platform eq "nt") {
  # Note we need to know what INSTALL_TYPE we have here as a different ps
  # comamnd is required on standalone vs collocated.
  if ($INSTALL_TYPE eq "standalone") {
    $ping = "ps -ef | grep weblogic.NodeManager";
  } else {
    $ping  = "ps -ef | grep jps-config";
  }
} elsif ($platform =~ /hpia64/) {
  $ping = "ps -ax | grep weblogic.NodeManager";
} elsif ($platform =~ /solaris/) {
  $ping = "/usr/ucb/ps awwx | grep weblogic.NodeManager";
} else {
  $ping  = "ps -ef | grep weblogic.NodeManager";
}
$ping = $ping . " | grep -v grep | wc -l";
$output = 0;

print "Ping: $ping\n\n";
$output = `$ping`;

# Is Node Manager up already?
if ($output > 0)  {
   print "NodeManager is up & running already. No action necessary.\n\n";
   exit 0;
} 

# NodeManager is not up.  Okay, try to start it...
$startcmd = $DOMAIN_HOME . $SD . "bin" . $SD;
if ($platform eq "nt") {
   $startcmd = "start ".  $startcmd . "startNodeManager.cmd";
} else {
   $startcmd = $startcmd . "startNodeManager.sh &";
}
  
print "NodeManager is not up.  Attempting to start it...\nCmd:  $startcmd\n\n";
system($startcmd);
sleep 20;
 
# Ping NM again -- okay it should be up now
$output = `$ping`;
if ($output > 0)  {
   print "NodeManager has been started successfully.\n\n";
   exit 0;
}

# ------------------------------
# If we reach this point, NM failed to start...

$fileText = "FAILURE:  Unable to start the NodeManager.\nNodeManager up and running is a prequisite for testing.  Please triage this issue before attempting to re-run the tests.  See chkNM.log for details.\n\n";
print $fileText;

open(OUTFILE, ">$BADdif") || die "Unable to open $BADdif";
print OUTFILE $fileText;
close(OUTFILE);

