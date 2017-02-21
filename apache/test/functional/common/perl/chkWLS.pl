#!/usr/local/bin/perl
#
# chkWLS.pl
# 
# Copyright (c) 2008, Oracle.  All rights reserved.  
#
#    NAME
#      chkWLS.pl - <one-line expansion of the name>
#
#    DESCRIPTION
#      Check to see if WLS is up;  if not, start it...
#
#    NOTES
#      To call:  perl chkWLS.pl
#
#    MODIFIED   (MM/DD/YY)
#    kdclark     09/10/14 - update to 'ps' in 12.1.4
#    kdclark     01/23/09 - update to work on Windows
#    kdclark     09/02/08 - Creation
# 

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

$twork        = $ENV{T_WORK};
$BADdif       = $twork . $SD . "BAD_ENVIRONMENT.dif";
$DOMAIN_HOME  = $ENV{DOMAIN_HOME};
$INSTALL_TYPE = $ENV{INSTALL_TYPE};
$fileText     = "";
$sleepTime    = "180";

# Exit immediately if suspect environment...
if (-e $BADdif) {
   print "ERROR:  BAD_ENVIRONMENT.dif detected in T_WORK.\nThis environment is suspect.  Exiting...\n";
   exit 0;
}

# Exit immediately if we have a standalone OHS install. 
# WLS does not exist for standalone OHS, so nothing to do here...
if ($INSTALL_TYPE eq "standalone") {
   print "This is a standalone OHS install.  WLS does not exist.  Exiting...\n";
   exit 0;
}

# ------------------------------
# Setup Ping WLS command

if ($platform eq "nt") {
  $ping  = "ps -ef | grep weblogic.Name=AdminServer";
} elsif ($platform =~ /hpia64/) {
  $ping = "ps -ax | grep weblogic.Name=AdminServer";
} elsif ($platform =~ /solaris/) {
  $ping = "/usr/ucb/ps awwx | grep weblogic.Name=AdminServer";
} else {
  $ping  = "ps -ef | grep weblogic.Name=AdminServer";
}
$ping = $ping . " | grep -v grep | wc -l";

# ------------------------------
# Setup Start WLS command - run in background

$startcmd = $DOMAIN_HOME . $SD . "bin" . $SD;
if ($platform eq "nt") {
  $startcmd = "start " . $startcmd . "startWebLogic.cmd";
} else {
  $startcmd = $startcmd . "startWebLogic.sh &";
}

# ------------------------------
# Initial check to see if WLS is up...

$output = 0;
print "Ping: $ping\n";
$output = `$ping`;

if ($output > 0) {
  print "WLS is already up & running. No action is necessary.\n\n";
  exit 0;
} 
  
print "WLS is not up.  Attempting to start it...\nCmd:  $startcmd\n\n";
system($startcmd);
sleep $sleepTime;

# Wait for WLS to come up...
$count = 1;
while ($count < 11) {

    # Ping WLS to see if it's up...
    print "Ping \#" . $count . ": Is WLS up?\n";
    $output = `$ping`;
    if ($output > 0) { 
      print "WLS started successfully!\n\n";
      exit 0;
    }
    $count++;
    sleep 30;
}

# --------------------------------------
# If we reached this point, WLS failed to come up...

$fileText = "FAILURE:  Unable to start WLS.\nIn a collocated environment (both full domain and compact domain), WLS needs to be up and running for testing.  Please triage this problem before trying to re-run the tests.  See chkWLS.log for details.\n\n";
print $fileText;

open(OUTFILE, ">$BADdif") || die "Unable to open $BADdif";
print OUTFILE $fileText;
close(OUTFILE);

