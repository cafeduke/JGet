#!/usr/local/bin/perl
#
# stopWLS.pl
# 
# Copyright (c) 2008, Oracle.  All rights reserved.  
#
#    NAME
#      stopWLS.pl - <one-line expansion of the name>
#
#    DESCRIPTION
#      Check to see if WLS is running.  If it is, stop it...
#
#    NOTES
#      To call:  perl stopWLS.pl
#
#    MODIFIED   (MM/DD/YY)
#    kdclark     06/30/15 - Creation
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
$DOMAIN_HOME  = $ENV{DOMAIN_HOME};
$INSTALL_TYPE = $ENV{INSTALL_TYPE};
$fileText     = "";
$sleepTime    = "120";

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
# Setup Shutdown WLS command

$stopcmd = $DOMAIN_HOME . $SD . "bin" . $SD;
if ($platform eq "nt") {
  $stopcmd = $stopcmd . "stopWebLogic.cmd";
} else {
  $stopcmd = $stopcmd . "stopWebLogic.sh";
}

# ------------------------------
# Initial check to see if WLS is running...

$output = 0;
print "Ping: $ping\n";
$output = `$ping`;

if ($output == 0) {
  print "WLS is already down. No action is necessary.\n\n";
  exit 0;
} 
  
print "WLS is up.  Attempting to stop it...\nCmd:  $stopcmd\n\n";
system($stopcmd);
sleep $sleepTime;

# Wait for WLS to shutdown
$count = 1;
while ($count < 11) {

    # Ping WLS to see if it's up...
    print "Ping \#" . $count . ": Is WLS up?\n";
    $output = `$ping`;
    if ($output == 0) { 
      print "WLS shutdown successfully!\n\n";
      exit 0;
    }
    $count++;
    sleep 30;
}

# If we get here, WLS failed to shutdown... exit with error.
print "ERROR:  WLS failed to shutdown in a timely manner.  Exiting...\n";
return 1;

