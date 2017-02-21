#!/usr/local/bin/perl
# Copyright (c) 2008, Oracle.  All rights reserved.  
#
#    NAME
#      ohs_state.pl - <one-line expansion of the name>
#
#    DESCRIPTION
#      Get the state of a given OHS instance
#      Use appropriate WLST commands to capture
#      Usage:  ohs_state.pl -ohs <ohsName>
#     
#    NOTES
#      <other useful comments, qualifications, etc.>
#
#    MODIFIED   (MM/DD/YY)
#    kdclark     09/11/14 - update for 12.2.1
#    kdclark     09/24/08 - Creation
# 

use File::Basename;
my $CURRENT_DIR = dirname(__FILE__);
require "$CURRENT_DIR/support.pl";

# Import required variables
$OHS_NAME = "";

# Pull in parameters
foreach $i (0 .. $#ARGV) {
  next if (!($ARGV[$i] =~ /-/));    # ignore non flags
  if ($ARGV[$i] =~ /-ohs/) {
    $OHS_NAME=$ARGV[$i+1];
  }
}

if (!$OHS_NAME) {
   print "ERROR:  No OHS_NAME was provided!\n";
   exit 1;
}

$twork           = $ENV{T_WORK};
$ADMIN_HOST      = $ENV{ADMIN_HOST};
$ADMIN_PORT      = $ENV{ADMIN_PORT};
$WLS_USER        = $ENV{WLS_USER};
$WLS_PWD         = $ENV{WLS_PWD};
$WLST            = $ENV{WLST_LOC};
$INSTALL_TYPE    = $ENV{INSTALL_TYPE};
$DOMAIN_NAME     = $ENV{DOMAIN_NAME};
$scriptBase      = "$CURRENT_DIR/../wlst/";

$ENV{'WLST_PROPERTIES'}="-Dwlst.offline.log=/home/kdclark/wlst.log -Dwlst.log.priority=all";

if ($INSTALL_TYPE eq "standalone") {
    $script = $scriptBase . "ohs_state_standalone.py";
} else {
    $script = $scriptBase . "ohs_state.py";
}

$cmd = "env > /home/kdclark/outReal.txt";
system($cmd);

$cmd = $WLST . " $script $ENV{'WLST_PROPERTIES'}"
  . " --ADMIN_HOST=$ADMIN_HOST"
  . " --WLS_USER=$WLS_USER"
  . " --WLS_PWD=$WLS_PWD"
  . " --OHS_NAME=$OHS_NAME"
  . " --DOMAIN_NAME=$DOMAIN_NAME";

# Do not include ADMIN_PORT for standalone OHS
if ($INSTALL_TYPE ne "standalone") {
  $cmd = $cmd . " --ADMIN_PORT=$ADMIN_PORT";
}

# FYI:  If CLASSPATH is set to some values in one's environment
# can kill WLST.

print "=============================\n";
print "Capture state of OHS instance: $OHS_NAME\n\n$cmd\n\n";
print "=============================\n";

# Execute the command
system($cmd);

