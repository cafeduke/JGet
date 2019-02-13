#!/usr/local/bin/perl
# 
# Copyright (c) 2008, Oracle.  All rights reserved.  
#
#    NAME
#      wlstCompareLogs.pl - <one-line expansion of the name>
#
#    DESCRIPTION
#      Helper script for tiapmoi scripting tests.
#      Compare two logfiles:  usually test output with a goldfile.
#      Usage:  wlstCompareLogs.pl <logFile> <goldFile>
#
#    MODIFIED   (MM/DD/YY)
#    kdclark     10/20/14 - convert to 12.2.1
#    kdclark     07/22/13 - Creation
# 

$twork    = $ENV{T_WORK};
$logFile  = $twork . "/" . $ARGV[0];
$goldFile = $twork . "/" . $ARGV[1];

if ((!$ARGV[0]) || (!$ARGV[1])) {
  print "ERROR:  Logfile or GoldFile not provided.\n";
  exit 1;
}

if (!-e $logFile) {
  print "ERROR:  Can't find logfile: $logFile\n";
  exit 1;
}
if (!-e $goldFile) {
  print "ERROR:  Can't find goldfile: $goldFile\n";
  exit 1;
}

# -------------------------------------
# Determine number of test cases

open(LOGS, $logFile) || die "Unable to open $logFile!";
while(<LOGS>) {
  if ($_ =~ /#### - BEGIN/) {
     ($junk, $caseNum) = split(/BEGIN /, $_);
  }
}
close(LOGS);

# -------------------------------------
# For each test case...

$failOut = 0;

foreach $i (1 .. $caseNum) {
  $searchFor = "#### - BEGIN " . $i;
  print "=================================\n";
  print "TestCase:  $i\n\n";
  
  # Grab test results
  $recording = 0;
  @output    = ();
  open(LOGS, $logFile) || die "Unable to open $logFile!";
  while(<LOGS>) {
    if ($_ =~ $searchFor) {
       $recording = 1;
       next;
    }
    if (($recording) && ($_ =~ /END/)) {
       last;
    }
    if ($recording) {
       push(@output, $_);
    }
  }
  close(LOGS);
 
  # Grab expected results from goldfile
  $recording = 0;
  @gold      = ();
  open(GOLD, $goldFile) || die "Unable to open $goldFile!";
  while(<GOLD>) {
    if ($_ =~ $searchFor) {
       $recording = 1;
       next;
    }
    if (($recording) && ($_ =~ /END/)) {
       last;
    }
    if ($recording) {
       push(@gold, $_);
    }
  }
  close(GOLD);

  # Verify test results match goldfile
  $match=0;
  $tstOutput = "";
  foreach $j (0 .. $#gold) {
    $found = 0;
    $tstOutput = $tstOutput . "Searching for:\n$gold[$j]\n";
    foreach $k (0 .. $#output) {
         $matchVal1 = $output[$k];  # actual output line
         $matchVal2 = $gold[$j];    # expected output
         chomp $matchVal1;
         chomp $matchVal2;
         #print "Looking for: $matchVal2\nIn Line: $matchVal1\n";
         if ($matchVal1 =~ /$matchVal2/) {
           $match = $match + 1;
           $found = 1;
           #print "*** FOUND IT! ***\n";
           last;
         }
    }  
    if ($found) {
       $tstOutput = $tstOutput . "FOUND!\n\n";
    } else {
       $tstOutput = $tstOutput . "NOT FOUND!\n\n";
    }
  }
  $tst = $i;
  if ($tst < 10) { $tst = "0" . $tst; }
  $matchTotal = $#gold + 1;
  if ($match == $matchTotal) {
     $tstOutput = $tstOutput . "Test case passed!\n";
     print $tstOutput;
  } else {
     $tstOutput = $tstOutput . "Test failed!\n";
     print $tstOutput;
     $failOut = 1;
  }
}

print "=================================\n";
print "=================================\n";
print "=================================\n";

print "Test summary:  ";
if ($failOut) {
  print "FAILED!\nOne or more test cases above failed.\n";
  exit 1;
} else {
  print "SUCCESS!\nAll test cases above passed successfully.\n";
}
