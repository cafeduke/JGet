#!/usr/local/bin/perl
# Copyright (c) 2008, Oracle.  All rights reserved.  
#
#    NAME
#      ohs_get_ports.pl - <one-line expansion of the name>
#
#    DESCRIPTION
#      Capture the ports for a given OHS instance
#      Captures info direct from OHS config files
#      Usage:  ohs_get_ports.pl -ohs <ohsName>
#     
#      Port file will be written to $T_WORK.
#    
#    NOTES
#      <other useful comments, qualifications, etc.>
#
#    MODIFIED   (MM/DD/YY)
#    kdclark     09/11/14 - update for 12.2.1
#    kdclark     09/24/08 - Creation
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
   print "ERROR:  No OHS_NAME was provided!\n";
   exit 1;
}

$twork           = $ENV{T_WORK};
$ohsConfDir      = $ENV{ohsConfDir};
$PORT_FILE       = $twork . "/ports-" . $OHS_NAME . ".txt";

# -------------------------------
# In collocated one can do a WLS call to get the ports
# This does not work in standalone OHS, however.  
# Lacking an equivalent command to opmnctl status we will grab the
# ports right out of the given OHS instance.

$CONFIG_DIR = $ohsConfDir . "/instances/" . $OHS_NAME . "/"; 
$HTTP_FILE  = $CONFIG_DIR . "httpd.conf";
$SSL_FILE   = $CONFIG_DIR . "ssl.conf";

if (!-e $HTTP_FILE) {
   print "ERROR:  Unable to find:\n$HTTP_FILE\n\n";
   print "Could the OHS instance name: $OHS_NAME be incorrect?\nExiting...\n";
   exit 1;
}

$cmd = "grep Listen $HTTP_FILE | grep -v \\#";
$line = `$cmd`;
($junk, $APACHE_PORT) = split(/Listen /, $line);
chomp $APACHE_PORT;

$cmd = "grep Listen $SSL_FILE | grep -v \\#";
$line = `$cmd`;
($junk, $APACHE_SSL_PORT) = split(/Listen /, $line);
($APACHE_SSL_PORT, $junk) = split(/ /, $APACHE_SSL_PORT);

# If the port file already exists, let's replace it... the instance
# might be listening on new ports.
if (-e $PORT_FILE) {
   $cmd = "rm $PORT_FILE";
   system($cmd);
}

print "================================\n";
print "OHS Instance:  $OHS_NAME\n";
print "APACHE_PORT=$APACHE_PORT\n";
print "APACHE_SSL_PORT=$APACHE_SSL_PORT\n";
print "Writing out new port file:\n$PORT_FILE\n";
print "================================\n";

# Write out the new port file...
open(OUTFILE, ">$PORT_FILE") || die "Unable to open $PORT_FILE";
print OUTFILE "APACHE_PORT=" . $APACHE_PORT . "\n";
print OUTFILE "APACHE_SSL_PORT=" . $APACHE_SSL_PORT . "\n";
close(OUTFILE);
