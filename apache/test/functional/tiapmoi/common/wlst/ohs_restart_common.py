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
# Note that softRestart() applies only to system components and there is
# no support for Servers or Clusters.

cmdStr="softRestart()"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="softRestart(\'\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="softRestart(\' \')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

# No such instance
cmdStr="softRestart(\'bogus\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

# --------------------------------
# Check restart at various states

cmdStr="ohs_createInstance(instanceName=\'" + OHS_NAME + "\',machine=\'" + MACHINE + "\')"
executeCommand(cmdStr)
cmdStr="state(\'" + OHS_NAME + "\')"
executeCommand(cmdStr)

# Restart after instance creation
cmdStr="softRestart(\'" + OHS_NAME + "\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="start(\'" + OHS_NAME + "\')"
executeCommand(cmdStr)

# Should be successful
cmdStr="softRestart(\'" + OHS_NAME + "\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="shutdown(\'" + OHS_NAME + "\')"
executeCommand(cmdStr)

# Restart after a shutdown
cmdStr="softRestart(\'" + OHS_NAME + "\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="ohs_deleteInstance(instanceName=\'" + OHS_NAME + "\')"
executeCommand(cmdStr)

# Instance no longer exists
cmdStr="softRestart(\'" + OHS_NAME + "\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

# --------------------------------------------
# Disconnect

disconnectCommon()
printLog("Operation completed.")
