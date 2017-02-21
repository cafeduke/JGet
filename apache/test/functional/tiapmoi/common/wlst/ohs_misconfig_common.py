import sys
import java.lang.System as System
from java.lang import String
execfile(System.getenv('ADE_VIEW_ROOT') + '/apache/test/functional/common/wlst/ohs_common.py')

OHS_NAME = getVar("OHS_NAME")
MACHINE  = getVar("MACHINE_NAME")
TSTCASE=0

# --------------------------------------------
# Connect

connectCommon()

# -------------------------------------------
# Note that we have three instances ready with misconfigurations

# Start with misconfiguration (not running)
# Start will fail, httpd not running
cmdStr="start(\'ohs01\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

# Restart with misconfiguration (already up)
# Shutdown will work, restart will not
cmdStr="softRestart(\'ohs02\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

# Shutdown with misconfiguration (already up)
# Shutdown will work successfully
cmdStr="shutdown(\'ohs03\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

# Delete the instances - check for problems
cmdStr="ohs_deleteInstance(instanceName=\'ohs01\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="ohs_deleteInstance(instanceName=\'ohs02\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="ohs_deleteInstance(instanceName=\'ohs03\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

# --------------------------------------------
# Disconnect

disconnectCommon()
printLog("Operation completed.")
