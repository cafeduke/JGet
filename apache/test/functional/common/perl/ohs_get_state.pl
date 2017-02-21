#!/usr/local/bin/perl
# Copyright (c) 2008, Oracle.  All rights reserved.  
#
#    NAME
#      ohs_get_state.pl - <one-line expansion of the name>
#
#    DESCRIPTION
#      Quickly capture the state of a given OHS instance
#      Grabs state info out of $DOMAIN_HOME/system_components
#      Usage:  ohs_get_state.pl -ohs <ohsName>
#     
#    NOTES
#      <other useful comments, qualifications, etc.>
#
#    MODIFIED   (MM/DD/YY)
#    kdclark     09/11/14 - update for 12.2.1
#    kdclark     09/24/08 - Creation
# 

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

# -------------------------------
# One can call state() or nmServerStatus() to get the state of a given
# instance, but for consistency between collocated and standalone and
# to avoid a time hit with wlst.sh we will grab the state info directly
# out of the DOMAIN_HOME/system_components/OHS area.

$STATE_DIR = $ENV{'DOMAIN_HOME'} . "/system_components/OHS/" . $OHS_NAME . "/data/nodemanager/";
$STATE_FILE = $STATE_DIR . $OHS_NAME . ".state";

# Verify the state file exists
if (!-e $STATE_FILE) {
   print "ERROR:  Unable to find:\n$STATE_FILE\n\n";
   print "Weird.  Check the domain home for errors.\nExiting...\n";  
   exit 1;
}

# Open & read the state file -- usually just one line
open(INFILE, "$STATE_FILE") || die "Unable to open $STATE_FILE";
while(<INFILE>) {
   ($ohsState, $junk1, $junk2) = split(/:/, $_);
}
close(INFILE);

print "=============================\n";
print "OHS Instance:  $OHS_NAME\n\n";
print "State=$ohsState\n";
print "=============================\n";

$ENV{'OHS_STATE'} = $ohsState;

