#!/usr/local/bin/perl
# testClient.pl
# 
# Copyright (c) 2008, Oracle.  All rights reserved.  
#
#    NAME
#      testClient.pl
#      This script is designed to work with the openssl configServer.pl
#      script to test various SSL protocols/ciphers in testing NZ.
#      
#      usage:  perl testClient.pl <ohs> <protocol> <cipherFile>
#          - <ohs>        - OHS instance to fire against
#          - <protocol>   - protocol to use
#          - <cipherFile> - cipher file to use (standard or ECC)
#          - <translationFile> - translations file for openssl
#
#    DESCRIPTION
#      Helper script for openssl testing.
#
#    MODIFIED   (MM/DD/YY)
#    kdclark     02/04/15 - Creation
# 

$twork      = $ENV{T_WORK};
$ohsName    = $ARGV[0];
$protocol   = $ARGV[1];
$testName   = $ARGV[2];
$cipherFile = $ARGV[3];

# --------------------------------------------
# Account for OS specific features

$os = $ENV{'OS'};
if(!$os) {
   $os = "Linux";
}

# Set OPENSSL_CONF to avoid error message on windows 2012
# Config file is located in the same location as the executable
if ($os =~ /Windows/) {
  if (-e  $ENV{"OPENSSL_LOC"}."\\openssl.exe") {
          $myPath = $ENV{"OPENSSL_LOC"};
          print "Openssl loc from env variable:".$myPath."\n";
  } else {
	    
		$ENV{"OPENSSL_LOC"} = "c:\\openssl";
		if (! -e  $ENV{"OPENSSL_LOC"}."\\openssl.exe") {
				system("mkdir ".$ENV{"OPENSSL_LOC"});
			print "Openssl Path not found. downloading..\n";
			if (! -e  $ENV{"ADE_VIEW_ROOT"}."/bootstrap/retool/4.0.0/bin/re.bat") {
				print ("re.bat not found");
				return;
			}
			system("$ENV{ADE_VIEW_ROOT}/bootstrap/retool/4.0.0/bin/re.bat get \"com.oracle.ohs.windows_x64:openssl:1.0.2j-fips:zip\"");
			system("unzip openssl-1.0.2j-fips.zip -d ".$ENV{"OPENSSL_LOC"});
			print "completed";
		}
		$myPath  = $ENV{"OPENSSL_LOC"};
        print "new Openssl loc :".$myPath."\n";
  }
  
  $configSSL = $myPath . "\\openssl.cfg";
  if (!-e $configSSL) {
     $cmdPath = "touch $configSSL";
     system($cmdPath);
  }
  $ENV{'OPENSSL_CONF'} = $configSSL;
  $cmdPath   = $myPath . "\\openssl.exe";
  print "Win64:  Setting OPENSSL_CONF to: $ENV{'OPENSSL_CONF'}\n";

  $cmdPath   = $myPath . "\\openssl.exe";

} else {
  $cmdPath = "/usr/bin/openssl";
}

# --------------------------------------------
# Error Checking

if (!$ohsName) {
  print "ERROR:  No OHS instance was provided!\n";
  exit 1;
}
if (!$protocol) {
  print "ERROR:  Protocol was not provided.\n";
  exit 1;
}
if (!-e $cipherFile) {
  print "ERROR: Can't find cipher file:  $cipherFile\n";
  exit 1;
}

# Look in same directory as cipher file
($path, $junk) = split(/ciphers/, $cipherFile);
$transFile = $path . "translations.txt";
if (!-e $transFile) {
  print "ERROR:  Can't find $transFile\n";
  exit 1;
}

# Translate the protocol to openssl format:
PROT: {
    if ($protocol eq "SSLv3")   { $oprot = "ssl3"; last PROT; }
    if ($protocol eq "TLSv1")   { $oprot = "tls1"; last PROT; }
    if ($protocol eq "TLSv1.1") { $oprot = "tls1_1"; last PROT; }
    if ($protocol eq "TLSv1.2") { $oprot = "tls1_2"; last PROT; }
    if ($protocol eq "SSLv2")   { $oprot = "ssl2"; last PROT; }
    print "ERROR:  Unable to translate $protocol to openssl format.\n";
    exit 1;
}

$outputDir = $twork . "/tiapsh_openssl";

# --------------------------------------------
# If request file doesn't exist, create it...
# HOST refers to hostname testClient.pl is run from
# Need a blank line at the end of the file

$testHost = `hostname`;
chomp $testHost;
$testHost = $testHost . ".us.oracle.com";

$reqFile = $twork . "/tiapsh_req.txt";
if (!-e $reqFile) {
  print "Writing out request file:\n$reqFile\n";
  $outfile = ">" . $reqFile;
  open(OUTFILE, $outfile) || die "Unable to open $outfile!";
  print OUTFILE "GET /index.html HTTP/1.0\n";
  print OUTFILE "HOST: $testHost\n\n";
  close(OUTFILE);   
}

# --------------------------------------------
# Figure out what SSL port the OHS instance is running on

$portFile = $twork . "/ports-" . $ohsName . ".txt";

open(INFILE, $portFile) || die "Can't open $portFile";
while(<INFILE>) {
  if ($_ =~ /APACHE_SSL_PORT/) {
    ($junk, $sslPort) = split(/=/, $_);
  }
}
chomp $sslPort;

if (!$sslPort) {
  print "ERROR:  Unable to determine APACHE_SSL_PORT for $ohsName!\n";
  exit 1;
}

# --------------------------------------------
# How many cipher tests are we looking at?

$count = 0;
open(INFILE, $cipherFile) || die "Can't open $cipherFile";
while(<INFILE>) {
  if ($_ =~ /cipher/) { $count++; }
}
close(INFILE);

if ($count == 0) {
  print "Error:  Unable to find any tests in $cipherFile\n";
  exit 1;
}

# --------------------------------------------
# Display header info

print "============================================\n";
print "testClient.pl:\n";
print "Host:  $testHost" . ":" . $sslPort . "\n";
print "============================================\n";
print "Cipher File:\n$cipherFile\n";
print "Found: $count tests to run...\n";
print "============================================\n";

# --------------------------------------------
# Run the tests

foreach $i (1 .. $count) {

  print "============================================\n";
  print "TEST #" . $i ." of $count:\n\n";
 
  # Grab the next cipher
  $cMatch = $i . ".cipher";
  open(INFILE, $cipherFile) || die "Can't open $cipherFile";
  while(<INFILE>) {
    if ($_ =~ /$cMatch/) {
      ($junk, $cipher) = split(/=/, $_);
      last;
    }
  }
  close(INFILE);
  chomp $cipher;
 
  # ------------------- 
  # Translate to openssl format
  # openssl has its own valus for protocols and ciphers

  $ocip = "";
  open(INFILE, $transFile) || die "Can't open $transFile";
  while (<INFILE>) {
    if ($_ =~ /$cipher/) {
       ($junk, $ocip) = split(/=/, $_);
       last;
    }
  } 
  close(INFILE);
  chomp $ocip;
  if (!$ocip) {
    print "ERROR:  Unable to translate:  $cipher to openssl format.\n";
    exit 1;
  }
  print "Protocol: $protocol ==> $oprot\n";
  print "Cipher:   $cipher ==> $ocip\n\n";
 
  # -------------------------- 
  # Run the test
  # For info on openssl:  openssl s_client help
  # openssl is bundled automatically on OEL6.5, OEL7.

  if ($i < 10) {  
     $suffix = "0" . $i . ".out";
  } else {
     $suffix = "$i.out";
  }
  $rawDir = $outputDir . "/" . $testName;
  $rawLog = $rawDir . "/" . "test_" . $suffix;
  $cmd = $cmdPath . " s_client -connect $testHost:$sslPort < $reqFile -state -debug -cipher $ocip -$oprot";

  # Redirect appropriate to platform
  if ($os =~ /Windows/) {
    $cmd = $cmd . " > $rawLog 2>&1";
  } else {
    $cmd = $cmd . " >& $rawLog";
  }

  print "Running:\n$cmd\n\n";
  system($cmd);

  # Add the command to the raw output just for debugging usefulness
  $out = ">>" . $rawLog;
  open(OUTFILE, $out) || die "Can't open $out";
  print OUTFILE "\n================================================\n";
  print OUTFILE "Command used:\n";
  print OUTFILE $cmd . "\n";
  print OUTFILE "================================================\n";
  close(OUTFILE);

  # Search for errors in openssl log -
  # ignore self cert chain errors - these are okay
  $result = "PASS";
  open(INFILE, $rawLog) || die "Can't open $rawLog\n";
  while(<INFILE>) {
    if (($_ =~ /fail/) || ($_ =~ /error/)) {
       next if ($_ =~ /18\:self/);
       next if ($_ =~ /19\:self/);
       $result = "FAIL";
    } 
  }
  
  print "TEST RESULT." . $i . "=$result\n"; 

}

print "============================================\n";
