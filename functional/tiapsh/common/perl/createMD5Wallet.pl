#!/usr/local/bin/perl
# createMD5Wallet.pl
# 
# Copyright (c) 2008, Oracle.  All rights reserved.  
#
#    NAME
#      createMD5Wallet.pl
#      Create a test wallet containing an MD5 certificate.
#
#    DESCRIPTION
#      Helper script for tiapsh_md5cert testing.
#
#    MODIFIED   (MM/DD/YY)
#    kdclark     10/19/16 - Creation
# 

$walletName      = $ARGV[0];
$ORACLE_HOME     = $ENV{ORACLE_HOME};
$twork           = $ENV{T_WORK};

# --------------------------------------------
# Error Checking

if (!$walletName) {
  print "ERROR:  Can't find wallet:  $walletName\n";
  exit 1;
}

# --------------------------------------------
# Create the test wallet

chdir $twork;
$oraCmd = "$ORACLE_HOME/oracle_common/bin/orapki";
$cmd = $oraCmd . " wallet create -wallet $walletName -auto_login_only";
print "Running:  $cmd\n";
system($cmd);

$cmd = $oraCmd . " wallet add -wallet " . $walletName
               . " -dn \"CN=test_cert,C=test_md5\""
               . " -keysize 2048 -sign_alg md5"
               . " -self_signed -validity 3650"
               . " -auto_login_only";
print "Running: $cmd\n";
system($cmd);


