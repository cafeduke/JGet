#/usr/bin/perl

#############################################################################
# Author: kdclark
# Creation:  09/22/08
#
# compare_files.pl
# Take two files and run a diff on them.  
#
# Usage:  perl compare_files.pl <goldfile> <testfile>
#
############################################################################
use File::Copy;
require "$ENV{ADE_VIEW_ROOT}/apache/test/functional/common/perl/support.pl";

$goldfile	   = $ARGV[0];
$testfile          = $ARGV[1];
$outfile           = $ARGV[2];

if (!-e $goldfile) { 
  print "ERROR: Can't find $goldfile\n";
  exit 1;
}
if (!-e $testfile) {
  print "Error:  Can't find $testfile\n";
  exit 1;
}

$cmd = "diff $goldfile $testfile >> $outfile";
@diffs = system($cmd);

# Enhance the output to make debugging a little easier
open(RESULTS, "$outfile") || die "Can't open $outfile";
while(<RESULTS>) {
  if ($_ =~ /\</) { $fileText1 = $fileText1 . $_; }
  if ($_ =~ /\>/) { $fileText2 = $fileText2 . $_; } 
}
close(RESULTS);
print "=======================================================\n";
print "Comparing files:\n1) $goldfile\n2) $testfile\n\n";

# If we found any differences, throw an error...
if (($fileText1) || ($fileText2)) {

   print "Differences were found in comparing the expected file set (1) to the actual file set (2):\n\n";
  if ($fileText1) { 
    print "The following file(s) were expected, but not found:\n$fileText1\n";
  }
  if ($fileText2) {
    print "The following file(s) were NOT expected, but have appeared:\n$fileText2\n";
  }
  exit 1;
} else {
  print "Success!  All expected files were found!  No unexpected files found.\n";
}

