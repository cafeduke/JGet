# 
# Copyright (c) 2002, Oracle Corporation.  All rights reserved.  
#
#    NAME
#      tiapwt_bk_clean.pl
#
#    DESCRIPTION
#      Clean up all the requisite stuff for backend SSL.
#
#    MODIFIED   (MM/DD/YY)
#    kdclark    13/02/11 - Creation
# 

# Capture variables
$ORACLE_HOME = $ENV{ORACLE_HOME};
$DOMAIN_HOME = $ENV{DOMAIN_HOME};
$twork       = $ENV{T_WORK};
$JAVA_HOME   = $ENV{JAVA_HOME};
$ADMIN_HOST  = $ENV{ADMIN_HOST};
$ADMIN_PORT  = $ENV{ADMIN_PORT};
$ADMIN_SSL_PORT = $ENV{ADMIN_SSL_PORT};
$WLS_USER    = $ENV{WLS_USER};
$WLS_PWD     = $ENV{WLS_PWD};
$wlst        = $ENV{WLST_LOC};

# Set up platform specific stuff
$platform = $ENV{'OSTYPE'};
if ($platform =~ /MSWin32/) { $platform = "nt";}
if ($platform eq "nt") {
  $SD  = "\\";
} else {
  $SD  = "\/";
}

print "----------------------------\n";
print "tiapwt_bk_clean.pl\n\n";

# ------------------------------------
# Configure WLS console

$script = "$ENV{'ADE_VIEW_ROOT'}/apache/test/functional/tiapwt/common/scripts/tiapwt_bk_clean.py";

# Be sure WLST is set correctly
if (!-e $wlst) {
  print "ERROR:  Unable to locate $wlst.\nExiting...\n";
  exit 1;
}

print "----------------------------\n";
print "Starting WLST to update AdminServer:\n\n";

# Call the script to set up WLS
$cmd = $wlst . " $script"
       . " --ADMIN_HOST=$ADMIN_HOST"
       . " --ADMIN_PORT=$ADMIN_PORT"
       . " --ADMIN_SSL_PORT=$ADMIN_SSL_PORT"
       . " --WLS_USER=$WLS_USER"
       . " --WLS_PWD=$WLS_PWD";

print "$cmd\n\n";
system($cmd);

# ------------------------------------

