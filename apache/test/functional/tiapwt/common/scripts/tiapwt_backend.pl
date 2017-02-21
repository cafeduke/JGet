# 
# Copyright (c) 2002, Oracle Corporation.  All rights reserved.  
#
#    NAME
#      tiapwt_backend.pl
#
#    DESCRIPTION
#      Set up all the requisite stuff for backend SSL.
#      OHS <--https--> WLS.
#
#    MODIFIED   (MM/DD/YY)
#    kdclark    13/09/05 - Creation
# 

# Capture variables
$ORACLE_HOME = $ENV{ORACLE_HOME};
$DOMAIN_HOME = $ENV{DOMAIN_HOME};
$twork       = $ENV{T_WORK};
$JAVA_HOME   = $ENV{JAVA_HOME};
$ADMIN_HOST  = $ENV{ADMIN_HOST};
$ADMIN_PORT  = $ENV{ADMIN_PORT};
$ADMIN_SSL_PORT = $ENV{ADMIN_SSL_PORT};
$WLS_USER    = $ENV{WLS_USER};
$WLS_PWD     = $ENV{WLS_PWD};
$wlst        = $ENV{WLST_LOC};

# Set up platform specific stuff
$platform = $ENV{'OSTYPE'};
if ($platform =~ /MSWin32/) {$platform = "nt";}
if ($platform eq "nt") {
  $SD  = "\\";
} else {
  $SD  = "\/";
}

$keyTool     = $JAVA_HOME . "/bin/keytool";
$oraTool     = $ORACLE_HOME . "/oracle_common/bin/orapki";
$myJKS       = $twork . "/tiapwt_weblogic.jks";
$myCRT       = $twork . "/tiapwt_ohs.crt";
$pwd         = "welcome1";

# Have we run this previously?
if (-e $myJKS) {
   $cmd = "rm $myJKS";
   print "Found an older JKS file.  Removing it...\n$cmd\n";
   system($cmd);
} 
if (-e $myCRT) {
   $cmd = "rm $myCRT";
   print "Found an older CRT file.  Removing it...\n$cmd\n";
   system($cmd);
}


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

# -----------------------------------
# Export DemoTrust cert

$cmd = $keyTool . " -exportcert -rfc -alias wlscertgenca -storepass DemoTrustKeyStorePassPhrase"
	. " -file $twork/nmCert.crt"
    . " -keystore $ORACLE_HOME/wlserver/server/lib/DemoTrust.jks";
print "----------------------------\n";
print "Export nmCert\n\n";
print "$cmd\n\n";
system($cmd);

# -----------------------------------
# Import nmCert

$cmd = $keyTool . " -importcert -alias wlscertgenca"
    . " -file $twork/nmCert.crt"
    . " -keystore $JAVA_HOME/jre/lib/security/cacerts"
    . " -trustcacerts -storepass changeit -noprompt";
print "----------------------------\n";
print "Import nmCert\n\n";
print "$cmd\n\n";
system($cmd);



# ------------------------------------
# Import ohs.crt to OHS wallet1

$walletPath1 = $DOMAIN_HOME . "/config/fmwconfig/components/OHS/ohs1/keystores/default/cwallet.sso";

$cmd = $oraTool . " wallet add"
    . " -wallet $walletPath1"
    . " -trusted_cert"
    . " -cert $myCRT"
    . " -auto_login_only";
print "----------------------------\n";
print "Import ohs.crt to wallet1:\n\n";
print "$cmd\n\n";
system($cmd);

# ------------------------------------
# Import ohs.crt to OHS wallet2

$walletPath2 = $DOMAIN_HOME . "/config/fmwconfig/components/OHS/instances/ohs1/keystores/default/cwallet.sso";

$cmd = $oraTool . " wallet add"
    . " -wallet $walletPath2"
    . " -trusted_cert"
    . " -cert $myCRT"
    . " -auto_login_only";
print "----------------------------\n";
print "Import ohs.crt to wallet2:\n\n";
print "$cmd\n\n";
system($cmd);


# ------------------------------------
# Configure WLS console

$script = "$ENV{'ADE_VIEW_ROOT'}/apache/test/functional/tiapwt/common/scripts/tiapwt_backend.py";

# Be sure WLST is set correctly
if (!-e $wlst) {
  print "ERROR:  Unable to locate $wlst.\nExiting...\n";
  exit 1;
}

print "----------------------------\n";
print "Starting WLST to update AdminServer:\n\n";

# Set special path to try to avoid breaking Windows
# Need to use forward slashes in path passed to WLST
$platform = $ENV{'PLATFORM'};
if ($platform eq "nt") {
  @biglist = split(/\\/, $twork);
  $twork   = join('/', @biglist);
}
$JKS_PATH = $twork . "/tiapwt_weblogic.jks";

# Call the script to set up WLS
$cmd = $wlst . " $script"
       . " --JKS_PATH=$JKS_PATH"
       . " --ADMIN_HOST=$ADMIN_HOST"
       . " --ADMIN_PORT=$ADMIN_PORT"
       . " --ADMIN_SSL_PORT=$ADMIN_SSL_PORT"
       . " --WLS_USER=$WLS_USER"
       . " --WLS_PWD=$WLS_PWD";

print "$cmd\n\n";
system($cmd);

# ------------------------------------

