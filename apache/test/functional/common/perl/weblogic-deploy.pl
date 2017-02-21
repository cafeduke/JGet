#
# Copyright (c) 2002, Oracle Corporation.  All rights reserved.  
#
#    NAME
#      weblogic-deploy.pl
#
#    DESCRIPTION
#	Macro to remotely deploy a web application to Weblogic
#	perl weblogic-deploy.pl <ear_file> 
#
#    MODIFIED   (MM/DD/YY)
#    kdclark    05/15/08 - Creation
# 

use File::Basename;
my $CURRENT_DIR = dirname(__FILE__);
require "$CURRENT_DIR/support.pl";

$archive=$ARGV[0];

# Grab other info we need from env variables
$WLS_USER   = $ENV{WLS_USER};
$WLS_PWD    = $ENV{WLS_PWD};
$archive    = "$ENV{T_WORK}/$archive";
$JAVA_HOME  = $ENV{ORACLE_JAVA_HOME};
$CLASSPATH  = $ENV{WL_DEPLOY_CLASSPATH};
$adminURL   = $ENV{ADMIN_HOST} . ":" . $ENV{ADMIN_PORT};

if (!-e $archive) {
  print "Unable to locate archive: $archive\n";
  print "Exiting...\n";
  exit 1;
}

# Remotely deploy web application
$cmd = "$JAVA_HOME/bin/java -classpath $CLASSPATH weblogic.Deployer -user $WLS_USER -password $WLS_PWD -deploy -upload $archive -adminurl $adminURL";
print "$cmd\n";
system($cmd); 


