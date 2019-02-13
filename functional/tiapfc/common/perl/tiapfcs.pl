# 
# Copyright (c) 2002, Oracle Corporation.  All rights reserved.  
#
#    NAME
#      tiapfcs.pl
#
#    DESCRIPTION
#      Setup script for tiapfc test.
#
#    NOTES
#      <other useful comments, qualifications, etc.>
#
#    MODIFIED   (MM/DD/YY)
#    kdclark     08/12/12
#    oraqasv1    08/05/04 - 
#    schristl    12/20/02 - schristl_apache_1217
#    schristl    12/20/02 - Creation
# 

# Include a little support
require "$ENV{IASQA_HOME}/common/src/support.pl";

# Compile the 
chdir("tiapfc/sosd");
if ($platform eq "nt") {
  system("nmake -f Makefile.mak all");
} else {
  system("make all");
}
# Install echo binaries in fcgi-bin
$install_dir = "$ENV{OHS_CONFIG_INST}/fcgi-bin";

# Copy the fastcgi programs
copy("tiapfc_echo1", "$install_dir");
chmod(0755, "$install_dir/tiapfc_echo1");
copy("tiapfc_echo2", "$install_dir");
chmod(0755, "$install_dir/tiapfc_echo2");

