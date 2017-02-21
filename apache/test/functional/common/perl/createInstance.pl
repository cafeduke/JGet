#!/usr/local/bin/perl
# createInstance.pl
# 
# Copyright (c) 2008, Oracle.  All rights reserved.  
#
#    NAME
#      createInstance.pl - <one-line expansion of the name>
#
#    DESCRIPTION
#      Creates an OHS instance.  Example:
#
#      createInstance.pl
#          [-ohs <ohsName>]          # default = ohs1
#          [-machine <machineName>]  # default = $ENV{'MACHINE_NAME'}
#          [-listen XXXX]            # set listen port
#          [-ssl XXXX]               # set ssl port
#          [-admin XXXX]             # set admin port
#          [-old]                    # use older WLST commands to create
#          [-nodelete]               # do not delete if ohsName exists
#
#    MODIFIED   (MM/DD/YY)
#    kdclark     06/14/12 - Creation
#    kdclark     10/29/12 - enhanced version
# 

use File::Basename;
my $CURRENT_DIR = dirname(__FILE__);
require "$CURRENT_DIR/support.pl";

# Jython Script
$script = "$CURRENT_DIR/../wlst/ohs_create_instance.py";

# Account for OS dependent features
$os = $ENV{'OS'};
if(!$os) {
   $os = "Linux";
}
$SD="\/";
$SD="\\" if ($os=~/Windows/);

# Default Settings
$USE_OLD  = 0;           # default to custom command
$OHS_NAME = "ohs1";      # default OHS instance name
$NODELETE = 0;           # delete instance if it already exists
$DEBUG = 0;              # debug mode off by default

# Capture any passed-in parameters
foreach $i (0 .. $#ARGV) {
  next if (!($ARGV[$i] =~ /-/));    # ignore non flags
  if ($ARGV[$i] =~ /-ohs/) {
    $OHS_NAME=$ARGV[$i+1];
  }
  if ($ARGV[$i] =~ /-listen/) {
    $OHS_LISTEN_PORT=$ARGV[$i+1];
  } 
  if ($ARGV[$i] =~ /-ssl/) {
    $OHS_SSL_PORT=$ARGV[$i+1];
  }
  if ($ARGV[$i] =~ /-admin/) {
    $OHS_ADMIN_PORT=$ARGV[$i+1];
  }
  if ($ARGV[$i] =~ /-machine/) {
    $MACHINE_NAME=$ARGV[$i+1];
  }
  if ($ARGV[$i] =~ /-old/) {
    $USE_OLD = 1;
  }
  if ($ARGV[$i] =~ /-nodelete/) {
    $NODELETE = 1;
  }
}

# Set Variables
$ORACLE_HOME     = $ENV{ORACLE_HOME};
$twork           = $ENV{T_WORK};
$ADMIN_HOST      = $ENV{ADMIN_HOST};
$ADMIN_PORT      = $ENV{ADMIN_PORT};
$WLS_USER        = $ENV{WLS_USER};
$WLS_PWD         = $ENV{WLS_PWD};
$wlst    	 = $ENV{WLST_LOC};
$machine         = $ENV{MACHINE_NAME} unless $MACHINE_NAME;
$ohsConfDir      = $ENV{ohsConfDir};

# CLASSPATH included as it can mess up a launch of wlst.sh.

# Be sure WLST is set correctly
if (!-e $wlst) {
  print "ERROR:  Unable to locate WLST at:  $wlst.\nExiting...\n";
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
  print "Machine Name:     $machine\n";
  print "WLST_LOC:         $wlst\n";
}

# ----------------------
# Check if instance OHS_NAME exists already
# Do we need to delete it first?

$OHS_PATH = $ohsConfDir . $SD . $OHS_NAME;

if ((-d $OHS_PATH) && (!$NODELETE)) {
  print "=============================\n";
  print "It appears OHS instance:  $OHS_NAME already exists in this environment.\nAttempting to remove it...\n";

  # Fully remove the old instance with -delete flag
  $cmd = "perl $CURRENT_DIR/deleteInstance.pl "
            . "-ohs $OHS_NAME "
            . "-delete";
  print "$cmd\n\n";
  #systemWithAbort($cmd);
  system($cmd);

  print "=============================\n";
}

# ----------------------
# Create the OHS Instance

# Run WLST script
$cmd = $wlst . " $script"
       . " --ADMIN_HOST=$ADMIN_HOST" 
       . " --ADMIN_PORT=$ADMIN_PORT"
       . " --WLS_USER=$WLS_USER" 
       . " --WLS_PWD=$WLS_PWD"
       . " --OHS_NAME=$OHS_NAME" 
       . " --MACHINE_NAME=$machine";

# Optional parameters
if ($OHS_LISTEN_PORT) {
  $cmd = $cmd . " --OHS_LISTEN_PORT=$OHS_LISTEN_PORT";
}
if ($OHS_SSL_PORT) {
  $cmd = $cmd . " --OHS_SSL_PORT=$OHS_SSL_PORT";
}
if ($OHS_ADMIN_PORT) {
  $cmd = $cmd . " --OHS_ADMIN_PORT=$OHS_ADMIN_PORT";
}
if ($USE_OLD) {
  $cmd = $cmd . " --USE_OLD=$USE_OLD";
} 

print "=============================\n";
print "Creating OHS instance: $OHS_NAME\n\n$cmd\n\n";
print "=============================\n";

#systemWithAbort($cmd);
system($cmd);


