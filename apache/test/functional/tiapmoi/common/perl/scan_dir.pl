#/usr/bin/perl

#############################################################################
# Author: kdclark
# Creation:  09/22/08
#
# scan_dir.pl
# Utility script to list a given directory in the ORACLE_HOME.  Due to the
# behavior of the 'ls' command, the results are then sorted and written out
# to the provided <outfile>.
#
# Usage:  perl scan_dir <action> <outfile> [<ohsName>]
#
# Action = go scan the following:
#   ohs_bin  --> $ORACLE_HOME/ohs/bin
#   ohs_conf --> $ORACLE_HOME/ohs/templates/conf
#   ohs_lib  --> $ORACLE_HOME/ohs/lib
#   oi_conf  --> $OI/config/fmwconfig/components/OHS/ohs1
#   oi_inst  --> $OI/config/fmwconfig/components/OHS/instances/ohs1
#   oi_logs  --> $OI/servers/ohs1/logs
#   oi_syst  --> $OI/system_components/OHS/ohs1
#
############################################################################
use File::Copy;
require "$ENV{ADE_VIEW_ROOT}/apache/test/functional/common/perl/support.pl";

$platform = $ENV{'OSTYPE'};
if ($platform =~ /MSWin32/) { $platform = "nt"; }

# Set up platform specific stuff
if ($platform eq "nt") {
  $SD  = "\\";
  $ls = "C:\\mksnt\\ls.exe"; 
} else {
  $SD  = "\/";
  $ls = "/bin/ls";
}

$action	         = $ARGV[0];
$logfile         = $ARGV[1];
$OHS_NAME        = $ARGV[2];
$ORACLE_HOME     = $ENV{ORACLE_HOME};
$ohsConfDir      = $ENV{ohsConfDir};
$nmLogDir        = $ENV{nmLogDir};
$ohsLogDir       = $ENV{ohsLogDir};
$twork           = $ENV{T_WORK};

$OHS_NAME = "ohs1" unless $OHS_NAME;

# --------------------------------
# Determine which directory to scan
# Add any exceptions to the scan in here
# --------------------------------

$dir="";
$cmd = "$ls -1 -R ";
$ohsBase  = $ORACLE_HOME . $SD . "ohs" . $SD;

if ($action =~ /ohs_bin/)  { 
     $dir = $ohsBase. "bin"; 
}
if ($action =~ /ohs_conf/) { $dir = $ohsBase . "templates" . $SD . "conf"; }
if ($action =~ /ohs_lib/)  { $dir = $ohsBase . "lib"; }

if ($action =~ /oi_conf/) {
     $dir = $ohsConfDir . $SD . $OHS_NAME; 
}
if ($action =~ /oi_inst/) { 
     $dir = $ohsConfDir . $SD . "instances" . $SD . $OHS_NAME;
}

if ($action =~ /oi_logs/) {
     $dir = $ohsLogDir . $SD . $OHS_NAME . $SD . "logs";
}
if ($action =~ /oi_syst/) {
     $dir = $nmLogDir . $SD . $OHS_NAME;
}

# --------------------------------   
# Does the directory exist?
if (!-d $dir) {
    # If no, then report in the log
    print "ERROR:  Location $dir does not exist!\n";
    exit 1;
}

# --------------------------------
# Scan the directory

chdir $dir || die "Unable to chdir to $dir!";
$cmd1 = $cmd . " >> $twork/jum.txt";
system($cmd1);

$cmd = $cmd . " >> $logfile";
system($cmd);

# --------------------------------
# Sort the output file

# The ls command has many different flavours so it is necessary to sort the
# file afterwards for a pure goldfile match.

# Sweep through on first pass and grab the directory headers
@lines   = ();
@headers = ();
$ignore  = 0;
open (INFILE, $logfile) || die "Can't open $logfile!";
while (<INFILE>) {

     # Check to make sure we have something to sort
     if ($_ =~ /does not exist/) {
        close(INFILE);
        exit 0;
     }

     $line = $_;
     $line =~ s/\:/\/\:/;

     # If a directory header...
     if ($line =~ /\:/) {
       # Ignore the "content" directories
       if (($line =~ /htdocs/) ||
           ($line =~ /manual/) ||
           ($line =~ /icons/)  ||
           ($line =~ /man/)    ||
           ($line =~ /auditlogs/) ||
           ($line =~ /error/)) {
         $ignore = 1;
         next;
       } else {
         # Grab it
         push(@headers, $line);
         $ignore = 0;
       }
     }

     # If we're in the manual directory do not capture 
     if ($ignore) {
        next;
     } else {
       # Capture all lines in the file 
       push(@lines, $line);
     }
}
close(INFILE);
push (@lines, "\n");

# ------------------
# Sort the directory headers.
@headers = sort(@headers);

# ------------------
# Now we go through the lines again and sort the contents of each dir

foreach $i (0 .. $#headers) {
   $curHeader   = $headers[$i];
   $record      = 0;
   @items       = ();
   @orderedlist = ();

   foreach $j (0 .. $#lines) {
     $curLine = $lines[$j];

     if (($record == 1) && ($curLine eq "\n")) {
       # Okay we found the end, we're done with this header
       # Sort the items found and write to output
       @orderedlist = sort(@items);
       foreach $k (0 .. $#orderedlist) {
         $output = $output . $orderedlist[$k];
       }
       $output = $output . "\n";
       last;
     }

     if ($record == 1) {
       # We're recording;  grab it to be sorted
       push(@items, $curLine);
       next;
     }

     if ($curHeader eq $curLine) {
       # Okay we found the directory we're looking for.
       # Start recording what comes next...
       $output = $output . $curHeader;
       $record = 1;
     }

   }
}

# Write the sorted output to STDOUT
print $output;

