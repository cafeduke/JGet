#/usr/bin/perl
# 
# Copyright (c) 2008, Oracle.  All rights reserved.  
#
#    NAME
#      check_pids.pl
#
#    DESCRIPTION
#      Verify the number of procs running.
#      Usage:  perl check_pids.pl <pidName> <expectedOutput>
#      Ex:     perl check_pids.pl httpd.worker 3
#
#    MODIFIED   (MM/DD/YY)
#    kdclark     08/09/12 - Creation
# 

# Import required variables
$pidName         = $ARGV[0];
$goldState       = $ARGV[1];
$ohsName         = $ARGV[2];
$twork           = $ENV{T_WORK};
$os              = $ENV{'OS'};

die "No pid name provided!" unless $pidName;

# Linux   = OHS_MPM_WORKER
# Windows = ???
#if (($os =~ /Windows/) && ($pidName =~ /httpd/)) {
#   $pidName = "httpd";
#}

# ----------------------------------------
# Get the pid count

print "Looking for:  $pidName and expecting this many:  $goldState\n\n";

$cmd = "ps -ef | grep $pidName | grep $ohsName | grep -v grep | grep -v check_pids.pl";
@pids = `$cmd`;

$count = 0;
$pidTxt = "";
foreach $i (@pids) {
   if ($i =~ /$pidName/) {
     $count++;
     $pidTxt = $pidTxt . $i;
   }
}

# ---------------------------------------
# Check that the returned state matches the goldState

if ($count == $goldState) {
  print "$count pids found as expected!\n";
} else {
  print "ERROR:\nExpecting $goldState pid(s), but found $count.\n$pidTxt";
  exit 1;
}

