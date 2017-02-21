#!/usr/local/bin/perl
# chkResults
# 
# Copyright (c) 2008, Oracle.  All rights reserved.  
#
#    NAME
#      chkResults.pl
#      Compare output from the openssl commands with a goldfile
#      and product a logfile.  Fail out as necessary.
#      
#      usage:  perl chkResults.pl <testName> <goldFile>
#          - <testName>   - name of the test suite being run
#          - <goldFile> - goldfile to use
#
#    DESCRIPTION
#      Helper script for openssl testing.
#
#    MODIFIED   (MM/DD/YY)
#    kdclark     04/28/15 - Creation
# 

$twork      = $ENV{T_WORK};
$testName   = $ARGV[0];
$goldFile   = $ARGV[1];

$outFile = $twork . "/" . $testName . ".out";

# --------------------------------------------
# Error Checking

if (!-e $outFile) {
   print "ERROR:  Can't find $outFile...\n";
   exit 1;
}
if (!-e $goldFile) {
  print "ERROR: Can't find gold file:  $goldFile\n";
  exit 1;
}

# --------------------------------------------
# Put together header info

print "=========================================\n";
print "Test Suite:  $testName\n";
print "=========================================\n";
print "Results file:\n$outFile\n\nGoldFile:\n$goldFile\n";
print "=========================================\n\n";

# --------------------------------------------
# Check Results

$failed = 0;
open(INFILE1, $outFile) || die "Can't open $outFile!\n";
while(<INFILE1>) {

  # Special check to see if we were unable to connect
  if ($_ =~ /Connection refused/) {
     print "ERROR:  Connection refused.  Unable to connect!\n";
     exit 1;
  }
  # Ignore everything except results...
  if (!($_ =~ /RESULT/)) { next; }
 
  # Okay, we have a result...
  # Capture the testcase and actual result
  $line = $_;
  ($junk1, $junk2) = split(/\./, $line);
  ($testCase, $result) = split(/=/, $junk2);  
  chomp $result;

  # Scan the gold file for expected result 
  $expectedResult="";
  open(INFILE2, $goldFile) || die "Can't open goldfile: $goldFile";
  while(<INFILE2>) {
     $sMatch = $testCase . ".result";
     if ($_ =~ /$sMatch/) {
       ($junk, $expectedResult) = split(/=/, $_);
       chomp $expectedResult;
       last;
     }
  }
  
  # Throw an error if we found no expected results for testcase
  if (!$expectedResult) {
    print "ERROR:  Can't find result for testcase:  $testCase!\n";
    exit 1;
  }

  if ($testCase < 10) { $testCase = "0" . $testCase; }
  print "Test #" . $testCase . ": ";
  print "Expected:  $expectedResult <-> Actual:  $result";
  if ($result ne $expectedResult) {
    $failed = 1;
    print " ====> *** FAILED *** \n\n";
  } else {
    print " ====> Test passed.\n";
  }
}

# Trigger a failure for TestLogic
if ($failed) {
  print "\n\n*** FAILURE:  One or more of the tests failed! ***\n";
  exit 1;
} else {
  print "\n\nAll test cases passed successfully.\n";
  exit 0;
}


