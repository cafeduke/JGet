#!/usr/local/bin/perl
# deleteInstance.pl
# 
# Copyright (c) 2008, Oracle.  All rights reserved.  
#
#    NAME
#      deleteInstance.pl - <one-line expansion of the name>
#
#    DESCRIPTION
#      Deletes an OHS instance.  Example
#      
#      deleteInstance.pl -ohs <ohsName>
#         [-delete]          # force deletion of instance
#         [-old]             # use older WLST commands
#
#    MODIFIED   (MM/DD/YY)
#    kdclark     09/10/14 - modify for 12.2.1
#    kdclark     06/14/12 - Creation
# 

use File::Basename;
my $CURRENT_DIR = dirname(__FILE__);
require "$CURRENT_DIR/support.pl";

# Jython script
$script = "$CURRENT_DIR/../wlst/ohs_delete_instance.py";

# Account for OS dependent features
$os = $ENV{'OS'};
if(!$os) {
   $os = "Linux";
}
$SD="\/";
$SD="\\" if($os=~/Windows/);

# Default Settings
$USE_OLD  = 0;           # use custom command to delete instances
$DELETE   = 0;           # do not force deletion of instance
$DEBUG    = 0;           # debug mode off by default

# Capture any passed-in parameters
foreach $i (0 .. $#ARGV) {
  next if (!($ARGV[$i] =~ /-/));    # ignore non flags
  if ($ARGV[$i] =~ /-ohs/) {
    $OHS_NAME = $ARGV[i+1];
    print "OHS is $OHS_NAME\n";
  }
  if ($ARGV[$i] =~ /-old/) {
    $USE_OLD = 1;
  }
  if ($ARGV[$i] =~ /-delete/) {
    $DELETE = 1;
  }
}

# Make sure an instance name was provided - safety step.  Don't want
# to automatically delete "ohs1" here by accident.
if (!$OHS_NAME) {
  print "ERROR:  No OHS instance name was provided!\nExiting...";
  exit 1;
}

# Import required variables
$ORACLE_HOME     = $ENV{ORACLE_HOME};
$twork           = $ENV{T_WORK};
$ADMIN_HOST      = $ENV{ADMIN_HOST};
$ADMIN_PORT      = $ENV{ADMIN_PORT};
$WLS_USER        = $ENV{WLS_USER};
$WLS_PWD         = $ENV{WLS_PWD};
$wlst            = $ENV{WLST_LOC};
$ohsConfDir      = $ENV{ohsConfDir};
$ohsLogDir       = $ENV{ohsLogDir};
$nmLogDir        = $ENV{nmLogDir};

# CLASSPATH is included as it can mess up wlst.sh

# Be sure WLST is set correctly
if (!-e $wlst) {
  print "ERROR:  Unable to locate $wlst.\nExiting...\n";
  exit 1;
}

# Display variables
if ($DEBUG) {
  print "Running in Debug Mode:\n";
  print "----------------------\n";
  print "ORACLE_HOME:      $ORACLE_HOME\n";
  print "OHS_NAME:         $OHS_NAME\n";
  print "T_WORK:           $twork\n";
  print "ADMIN_HOST:       $ADMIN_HOST\n";
  print "ADMIN_PORT:       $ADMIN_PORT\n";
  print "User:             $WLS_USER\n";
  print "Password:         $WLS_PWD\n";
  print "WLST_LOC:         $wlst\n";
  print "CLASSPATH:        $CLASSPATH\n\n";
}

# ----------------------
# Delete the OHS Instance

# Run WLST script
$cmd = $wlst . " $script"
       . " --ADMIN_HOST=$ADMIN_HOST"
       . " --ADMIN_PORT=$ADMIN_PORT"
       . " --WLS_USER=$WLS_USER"
       . " --WLS_PWD=$WLS_PWD"
       . " --OHS_NAME=$OHS_NAME";

# Optional parameters
if ($USE_OLD) {
  $cmd = $cmd . " --USE_OLD=$USE_OLD";
}
if ($DELETE) {
  $cmd = $cmd . " --DELETE=$DELETE";
}

print "=============================\n";
print "Removing OHS instance: $OHS_NAME\n\n$cmd\n\n";
print "=============================\n";

#systemWithAbort($cmd);
system($cmd);

# ----------------------
# Remove the associated port file
# As the OHS instance is being deleted, there's no point to keeping this
# file around any more to track the instance's port info.

$PORT_FILE = $twork . $SD . "ports-" . $OHS_NAME;
if (-e $PORT_FILE) {
  print "=============================\n";
  print "Detected a port file:  $PORT_FILE\nRemoving it...\n";
  $cmd = "rm $PORT_FILE";
  #systemWithAbort($cmd);
  system($cmd);
  print "Port file removed.\n";
  print "=============================\n";
}

# ----------------------
# If the delete flag is on, completely destroy the OHS instance

if ($DELETE) {
  # Need to ensure that the directories are absolutely wiped for SRGs

$OHS_PATH1 = $ohsConfDir . $SD . $OHS_NAME;
$OHS_PATH2 = $ohsConfDir . $SD . "instances" . $SD . $OHS_NAME;
$OHS_PATH3 = $nmLogDir . $SD . $OHS_NAME;
$OHS_PATH4 = $ohsLogDir . $SD . $OHS_NAME;

################################################
### Update this section once bugs are fixed  ###
################################################
# Need blow away all directories...
  $cmd1 = "rm -rf $OHS_PATH1";
  $cmd2 = "rm -rf $OHS_PATH2";
  $cmd3 = "rm -rf $OHS_PATH3";
  $cmd4 = "rm -rf $OHS_PATH4";

  print "=============================\n";
  print "Performing a full deletion of the instance...\n";
  print "$cmd1\n$cmd2\n$cmd3\n$cmd4\n\n";
  system($cmd1);
  system($cmd2);
  system($cmd3);
  system($cmd4);
  print "Instance $OHS_NAME fully removed.\n\n";
  print "=============================\n";

}

