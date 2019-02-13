#
# tiapsm.pl
#
# Utility script for the OHS smoke tests.
# Call:  tiapsm_cp.pl smoke.html ohs1
#
# Copyright (c) 2001, 2006, Oracle. All rights reserved.  
#

use File::Copy;

# Include a little support
require "$ENV{ADE_VIEW_ROOT}/apache/test/functional/common/perl/support.pl";

$action    = $ARGV[0];
$OHS_NAME  = $ARGV[1];
$fileName  = $ARGV[2];

$INSTALL_TYPE = $ENV{INSTALL_TYPE};
$twork        = $ENV{T_WORK};
$ohsConfDir   = $ENV{ohsConfDir};
$ohsLogDir    = $ENV{ohsLogDir};
$nmLogDir     = $ENV{nmLogDir};

$smokeDif  = $twork . "/SMOKE_FAIL.dif";
$configMaster  = $ohsConfDir . "/" . $OHS_NAME;
$configRuntime = $ohsConfDir . "/instances/" . $OHS_NAME;
$ohsLogDir     = $ohsLogDir  . "/" . $OHS_NAME;
$nmLogDir      = $nmLogDir   . "/" . $OHS_NAME;

###########################################
# Action:  chkCreate
###########################################
# Usage:  perl tiapsm.pl chkCreate <OHS_NAME>
# Quick check to ensure provisioning took place upon OHS instance creation.
# If provisioning did not happen as expected, throw a SMOKE_FAIL.dif.

if ($action eq "chkCreate") {
  $file1 = $configMaster  . "/httpd.conf";
  $file2 = $configRuntime . "/httpd.conf";
 
  # Check if creation took place succesfully
  if ((-e $file1) && (-e $file2)) {
    print "Found:\n$file1\n$file2\n\n";
    print "The OHS instance: $OHS_NAME appears to have been created successfully!\n\n";

  } else {
    $fileText = "ERROR:  Creation of OHS instance: $OHS_NAME does not appear successful!\n\n";
    if (!-e $file1) { 
      $fileText = $fileText . "Unable to locate: $file1\n"; 
    } 
    if (!-e $file2) {
      $fileText = $fileText . "Unable to locate: $file2\n";
    }
    &smokeFail($fileText);
  }

  exit 0;
}

###########################################
# Action:  chkRemove
###########################################
# Usage:  perl tiapsm.pl chkRemove <OHS_NAME>
# Check that instance deletion was successful.  If the removal was not
# successful, throw a SMOKE_FAIL.dif.

if ($action eq "chkRemove") {
  $dir1 = $configMaster;
  $dir2 = $configRuntime;
  $dir3 = $ohsLogDir;
  $dir4 = $nmLogDir;

  # Was instance removed successfully?
  if ((-d $dir1) || (-d $dir2) || (-d $dir3) || (-d $dir4)) {
    $fileText = "ERROR:  Removal of OHS instance: $OHS_NAME does not appear successful!\n\n";
    if (-d $dir1) { 
      $fileText = $fileText . "The OHS master configuration files do not appear to have been deleted.\n$dir1 still exists.\n";
    }
    if (-d $dir2) {
      $fileText = $fileText . "The OHS runtime configuration files do not appear to have been removed.\n$dir2 still exists.\n";
    }
    if (-d $dir3) {
      $fileText = $fileText . "The OHS logs do not appear to have been removed.\n$dir3 still exists.\n";
    }
    if (-d $dir4) {
      $fileText = $fileText . "The NM information for $OHS_NAME does not appear to have been removed.\n$dir4 still exists.\n";
    }
    &smokeFail($fileText);   
  } else {
    print "The OHS instance: $OHS_NAME appears to have been removed successfully!\n\n";
  }

  exit 0;
}

################################################
# IF PASSED ARGUMENT is "scanLog"
################################################
# Scan a given logfile to see if there are any Exception messages
# If so throw a SMOKE_FAIL.dif.  Usually used after a WLST operation
# to see if any error messages were returned.
# Usage:  perl tiapsm.pl scanLog <bogusOHS> <fileName>

if ($action eq "scanLog") {
   
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
     if ($_ =~ /Welcome to WebLogic/) { $record = 1; }
     if (!$record) { next; }

     ########################

     # Ignore non-SSL warnings - this is more of an info message
     if (($_ =~ /warning/i) && ($_ =~ /insecure protocol/)) { next; }

     # Look for any of the following:
     if (($_ =~ /exception/i) || 
         ($_ =~ /error/i) ||
         ($_ =~ /not reachable/i) ||
         ($_ =~ /failed/i) || 
         ($_ =~ /warning/i) ||
         ($_ =~ /NullPointer/i) ||
         ($_ =~ /Problem/i) ||
         ($_ =~ /already exists/i)) {

       # We have a legit exception;  capture it
       $fileText  = $fileText . $_ . "\n";
     }
   } 
   close(INFILE);

   # Throw a dif if any errors were found...
   if ($fileText) {
     $fileText = "Errors or exceptions were found in $fileName!\n\n" . $fileText;
   &smokeFail($fileText);
   } else { 
     print "Scanning $fileName and no errors were found!\n\n";
   }

   exit 0;
}

###########################################
# Otherwise, exit...
###########################################
print "Invalid argument: $action passed to tiapsm.pl.  Exiting...\n";
exit 1;


###########################################
# Generic routine to throw a SMOKE_FAIL.dif

sub smokeFail {
  local($msg) = @_;
 
  print $msg . "\n";  
  open(OUTFILE, ">$smokeDif") || die "Unable to open $smokeFail";
  print OUTFILE $msg . "\n\n";
  print OUTFILE $generic;
  close(OUTFILE);
}

