#!/usr/local/bin/perl
# 
# getEnv.pl
# 
# Copyright (c) 2008, Oracle.  All rights reserved.  
#
#    DESCRIPTION
#      Pick up env information from the topology.  Throw a
#      BAD_ENVIRONMENT.dif if any expected vars are not being
#      set properly.  Also set all required environment variables.
#
#    NOTES
#      To call:  perl getEnv.pl
#
#    MODIFIED   (MM/DD/YY)
#       kdclark  06/30/16 - improve automation
#       kdclark  04/12/16 - add support for upgrade topologies
#       kdclark  09/10/14 - update to 12.2.1
#       kdclark  04/18/13 - reorg & use tiastopo.cfg
#       kdclark  03/08/13 - add standalone OHS support
#       kdclark  06/13/12 - update for 12c topology
#       kdclark  07/13/11 - add support for EM-only runs
#       kdclark  02/22/11 - update to match PS4 topology
#       kdclark  02/18/10 - update to match PS2 topology
#       kdclark  11/12/09 - add WLS SSL - improve var support
#       kdclark  11/09/09 - fix MW_HOME for windows
#       kdclark  10/12/09 - Update to match topo update
#       kdclark  05/11/09 - Fix to support new EM topology
#       cbroadaw 03/27/09 - Fix wl.pwd generation
#       cbroadaw 02/10/09 - Add WL admin password file
#       kdclark  09/11/08 Creation

use File::Basename;
my $CURRENT_DIR = dirname(__FILE__);
require "$CURRENT_DIR/support.pl";

# Account for OS dependent features
$os = $ENV{'OSTYPE'};
if ($os =~ /MSWin32/) {
  $os = "Windows";
  $SD = "\/";
  $SP = "\;";

} else {
  $os = "Linux";
  $SD = "\/";
  $SP = "\:";
}

# Input Files
$twork        = $ENV{T_WORK};
$confFile     = $ENV{ADE_VIEW_ROOT} . $SD . "ascore" . $SD . "buildConfig.txt";
$jobFile      = $twork . $SD . "DTEjob.properties";
$downFile     = $twork . $SD . "DOWNLOAD_TEST1" . $SD . "tiastopo.cfg";

# Output Files
$varFile      = $twork . $SD . "ohsVars.txt";
$outfile      = $twork . $SD . "myVars.sh";
$pwdfile      = $twork . $SD . "wl.pwd";
$BADdif       = $twork . $SD . "BAD_ENVIRONMENT.dif";

# Exit immediately if this is a suspect environment...
if (-e $BADdif) {
  print "ERROR:  BAD_ENVIRONMENT.dif detected in T_WORK.\nThis environment is suspect.  Exiting...\n";
  exit 1;
}

# ---------------------------------------------
# Okay, go look for ohsVars.txt.  If it doesn't exist, we need to create it.
# We need:  MW_HOME, DOMAIN_HOME, HOSTNAME, WLS_USER, WLS_PWD
# All but standalone:  ADMIN_PORT, ADMIN_SSL_PORT
# Also topo ID so we can determine install type

if (!-e $varFile) {
  $fileText = "Unable to locate:  $varFile\n";

  # Does DOWNLOAD_TEST tiastopo.cfg exist?
  if (!-e $downFile) {
    $fileText = $fileText . "Attempted to generate it automatically from the output of the DTE topology, but testware can't find:  $downFile\n";
    $fileText = $fileText . "Either check the output of the topology or run:\nperl $ADE_VIEW_ROOT/ascore/setEnv.pl to generate ohsVars.txt.\n";
    open(OUTFILE, ">$BADdif") || die "Unable to open $BADdif";
    print OUTFILE $fileText;
    close(OUTFILE);
    exit 1;
  }

  # Does DTEjob.properties exist?
  $topoID = 0;
  open(INFILE, $jobFile) || die "Unable to open $jobFile";
  while(<INFILE>) {
    if ($_ =~ /TopoID/) {
      ($junk, $topoID) = split(/=/, $_);
      chomp $topoID;
      last;
    }
  }
  close(INFILE);
  if ($topoID == 0) {
    $fileText = "Unable to determine topoID from $jobFile\n";
    open(OUTFILE, ">$BADdif") || die "Unable to open $BADdif";
    print OUTFILE $fileText;
    close(OUTFILE);
    exit 1;
  }

  # Find out what the install type is
  $INSTALL_TYPE = "";
  $lookAfter    = "";
  open(INFILE, $confFile) || die "Cannot find $confFile";
  while(<INFILE>) {
    if ($_ =~ /\#/) { next; }
    if ($_ =~ /$topoID/) {
      ($topoName, $junk) = split(/=/, $_);
      last;
    }
  } 
  close(INFILE);
  if ($topoName eq "FULL_COLLOCATED") { $topoName = "full"; }
  if ($topoName eq "COMP_COLLOCATED") { $topoName = "compact"; }
  if ($topoName eq "REST_COLLOCATED") { $topoName = "restricted"; }
  if ($topoName eq "STANDALONE")      { $topoName = "standalone"; }

  if ($topoName eq "standalone") { 
    $lookAfter = "OHS_INST";
  } else {
    $lookAfter = "FMW_OHS_CONFIG";
  }
  $INSTALL_TYPE = $topoName;
 
  # If we didn't find a match...
  if (!$topoName) { 
    $fileText = "Can't find a match for $topoID in $confFile.\n";
    open(OUTFILE, ">$BADdif") || die "Unable to open $BADdif";
    print OUTFILE $fileText;
    close(OUTFILE);
    exit 1;
  }

  # Open tiastopo.cfg for reading...
  open (INFILE, $downFile)      || die "Unable to open $downFile";
  $record = 0;
  $found = "";
  while (<INFILE>) {
    $line = $_;

    # Ignore anything before lookAfter -- otherwise we get tripped up by
    # the database ORACLE_HOME here which is different.
    if ($record == 0) {
      if ($line =~ /$lookAfter/) {
         $record = 1;
      }
      next;
    }

   
    if ($line =~ /COMMON_ORACLE_HOME/) { next; }
    # Capture ORACLE_HOME
    # Bug in standalone topology:  MIDDLEWARE_HOME not set correctly in
    # FMW_OHS_INST1 block but ORACLE_HOME is set correctly
    if ($line =~ /COMMON_ORACLE_HOME/) { next; }
    if (($line =~ /ORACLE_HOME/) && (!($found =~ /ORACLE_HOME/))) {
      $found = "ORACLE_HOME " . $found;
      ($var, $ORACLE_HOME) = split("\=", $line);
      chomp($ORACLE_HOME);
      $MW_HOME = $ORACLE_HOME;
      $found = "MW_HOME " . $found;
      next;
    }

    # Capture MW_HOME
    #if ((($line =~ /MW_HOME/) || ($line =~ /MIDDLEWARE_HOME/)) && 
    #    (!($found =~ /MW_HOME/))) {
    #  $found = "MW_HOME " . $found;
    #  ($var, $MW_HOME) = split("\=", $line);
    #  chomp($MW_HOME);
    #  $ORACLE_HOME = $MW_HOME;
    #  $found = "ORACLE_HOME " . $found;
    #  next;
    #}

    # Capture DOMAIN_HOME
    if ($line =~ /SOA_DOMAIN_HOME/) { next; }
    if (($line =~ /DOMAIN_HOME/) && (!($found =~ /DOMAIN_HOME/))) {
       $found = "DOMAIN_HOME " . $found;
       ($var, $DOMAIN_HOME) = split("\=", $line);
       chomp($DOMAIN_HOME);
       next;
    }
 
    # Capture the hostname
    if (($line =~ /HOSTNAME/) && (!($found =~ /HOSTNAME/)))  {
       $found = "HOSTNAME " . $found;
       ($var, $ADMIN_HOST) = split("\=", $line);
       chomp($ADMIN_HOST);
       next;
    }

    # Capture the WLS username - usually "weblogic"
    if (($line =~ /WLS_USER/) && (!($found =~ /WLS_USER/))) {
       $found = "WLS_USER " . $found;
       ($var, $WLS_USER) = split("\=", $line);
       chomp($WLS_USER);
       next;
    }   

    # Capture the WLS password
    if (($line =~ /WLS_PWD/) && (!($found =~ /WLS_PWD/))) {
       $found = "WLS_PWD " . $found;
       ($var, $WLS_PWD) = split("\=", $line);
       chomp($WLS_PWD);      
       next;
    }
 
    # The remainder are all collocated-only variables
    if ($INSTALL_TYPE eq "standalone") { next; }
 
    # Capture the WLS port for http requests
    if ((($line =~ /WLS_CONSOLE_PORT/) || ($line =~ /WLS_PORT/)) &&
        (!($found =~ /ADMIN_PORT/))) {
       $found = "ADMIN_PORT " . $found;
       ($var, $ADMIN_PORT) = split("\=", $line);
       chomp($ADMIN_PORT);
       next;
    }
 
    # Capture the WLS port for https requests
    if (($line =~ /WLS_CONSOLE_SSLPORT/) &&
        (!($found =~ /ADMIN_SSL_PORT/))) {
       $found = "ADMIN_SSL_PORT " . $found;
       ($var, $ADMIN_SSL_PORT) = split("\=", $line);
       chomp($ADMIN_SSL_PORT);
       next;
    }
  }
  close(INFILE);

  # Not set in standalone topo file - we'll use defaults
  if ($INSTALL_TYPE eq "standalone") {
    $WLS_USER = "weblogic";
    $WLS_PWD  = "welcome1";
  }

  # Finally... create the ohsVars.txt file
  $fileText = "TopoID=$topoID\n"
            . "INSTALL_TYPE=$INSTALL_TYPE\n"
            . "MW_HOME=$MW_HOME\n"
            . "ORACLE_HOME=$ORACLE_HOME\n"
            . "DOMAIN_HOME=$DOMAIN_HOME\n"
            . "ADMIN_HOST=$ADMIN_HOST\n"
            . "WLS_USER=$WLS_USER\n"
            . "WLS_PWD=$WLS_PWD\n";
  if ($INSTALL_TYPE ne "standalone") {
     $fileText = $fileText
            . "ADMIN_PORT=$ADMIN_PORT\n"
            . "ADMIN_SSL_PORT=$ADMIN_SSL_PORT\n";
  }
  open(OUTFILE, ">$varFile") || die "Unable to open $varFile";
  print OUTFILE $fileText;
  close(OUTFILE);

  # Make sure we found all the required information...
  $fileText = "";
  if (!$MW_HOME) { 
     $fileText = $fileText . "Unable to find $MW_HOME.\n";
  } 
  if (!$ORACLE_HOME) {
     $fileText = $fileText . "Unable to find ORACLE_HOME.\n";
  }
  if (!$DOMAIN_HOME) {
     $fileText = $fileText . "Unable to find DOMAIN_HOME.\n";
  }
  if (!$ADMIN_HOST) {
     $fileText = $fileText . "Unable to find ADMIN_HOST.\n";
  }
  if (!$WLS_USER) {
     $fileText = $fileText . "Unable to find WLS_USER.\n";
  }
  if (!$WLS_PWD) {
     $fileText = $fileText . "Unable to find WLS_PWD.\n";
  }
  if (($INSTALL_TYPE ne "standalone") && (!$ADMIN_PORT)) {
     $fileText = $fileText . "Unable to find ADMIN_PORT.\n";
  }
  if (($INSTALL_TYPE ne "standalone") && (!$ADMIN_SSL_PORT)) {
     $fileText = $fileText . "Unable to find ADMIN_SSL_PORT.\n";
  }
  # If there's anything in fileText, then we failed to find something...
  if ($fileText) {
    $fileText = "Searching:  $downFile\n\n" . $fileText;
    open(OUTFILE, ">$BADdif") || die "Unable to open $BADdif";
    print OUTFILE $fileText;
    close(OUTFILE);
    exit 1;
  }

}

# ===========================================================
# Okay, we have an ohsVars.txt that contains all the test environment
# info we need.  Read it back in and do some processing.

open(INFILE, $varFile) || die "Unable to open $varFile";
while(<INFILE>) {
    if ($_ =~ /TopoID/) { 
      ($junk, $topoID) = split(/=/, $_);  chomp $topoID;
      next;
    }
    if ($_ =~ /INSTALL_TYPE/) {
      ($junk, $INSTALL_TYPE) = split(/=/, $_);  chomp $INSTALL_TYPE;
      next;
    }
    if ($_ =~ /MW_HOME/) {
      ($junk, $MW_HOME) = split(/=/, $_);  chomp $MW_HOME;
      next;
    }
    if ($_ =~ /ORACLE_HOME/) {
      ($junk, $ORACLE_HOME) = split(/=/, $_);  chomp $ORACLE_HOME;
      next;
    }
    if ($_ =~ /DOMAIN_HOME/) {
      ($junk, $DOMAIN_HOME) = split(/=/, $_);  chomp $DOMAIN_HOME;
      next;
    }
    if ($_ =~ /ADMIN_HOST/) {
      ($junk, $ADMIN_HOST) = split(/=/, $_); chomp $ADMIN_HOST;
      next;
    }
    if ($_ =~ /WLS_USER/) {
      ($junk, $WLS_USER) = split(/=/, $_); chomp $WLS_USER;
      next;
    }
    if ($_ =~ /WLS_PWD/) {
      ($junk, $WLS_PWD) = split(/=/, $_);  chomp $WLS_PWD;
      next;
    }
    if ($_ =~ /ADMIN_PORT/) {
      ($junk, $ADMIN_PORT) = split(/=/, $_); chomp $ADMIN_PORT;
      next;
    }
    if ($_ =~ /ADMIN_SSL_PORT/) {
      ($junk, $ADMIN_SSL_PORT) = split(/=/, $_); chomp $ADMIN_SSL_PORT;
      next;
    }
}
close(INFILE);

# -----------------------------
# Okay, set up the rest of the stuff
$WL_HOME         = $MW_HOME . $SD . "wlserver";
$OHS_PERL_BIN    = $MW_HOME . $SD . "perl" . $SD . "bin" . $SD . "perl";
$ORACLE_INSTANCE = $DOMAIN_HOME;

# Figure out the location of WLST for OHS
if ($os eq "Linux") {
  $wlstName = "wlst.sh";
} else {
  $wlstName = "wlst.cmd";
}
$WLST_LOC = $MW_HOME . $SD . "oracle_common" . $SD . "common" . $SD . "bin" . $SD . $wlstName;

# Capture the DOMAIN_NAME
($junk, $DOMAIN_NAME) = split(/domains/, $DOMAIN_HOME);
$DOMAIN_NAME = substr($DOMAIN_NAME, 1);

# Set machine name if not already set
if ($INSTALL_TYPE ne "standalone") {
  $MACHINE_NAME = $ADMIN_HOST unless $MACHINE_NAME;
}

# -----------------------------
# Set up WLS password file in $T_WORK
# All install types

open (PWDFILE, ">$pwdfile") || die "Unable to open $pwdfile";
print PWDFILE $WLS_PWD;
close(PWDFILE);
$WL_PASSWORD_FILE=$pwdfile;

# ------------------------------
# Add in any additional information we need
# Path setup may be different in ant - also this is test-specific

# Set WL_DEPLOY_CLASSPATH
$WL_DEPLOY_CLASSPATH = $ORACLE_HOME . $SD . "wlserver" . $SD . "server" . $SD . "lib" . $SD . "weblogic.jar" . $SP . $ORACLE_HOME . $SD . "oracle_common" . $SD . "modules" . $SD . "net.sf.antcontrib_1.1.0.0_1-0b3" . $SD . "lib" . $SD . "ant-contrib.jar";

# Include ORACLE_JAVA_HOME
$ORACLE_JAVA_HOME = $ORACLE_HOME . $SD . "oracle_common" . $SD . "jdk" . $SD . "jre";

# Other paths that may be useful in testing
$ohsConfDir = $DOMAIN_HOME . $SD . "config" . $SD . "fmwconfig" . $SD . "components" . $SD . "OHS";
$wlsLogDir  = $DOMAIN_HOME . $SD . "servers" . $SD . "AdminServer" . $SD . "logs";
$nmLogDir   = $DOMAIN_HOME . $SD . "system_components" . $SD . "OHS";
$ohsLogDir  = $DOMAIN_HOME . $SD . "servers";

# ------------------------------
# Print values as properties

open (OUTFILE, ">$outfile") || die "Unable to open $outfile";

# Generic environment variables
&printOut("TopoID", $topoID);
&printOut("INSTALL_TYPE", $INSTALL_TYPE);

print "\n";
print OUTFILE "\n";

&printOut("MW_HOME", $MW_HOME);
&printOut("ORACLE_HOME", $ORACLE_HOME);
&printOut("WL_HOME", $WL_HOME);
&printOut("WLST_LOC", $WLST_LOC);
&printOut("ORACLE_INSTANCE", $ORACLE_INSTANCE);
&printOut("DOMAIN_HOME", $DOMAIN_HOME);
&printOut("DOMAIN_NAME", $DOMAIN_NAME);
&printOut("WLS_USER", $WLS_USER);
&printOut("WLS_PWD", $WLS_PWD);
&printOut("WL_PASSWORD_FILE", $WL_PASSWORD_FILE);
&printOut("ADMIN_HOST", $ADMIN_HOST);

# Collocated only
if ($INSTALL_TYPE ne "standalone") {
  &printOut("ADMIN_PORT", $ADMIN_PORT);
  &printOut("ADMIN_SSL_PORT", $ADMIN_SSL_PORT);
  &printOut("MACHINE_NAME", $MACHINE_NAME);
}

# Put some space in output files
print "\n";
print OUTFILE "\n";

# Useful paths for testing
&printOut("ohsConfDir", $ohsConfDir);
&printOut("wlsLogDir", $wlsLogDir);
&printOut("nmLogDir", $nmLogDir);
&printOut("ohsLogDir", $ohsLogDir);
&printOut("OHS_PERL_BIN", $OHS_PERL_BIN);
&printOut("ORACLE_JAVA_HOME", $ORACLE_JAVA_HOME);
&printOut("WL_DEPLOY_CLASSPATH", $WL_DEPLOY_CLASSPATH);

print "\n";
print OUTFILE "\n";

# Useful environment variables
&printOut("JAVA_HOME", $ENV{'JAVA_HOME'});
&printOut("PERL_HOME", $ENV{'PERL_HOME'});

close(OUTFILE);

# ------------------------------
# Subroutine to print out environment variable information
# to both standard output and to the output file

sub printOut {
  local($name, $val) = @_;

  # Test Logic doesn't like backslashes
  if ($os eq "Windows") {
    $val =~ s/\\/\//g;
  }
  print $name . "=" . $val . "\n";
  print OUTFILE "export $name" . "=" . $val . "\n";
}
