import sys
import java.lang.System as System
from java.lang import String
execfile(System.getenv('ADE_VIEW_ROOT') + '/apache/test/functional/common/wlst/ohs_common.py')

OHS_NAME = getVar("OHS_NAME")
OUTFILE  = getVar("OUTFILE")

# Connect to WLST
connectCommon()

# Move into the proper area
cmdStr="domainRuntime()"
executeCommand(cmdStr)
cmdStr="cd(\"SystemComponentLifeCycleRuntimes\")"
executeCommand(cmdStr)
cmdStr="cd(\"" + OHS_NAME + "\")"
executeCommand(cmdStr)

# Get the state
val=get("State")
print "OHS State: " + val
file = open(OUTFILE, "w")
file.write("echo OHS instance: " + OHS_NAME + " is currently in State: " + val + "\n")
file.close()

# Exit
disconnectCommon()
