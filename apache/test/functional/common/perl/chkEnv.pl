#!/usr/local/bin/perl
#
# chkEnv.pl
# 
# Copyright (c) 2008, Oracle.  All rights reserved.  
#
#    NAME
#      chkEnv.pl - <one-line expansion of the name>
#
#    DESCRIPTION
#      Check T_WORK to verify that no difs were thrown by the DTE
#      topology.  If there are any, we have a suspect environment.
#
#    NOTES
#      To call:  perl chkEnv.pl
#
#    MODIFIED   (MM/DD/YY)
#    kdclark     09/10/14 - Update to 12.2.1
#    kdclark     07/09/12 - Creation

use File::Basename;
my $CURRENT_DIR = dirname(__FILE__);
require "$CURRENT_DIR/support.pl";

$platform = $ENV{'OSTYPE'};
if ($platform =~ /MSWin32/) { $platform = "nt"; }

# Set up platform specific stuff
if ($platform eq "nt") {
  $SD  = "\\";
} else {
  $SD  = "\/";
}

$twork           = $ENV{T_WORK};
$BADdif          = $twork . $SD . "BAD_ENVIRONMENT.dif";
$topoFile        = $twork . $SD . "DOWNLOAD_TEST1" . $SD . "tiastopo.cfg";
$fileText        = "";

# --------------------------------------
# Initial cleanup, if any

# Erase any old dif files
if (-e $BADdif) {
  $cmd = "rm $BADdif";
  system($cmd);
}

# ------------------------------
# Detect the topoID
$topoID = 0;
$jobfile = $twork . $SD . "DTEjob.properties";
open(INFILE, $jobfile) || die "Unable to open $jobfile";
while(<INFILE>) {
  if ($_ =~ /TopoID/) {
    ($junk, $topoID) = split(/=/, $_);
    chomp $topoID;
    last;
  }
}
close(INFILE);
if ($topoID == 0) {
   $fileText = "Unable to determine topoID from $jobfile\n";
   open(OUTFILE, ">$BADdif") || die "Unable to open $BADdif";
   print OUTFILE $fileText;
   close(OUTFILE);
   exit 0;
}

# ------------------------------
# Check T_WORK for difs

print "Capturing block names from:\n$topoFile\n\n";

# Capture blocks used in the topology
@blocks = ();
if (-e $topoFile) {
  open(INFILE, $topoFile) || die "Unable to open $topoFile!";
  while(<INFILE>) {
    if ($_ =~ /INSTALL_ID/) {
      next if ($_ =~ /OHS_MATS/);
      ($junk, $blockName) = split(/=/, $_);
      ($blockName, $junk) = split(/ \*\*\*/, $blockName);
      push(@blocks, $blockName);
      #print "Found block:  $blockName\n";
    }
  }  
  close(INFILE);
} else {
   $fileText = "FAILURE:  Unable to locate\n$topoFile\nPossibly a topology was not used to set up the environment?\n";
}

if (!$fileText) {
  # Did any of the blocks throw a dif?
  $count    = 0;
  chdir $twork;
  foreach $i (@blocks) {
    chomp $i;
    $val = $i . ".dif";
    print "Looking for:  $val";
    #print "$val\n";
    if (-e $val) {

      # Exception for 12.1.2 upgrade as it throws an 'acceptable' dif
      # Not sure why blocks are allowed to dif for acceptable results...???
      if (($topoID == 88712) && ($val eq "rcu_12src.dif")) {
         print " --> DIF FOUND! but... ignored per special case for 12.1.2 upgrade.\n";
         next;
      } else {

         # Capture dif info
         $count++;
         $fileText = $fileText . $val . "\n";
         print " --> DIF FOUND!\n";
      }
    } else {
      print " --> not found\n";
    }
  }

  # Write out suc/dif file
  if ($count > 0) {
     $fileText = "FAILURE:  $count DTE block dif(s) found in T_WORK!\n\n$fileText\n";
  } else {
     print "\nNo *.difs found in the topology.\n";
  }
}

# ------------------------------
# Write out BAD_ENVIRONMENT.dif if there's a problem...

if ($fileText) {
   $fileText = $fileText . "\nOne or more blocks in the DTE topology appear to have failed or are missing.  It is highly likely that your environment is suspect (e.g. the installation/configuration of WLS and/or OHS failed).  Please triage all *.dif files and/or missing blocks before attempting to re-run the tests.\n\n";
  print $fileText;

  open(OUTFILE, ">$BADdif") || die "Unable to open $BADdif";
  print OUTFILE $fileText;
  close(OUTFILE);
  
}

