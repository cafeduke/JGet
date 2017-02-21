#!/usr/local/bin/perl
# configServer.pl
# 
# Copyright (c) 2008, Oracle.  All rights reserved.  
#
#    NAME
#      configServer.pl
#      This script is designed to work with the openssl client script to
#      swiftly set up various OHS configurations for testing NZ.
#      
#      usage:  perl configServer.pl <ohsName> <configFile> <testNum> <ecc>
#         ohsName    - what OHS instance to use
#         configFile - which configuration file to use
#         testNum    - what test in the configuration file
#         ecc        - configure for ECC?
#         fips       - configure for FIPS?
#
#    DESCRIPTION
#      Helper script for openssl testing.
#
#    MODIFIED   (MM/DD/YY)
#    kdclark     02/03/15 - Creation
# 

$ohsName         = $ARGV[0];
$configFile      = $ARGV[1];
$testNum         = $ARGV[2];
$ecc             = $ARGV[3];
$fips            = $ARGV[4];
$twork           = $ENV{T_WORK};
$ORACLE_HOME     = $ENV{ORACLE_HOME};
$DOMAIN_HOME     = $ENV{DOMAIN_HOME};
$sslFile1        = $ENV{ohsConfDir} . "/" . $ohsName . "/ssl.conf";
$sslFile2        = $ENV{ohsConfDir} . "/instances/" . $ohsName . "/ssl.conf";
$ohsLogDir       = $ENV{ohsLogDir} . "/" . $ohsName . "/logs";

# --------------------------------------------
# Error Checking

$ohsName = "ohs1" unless $ohsName;
$ecc     = "0" unless $ecc;
$fips    = "0" unless $fips;

if (!-e $configFile) {
  print "ERROR:  Can't find configFile: $configFile!\n";
  exit 1;
}

if (!-e $sslFile1) {
  print "ERROR:  Can't find sslFile1:  $sslFile1!\n";
  exit 1;
} 
if (!-e $sslFile2) {
  print "ERROR:  Can't find sslFile2:  $sslFile2!\n";
  exit 1;
} 

# --------------------------------------------
# Create our ECC wallet if we need to

if ($ecc == 1) {
  $eccWallet = $twork. "/ecc";
  if (!-d $eccWallet) {
    print "ECC wallet not found.  Creating one. This may take a minute...\n";
    chdir $twork;
    $oraCmd = "$ORACLE_HOME/oracle_common/bin/orapki";
    $cmd = $oraCmd . " wallet create -wallet ecc -auto_login_only";
    print "Running:  $cmd\n";
    system($cmd);

    $host = `hostname`;
    chomp $host;
    $host = $host . ".us.oracle.com";
    $cmd = $oraCmd . " wallet add -wallet ecc"
                 . " -dn \"CN=" . $host . "\""
                 . " -keysize 1024 -asym_alg ECC -eccurve p256"
                 . " -self_signed -validity 3650"
                 . " -auto_login_only -jsafe";
    print "Running: $cmd\n";
    system($cmd);
  }
}

# --------------------------------------------
# Print Header
# Note that ssl.conf is already backed up by tiapsh

print "============================================\n";
print "Configuring ssl.conf for $ohsName ...\n";
print "============================================\n";
print "ECC:  ";
if ($ecc == 1) { print "Yes\n"; } else { print "No\n"; }
print "FIPS: ";
if ($fips == 1) { print "Yes\n"; } else { print "No\n"; }
print "Config File:\n$configFile\n\n";

# --------------------------------------------
# Capture new SSL settings

print "TEST CASE #" . $testNum . ":\n\n";
$count = 0;
open(INFILE, $configFile) || die "Can't open $configFile";
while(<INFILE>) {
    if ($_ =~ /$testNum\.SSLProtocol/) {
      ($junk, $protocol) = split(/=/, $_);
      $count++;
      next;
    }
    if ($_ =~ /$testNum\.SSLCipherSuite/) {
      ($junk, $suite) = split(/=/, $_);
      $count++;
    }
    last if ($count == 2);
}
close(INFILE);

if ($count != 2) {
  print "ERROR:  Can't find SSL configuration for test:  $testNum";
  exit 1;
}
chomp $protocol;
chomp $suite;

# --------------------------------------------
# Scan the ssl.conf file and replace directives
# Tried to use sed here, but doesn't work on windows -- might be
# better to come up with a more elegant solution here...
# Update SSLProtocol and SSLCipherSuite unless "default"
# For ECC, also update SSLWallet
# For FIPS, also add: SSLFIPS ON
  
$newContent = "";
open(INFILE, $sslFile1) || die "Can't open $sslFile1";
while(<INFILE>) {
   $line = $_;
   if (($line =~ /SSLProtocol/) && ($protocol ne "default")) {
      $line = "SSLProtocol $protocol\n";
   }   
   if (($line =~ /SSLCipherSuite/) && ($suite ne "default")) {
     $line = "SSLCipherSuite $suite\n";
   }
   # Look for SSL Global Context and add SSLFIPS after if relevant   
   # SSLFIPS cannot be set inside a VirtualHost
   if (($fips == 1) && ($line =~ /SSL Global Context/)) {
      $line = $line . "SSLFIPS ON\n";
   }
   if (($ecc == 1) && ($line =~ /SSLWallet/)) {
      $line = "SSLWallet $eccWallet\n";
   }
   $newContent = $newContent . $line;
}
close(INFILE);

if ($protocol eq "default") { $protocol = "<keep default>"; }
if ($suite    eq "default") { $suite    = "<keep default>"; }

# Write out the new ssl.conf files
$file = ">" . $sslFile1;
open(OUTFILE, $file) || die "Can't open $file";
print OUTFILE $newContent;
close(OUTFILE);

$file = ">" . $sslFile2;
open(OUTFILE, $file) || die "Can't open $file";
print OUTFILE $newContent;
close(OUTFILE);

# Display what we're doing to the user 
print "SSLProtocol $protocol\n";
print "SSLCipherSuite $suite\n";
if ($ecc == 1) {
  print "SSLWallet $eccWallet\n";
}
if ($fips == 1) {
  print "SSLFIPS ON\n";
}
print "\n";

print "============================================\n";
print "Configuration completed.\n";
print "============================================\n";
