#!/usr/local/bin/perl
# Copyright (c) 2008, Oracle.  All rights reserved.  
#
#    NAME
#      stopWLS.pl
#
#    DESCRIPTION
#      Check to see if WLS is running.  If it is, stop it...
#
#    NOTES
#      To call:  perl stopWLS.pl
#
#    MODIFIED   (MM/DD/YY)
#    kdclark     10/28/15 - Creation
# 

$platform     = $ENV{'OSTYPE'};
$DOMAIN_HOME  = $ENV{DOMAIN_HOME};
$fileText     = "";
$sleepTime    = "120";

if ($platform =~ /MSWin32/) { $platform = "nt"; }

# Set up platform specific stuff
if ($platform eq "nt") {
  $SD  = "\\";
} else {
  $SD  = "\/";
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
  $stopcmd = "start ". $stopcmd . "stopWebLogic.cmd";
} else {
  $stopcmd = $stopcmd . "stopWebLogic.sh";
}

# ------------------------------
# Initial check to see if WLS is running...

$output = 0;
print "Ping: $ping\n";
$output = `$ping`;

if ($output == 0) {
  print "Error:  WLS seems to be down already.  Exiting...\n\n";
  exit 1;
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
print "Error:  Looks like we timed out waiting to shutdown WLS.  Exiting...\n";
exit 1;

