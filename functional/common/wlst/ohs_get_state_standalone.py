import sys
import java.lang.System as System
from java.lang import String
execfile(System.getenv('ADE_VIEW_ROOT') + '/apache/test/functional/common/wlst/ohs_common.py')

OHS_NAME = getVar("OHS_NAME")
OUTFILE  = getVar("OUTFILE")

# Connect to NM
nmConnectCommon()

# Get the state
print "Running:"
print "nmServerStatus(serverName=\'" + OHS_NAME + "\',serverType=\'OHS\')"
val=nmServerStatus(serverName=OHS_NAME,serverType='OHS')
file = open(OUTFILE, "w")
file.write("echo OHS instance: " + OHS_NAME + " is currently in State: " + val + "\n")
file.close()

# Exit
nmDisconnectCommon()
