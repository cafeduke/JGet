import sys
import java.lang.System as System
from java.lang import String
execfile(System.getenv('ADE_VIEW_ROOT') + '/apache/test/mats/src/wlst/ohs_common.py')

# Use of this script has been deprecated as it works only for collocated
# environments.  It is faster & more consistent with standalone OHS to 
# just grab the ports directly from the OHS configuration files.  There
# is also a time hit every time we launch wlst.sh.

OHS_NAME = getVar("OHS_NAME")
OUTFILE  = getVar("OUTFILE")

# Connect to WLST
connectCommon()

# Move into the proper area
cmdStr="editCustom()"
executeCommand(cmdStr)
cmdStr="cd(\"oracle.ohs\")"
executeCommand(cmdStr)
ls()
cmdStr="cd(\"oracle.ohs:type=OHSInstance,name=" + OHS_NAME + "\")"
executeCommand(cmdStr)

# Grab ports and write to file
val=get("Ports")
print "APACHE_PORT:     " + val[0]
print "APACHE_SSL_PORT: " + val[1]
file = open(OUTFILE, "w")
file.write("APACHE_PORT=" + val[0] + "\n")
file.write("APACHE_SSL_PORT=" + val[1] + "\n")

# Close file
file.close()

# Exit
disconnectCommon()
