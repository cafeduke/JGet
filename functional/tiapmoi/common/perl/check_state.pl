#/usr/bin/perl
#
## Copyright (c) 2008, Oracle.  All rights reserved.  
#
#    NAME
#      check_state.pl
#
#    DESCRIPTION
#      Check the state of a given OHS instance
#      Usage:  perl check_state.pl <OHS_NAME> <expectedState>
#
#    MODIFIED   (MM/DD/YY)
#    kdclark     08/09/12 - Creation
# 

# Import required variables
$OHS_NAME        = $ARGV[0];
$goldState       = $ARGV[1];
$twork           = $ENV{T_WORK};
$ADMIN_HOST      = $ENV{ADMIN_HOST};
$ADMIN_PORT      = $ENV{ADMIN_PORT};
$WLS_USER        = $ENV{WLS_USER};
$WLS_PWD         = $ENV{WLS_PWD};
$WLST            = $ENV{WLST_LOC};
$INSTALL_TYPE    = $ENV{INSTALL_TYPE};
$DOMAIN_NAME     = $ENV{DOMAIN_NAME};

$script = $ENV{ADE_VIEW_ROOT} . "/apache/test/functional/common/wlst/";
if (($INSTALL_TYPE eq "full") || ($INSTALL_TYPE eq "compact")) {
  $script = $script . "ohs_get_state.py";
} else {
  $script = $script . "ohs_get_state_standalone.py";
} 

die "No OHS name provided!" unless $OHS_NAME;
die "No gold state provided!" unless $goldState;

print "Checking the state...\nExpecting instance: $OHS_NAME will be in state: $goldState\n";

# ----------------------------------------
# Run the WLST script to grab the ports

$OUTFILE = "$twork/state.tmp";
if (-e $OUTFILE) {
  $cmd = "rm $OUTFILE";
  system($cmd);
}

$cmd = $WLST . " $script"
  . " --ADMIN_HOST=$ADMIN_HOST"
  . " --ADMIN_PORT=$ADMIN_PORT"
  . " --WLS_USER=$WLS_USER"
  . " --WLS_PWD=$WLS_PWD"
  . " --OHS_NAME=$OHS_NAME"
  . " --DOMAIN_NAME=$DOMAIN_NAME"
  . " --OUTFILE=$OUTFILE";
system($cmd);

# ---------------------------------------
# Check that the returned state matches the goldState

$match = 0;

# Search the output log for the expected results
open(INFILE, $OUTFILE) || die "Unable to open $OUTFILE!";
while(<INFILE>) {

  # Special case -- on creation can be UNKNOWN or SHUTDOWN
  if (($goldState eq "UNKNOWN") && ($_ =~ /SHUTDOWN/)) {
      $match = 1;
  }
  if ($_ =~ /$goldState/) {
      $match = 1;
   }     
  $fileText = $fileText . $_;
}

if ($match == 0) {
  print $fileText . "\n\nERROR:  Was expecting instance to be in: $goldState\n";
  exit 1;
} else {
  print $fileText . "\n\nSuccess!\n";
}


