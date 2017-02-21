#!/usr/local/bin/perl
# 
# Copyright (c) 2008, Oracle.  All rights reserved.  
#
#    NAME
#      wlstRunScript.pl - <one-line expansion of the name>
#
#    DESCRIPTION
#      Run the provided WLST script.
#      Usage:  perl wlstRunScript.pl <script> <ohsName>
#
#    MODIFIED   (MM/DD/YY)
#    kdclark     10/10/14 - Update to 12.1.4
#    kdclark     09/27/12 - Creation
# 

$script  = $ARGV[0];
$ohsName = $ARGV[1];

if (!-e $script) { 
  print "ERROR:  Can't find $script!\n";
  exit 1;
}

# Set Variables
$ADMIN_HOST      = $ENV{ADMIN_HOST};
$ADMIN_PORT      = $ENV{ADMIN_PORT};
$ADMIN_SSL_PORT  = $ENV{ADMIN_SSL_PORT};
$WLS_USER        = $ENV{WLS_USER};
$WLS_PWD         = $ENV{WLS_PWD};
$wlst	 	 = $ENV{WLST_LOC};
$machine         = $ENV{MACHINE_NAME};

# Be sure WLST is set correctly
if (!-e $wlst) {
  print "ERROR:  Unable to locate $wlst.\nExiting...\n";
  exit 1;
}

# ----------------------
# Run WLST script
$cmd = $wlst . " $script"
       . " --ADMIN_HOST=$ADMIN_HOST" 
       . " --ADMIN_PORT=$ADMIN_PORT"
       . " --ADMIN_SSL_PORT=$ADMIN_SSL_PORT"
       . " --WLS_USER=$WLS_USER" 
       . " --WLS_PWD=$WLS_PWD"
       . " --OHS_NAME=$ohsName" 
       . " --MACHINE_NAME=$machine";

system($cmd);

