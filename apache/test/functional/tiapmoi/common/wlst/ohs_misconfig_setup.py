import sys
import java.lang.System as System
from java.lang import String
execfile(System.getenv('ADE_VIEW_ROOT') + '/apache/test/functional/common/wlst/ohs_common.py')

OHS_NAME = getVar("OHS_NAME")
MACHINE_NAME = getVar("MACHINE_NAME")
TSTCASE=0

# --------------------------------------------
# Connect

connectCommon()

# -------------------------------------------
# Create three instances

cmdStr="ohs_createInstance(instanceName=\'ohs01\',machine=\'" + MACHINE_NAME + "\')"
executeCommand(cmdStr)
cmdStr="ohs_createInstance(instanceName=\'ohs02\',machine=\'" + MACHINE_NAME + "\')"
executeCommand(cmdStr)
cmdStr="ohs_createInstance(instanceName=\'ohs03\',machine=\'" + MACHINE_NAME + "\')"
executeCommand(cmdStr)

# -------------------------------------------
# Start ohs02 and ohs03.  The ohs01 instances needs to be
# shutdown initially as we are testing start() with misconfig

cmdStr="start(\'ohs02\')"
executeCommand(cmdStr)
cmdStr="start(\'ohs03\')"
executeCommand(cmdStr)

# --------------------------------------------
# Disconnect

disconnectCommon()
printLog("Operation completed.")
