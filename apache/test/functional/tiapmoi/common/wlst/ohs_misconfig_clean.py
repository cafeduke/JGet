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
# At this point ohs03 was successfully deleted, so we only have
# to clean up ohs01 and ohs02.  Both are in state FAILED, so (now that the 
# misconfiguration has been removed), need to be started, then stopped, then
# removed.

cmdStr="start(\'ohs01\')"
executeCommand(cmdStr)
cmdStr="start(\'ohs02\')"
executeCommand(cmdStr)

cmdStr="shutdown(\'ohs01\')"
executeCommand(cmdStr)
cmdStr="shutdown(\'ohs02\')"
executeCommand(cmdStr)

cmdStr="ohs_deleteInstance(instanceName=\'ohs01\')"
executeCommand(cmdStr)
cmdStr="ohs_deleteInstance(instanceName=\'ohs02\')"
executeCommand(cmdStr)

# --------------------------------------------
# Disconnect

disconnectCommon()
printLog("Operation completed.")
