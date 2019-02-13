# 
# Copyright (c) 2002, Oracle Corporation.  All rights reserved.  
#
#    NAME
#      configureSSL.pl
#
#    DESCRIPTION
#      Generate an ohs.crt and OHS test wallet with self-signed certificate
#      on the fly and copy into T_WORK.  This will be used in testing SSL.
#
#      Usage:  perl configureSSL.pl -ohs <ohsName>
#
#    MODIFIED   (MM/DD/YY)
#    kdclark    09/24/14 - Creation
# 

use File::Basename;
my $CURRENT_DIR = dirname(__FILE__);
require "$CURRENT_DIR/support.pl";

$OHS_NAME = "";

# Pull in parameters
foreach $i (0 .. $#ARGV) {
  next if (!($ARGV[$i] =~ /-/));    # ignore non flags
  if ($ARGV[$i] =~ /-ohs/) {
    $OHS_NAME=$ARGV[$i+1]; 
  }
}
 
if (!$OHS_NAME) {
  print "Error:  Unable to determine OHS_NAME!\n";
  exit 1;
}

# Capture variables
$twork       = $ENV{T_WORK};
$JAVA_HOME   = $ENV{ORACLE_JAVA_HOME};
$ORACLE_HOME = $ENV{ORACLE_HOME};
$ohsConfDir  = $ENV{ohsConfDir};

$keyTool     = $JAVA_HOME . "/bin/keytool";
$cacerts     = $JAVA_HOME . "/lib/security/cacerts";
$oraTool     = $ORACLE_HOME . "/oracle_common/bin/orapki";
$myJKS       = $twork . "/sslJKS.jks";
$myCRT       = $twork . "/sslOHS.crt";
$pwd         = "welcome1";

print "----------------------------\n";
print " configureSSL.pl -ohs $OHS_NAME\n";

# The test framework will automatically remove myJKS.jks and myOHS.crt 
# each time it's kicked off.  If we alrady have a JKS and CRT file there's
# no need to do this again, we can import the same CRT to the wallet of
# the instance.

if (!-e $myCRT) {

  # ------------------------------------
  # Generate public & private keys and keystore

  chdir($twork);
  $cmd = $keyTool . " -genkeypair -keyalg RSA -validity 300 -alias sslcert"
   . " -dname \"cn=Beta Tester, ou=QA, o=Oracle, L=Portland, ST=Oregon, C=OR\""
   . " -keypass $pwd"
   . " -storepass $pwd"
   . " -keystore $myJKS";
  print "----------------------------\n";
  print "Generating keys an keystore:\n\n";
  print "$cmd\n\n";
  system($cmd);

  # ------------------------------------
  # Create self-signed cert
 
  $cmd = $keyTool . " -selfcert -keyalg RSA -alias sslcert"
      . " -storepass $pwd"
      . " -keystore $myJKS";
  print "----------------------------\n";
  print "Create self-signed cert:\n\n";
  print "$cmd\n\n";
  system($cmd);

  # ------------------------------------
  # Export self-signed cert to file

  $cmd = $keyTool . " -export -alias sslcert -rfc"
      . " -file $myCRT"
      . " -storepass $pwd"
      . " -keystore $myJKS";
  print "----------------------------\n";
  print "Export cert to file:\n\n";
  print "$cmd\n\n";
  system($cmd);

}

# ------------------------------------
# Import ohs.crt to OHS wallet

$walletPath  = $ohsConfDir . "/instances/" . $OHS_NAME
                           . "/keystores/default/cwallet.sso";
$walletPath1 = $walletPath . ".orig";
if (!-e $walletPath) {
  print "Cannot locate: $walletPath!\nExiting...\n";
  exit 1;
}

# Back up the wallet first
if (!-e $walletPath1) {
  $cmd = "cp $walletPath $walletPath1";
  system($cmd);
}

$cmd = $oraTool . " wallet add"
    . " -wallet $walletPath"
    . " -trusted_cert"
    . " -cert $myCRT"
    . " -auto_login_only";
print "----------------------------\n";
print "Import ohs.crt to wallet:\n\n";
print "$cmd\n\n";
system($cmd);


$cmd = $oraTool . " wallet display -wallet $walletPath";
print ($cmd);
system($cmd);

# ------------------------------------
# Export public key from OHS wallet

#$publicKey = $twork . "/b64cert.txt";
#$cmd = $oraTool . " wallet export"
#    . " -wallet $walletPath"
#    . " -dn 'CN=root_test,C=US'"
#    . " -cert $publicKey"
#    . " -alias ssltest";
#print "----------------------------\n";
#  print "Export public key from OHS wallet\n\n";
#print "$cmd\n\n";
#system($cmd);

# ------------------------------------
# Import the public key to cacerts used by ant

#$cmd = $keyTool . " -importcert"
#    . "  -trustcacerts"
#    . " -alias ssltest"
#    . " -file $publicKey"
#    . " -keystore $cacerts"
#    . " -storepass changeit";

$cmd = "chmod 777 $cacerts";
system($cmd);

$cmd = $keyTool . " -importcert"
    . "  -trustcacerts"
    . " -alias sslcert"
    . " -file $myCRT"
    . " -keystore $cacerts"
    . " -storepass changeit";
print "----------------------------\n";
print "Import public key to cacerts:\n\n";
print "$cmd\n\n";
system($cmd);

# ------------------------------------


