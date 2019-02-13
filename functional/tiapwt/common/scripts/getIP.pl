#!/usr/local/bin/perl
# 
# getIP.pl
# 
# Copyright (c) 2008, Oracle.  All rights reserved.  
#
#    DESCRIPTION
#      Capture local machine's IP address
#
#    NOTES
#      <other useful comments, qualifications, etc.>
#
#    MODIFIED   (MM/DD/YY)
#    kdclark     12/21/12 - fix for bug #16033947
#    kdclark     05/18/10 - Creation
# 


$twork = $ENV{T_WORK};
$platform = $ENV{OSTYPE};
if ($platform =~ /MSWin32/) { $platform = "nt"; }

if ($platform eq "nt") {

  # On Windows need to parse a ping command - this doesn't work on linux
  $hostname = `hostname`;

  # This no longer seems to work on Win2008 X64 RC2
  #@junk = `ping $hostname -n 1`;
  #foreach $i (0 .. $#junk) {
  # if ($junk[$i] =~ /Reply from/) {  
  #  $line = $junk[$i];
  #  ($ipAddr, $junk) = split(/:/,$line);
  #  ($junk, $junk2, $ipAddr) = split(/ /,$ipAddr);
  #  break;
  # }

  # Use nslookup to get the IP address
  @junk = `nslookup $hostname`;
  $record = 0;
  foreach $i (0 .. $#junk) {
    $line = $junk[$i];
    if ($line =~ /Name\:/) {  $record = 1; next;  }
    if (($record == 1) && ($line =~ /Address\:/)) {
       ($junk, $ipAddr) = split(/\: /, $line);
       $record = 0;
       last;
    } 
  }

} else {
  # Linux - grab IP address directly;  this doesn't work on Windows
  $ipAddr = `hostname -i`;
}
print  "IP_HOST=$ipAddr\n";



