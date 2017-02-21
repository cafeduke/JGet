#!/usr/local/bin/perl
# Copyright (c) 2008, Oracle.  All rights reserved.  
#
#    NAME
#      startWLS.pl
#
#    DESCRIPTION
#      Check to see if WLS is up;  if not, start it...
#
#    NOTES
#      To call:  perl startWLS.pl
#
#    MODIFIED   (MM/DD/YY)
#    kdclark     10/28/15 - Creation
# 

$platform     = $ENV{'OSTYPE'};
$DOMAIN_HOME  = $ENV{DOMAIN_HOME};
$fileText     = "";
$sleepTime    = "120";  # two minutes

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
  print "Error:  WLS seems to be up already.  Exiting...\n\n";
  exit 1;
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

print "Error:  Looks like we timed out waiting for WLS to start.  Exiting...\n";
exit 1;
