#!/usr/local/bin/perl
# Copyright (c) 2008, Oracle.  All rights reserved.  
#
#    NAME
#      ohs_restart.pl - <one-line expansion of the name>
#
#    DESCRIPTION
#      Restart a running OHS instance
#      Usage:  ohs_restart.pl -ohs <ohsName>
#
#      Note we are assuming ohsName is already up.
#     
#    NOTES
#      <other useful comments, qualifications, etc.>
#
#    MODIFIED   (MM/DD/YY)
#    kdclark     06/18/15 - Creation
# 

use File::Basename;
my $CURRENT_DIR = dirname(__FILE__);
require "$CURRENT_DIR/support.pl";

# Import required variables
$OHS_NAME = "";
$DEBUG    = 0;

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
$INSTALL_TYPE    = $ENV{INSTALL_TYPE};
$ADMIN_HOST      = $ENV{ADMIN_HOST};
$ADMIN_PORT      = $ENV{ADMIN_PORT};
$WLS_USER        = $ENV{WLS_USER};
$WLS_PWD         = $ENV{WLS_PWD};
$WLST            = $ENV{WLST_LOC};
$DOMAIN_NAME     = $ENV{DOMAIN_NAME};
$scriptBase      = "$CURRENT_DIR/../wlst/";

if ($INSTALL_TYPE eq "standalone") {
    $script = $scriptBase . "ohs_restart_standalone.py";
} else {
    $script = $scriptBase . "ohs_restart.py";
}

$cmd = $WLST . " $script"
  . " --ADMIN_HOST=$ADMIN_HOST"
  . " --WLS_USER=$WLS_USER"
  . " --WLS_PWD=$WLS_PWD"
  . " --OHS_NAME=$OHS_NAME"
  . " --DOMAIN_NAME=$DOMAIN_NAME";

# Do not include ADMIN_PORT for standalone OHS
if ($INSTALL_TYPE ne "standalone") {
  $cmd = $cmd . " --ADMIN_PORT=$ADMIN_PORT";
}

print "=============================\n";
print "Restarting OHS instance: $OHS_NAME\n\n$cmd\n\n";
print "=============================\n";

# Execute the command
system($cmd);

