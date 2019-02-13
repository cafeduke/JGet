# 
# Copyright (c) 2002, Oracle Corporation.  All rights reserved.  
#
#    NAME
#      weblogic-undeploy.pl
#
#    DESCRIPTION
#	Macro to remotely undeploy a web application from Weblogic
#	perl weblogic-undeploy.pl <ear_file> 
#
#    MODIFIED   (MM/DD/YY)
#    kdclark    09/11/14 - update to 12.2.1
#    kdclark    05/15/08 - Creation
# 

use File::Basename;
my $CURRENT_DIR = dirname(__FILE__);
require "$CURRENT_DIR/support.pl";

$archive = $ARGV[0];

# Grab other info we need from env variables
$WLS_USER   = $ENV{WLS_USER};
$WLS_PWD    = $ENV{WLS_PWD};
$JAVA_HOME  = $ENV{ORACLE_JAVA_HOME};
$CLASSPATH  = $ENV{WL_DEPLOY_CLASSPATH};
$adminURL   = $ENV{ADMIN_HOST} . ":" . $ENV{ADMIN_PORT};

# Remotely undeploy web application
$cmd = "$JAVA_HOME/bin/java -classpath $CLASSPATH weblogic.Deployer -user $WLS_USER -password $WLS_PWD -undeploy -name $archive -adminurl $adminURL";
print "$cmd\n";
system("$cmd"); 


