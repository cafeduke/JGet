#/usr/bin/perl

#############################################################################
# Author: kdclark
# Creation:  09/22/08
#
# wlstErrorScan.pl
# Helper script used to scan the output from an WLST command and look for
# any errors/exceptions that may have occurred.
#
# Usage:  perl wlstErrorScan.pl <logfile>
#
############################################################################

$fileName = $ARGV[0];   
$fileText  = "";
$record = 0;

# Scan the logfile for errors or exceptions (case insensitive)
open(INFILE, $fileName) || die "Can't open $fileName!";
while(<INFILE>) {
  ########################
  # BUG - ignore lines from manageohs_wlst.  This is being triggered by
  # Some sort of environment setting... happens in automated tests, but 
  # not in reality. Remove when we know what causes this.
  ########################
  if (($_ =~ /Welcome/) || ($_ =~ /Starting WLS with line/)) { $record = 1; }
  if (!$record) { next; }
  ########################

  # Skip non-ssl warnings
  if (($_ =~ /warning/i) && ($_ =~ /insecure protocol/)) { next; }

  # Look for any of the following:
  if ( ($_ =~ /not reachable/i) ||
      ($_ =~ /failed/i) ) 
      {

       # We have a legit exception;  capture it
       $fileText  = $fileText . $_ . "\n";
     }
} 
close(INFILE);

# Return code 1 if any errors found
if ($fileText) {
   print "Errors or exceptions were found in $fileName!\n\n" . $fileText;
   exit 1;
} else { 
   print "Scanning $fileName and no errors were found!\n\n";
   exit 0;
}

