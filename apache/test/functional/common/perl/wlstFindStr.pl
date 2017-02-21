#!/usr/local/bin/perl
# wlstFindStr.pl
# 
# Copyright (c) 2008, Oracle.  All rights reserved.  
#
#    NAME
#      wlstFindStr.pl - <one-line expansion of the name>
#
#    DESCRIPTION
#      Look for a message or set of messages in a given logfile
#      Often used to check output from WLST for an expected message.
#      Usage:  perl wlstFindStr.pl <logfile> <goldfile>
#
#    MODIFIED   (MM/DD/YY)
#    kdclark     10/10/14 - Update to 12.1.4
#    kdclark     09/27/12 - Creation
# 

$logfile  = $ARGV[0];
$goldfile = $ARGV[1];

# Pull in the goldfile messages and reverse
$count = 0;
@messages = ();
open(MSG, $goldfile) || die "Can't open $goldfile!";
while(<MSG>) {
   chomp $_;
   push(@messages, $_);
   $count++;
}
close(MSG);
@messages = reverse(@messages);

print "count: ---$count---\n";

# Scan the logfile for errors or exceptions (case insensitive) 
$found = 0;
$fileText = "";
$val = pop(@messages);
open(INFILE, $logfile) || die "Can't open $logfile!";
while(<INFILE>) {
  print "Looking for: --$val--\n";
  print "Current candidate:  --$_--\n";
  if ($_ =~ /$val/) {
   print "*** FOUND MATCH ***\n";
    $found++;
    $fileText = $fileText . "Found:  $val\n";
    if ($found == $count) { last; }
    $val = pop(@messages);
  }
}
close(INFILE);

print "Logfile:  $logfile\nComparison File:  $goldfile\n\n" . $fileText;

if ($found != $count) {
   print "\nThe expected message set was not found in Logfile!\n";
   exit 1;
} else {
   print "\nThe expected message set was found!\n";
}

