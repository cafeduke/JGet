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

cmdStr="ohs_deleteInstance()"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="ohs_deleteInstance(\'\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="ohs_deleteInstance(\' \')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="ohs_deleteInstance(instanceName=\'\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="ohs_deleteInstance(instanceName=\' \')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="ohs_deleteInstance(ohs1)"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="ohs_createInstance(instanceName=\'" + OHS_NAME + "\',machine=\'" + MACHINE + "\')"
executeCommand(cmdStr)

cmdStr="ohs_deleteInstance(\'" + OHS_NAME + "\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="ohs_deleteInstance(bob=\'fred\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

# No such instance
cmdStr="ohs_deleteInstance(instanceName=\'bogus\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

# Delete a RUNNING instance
cmdStr="start(\'" + OHS_NAME + "\')"
executeCommand(cmdStr)
cmdStr="state(\'" + OHS_NAME + "\')"
executeCommand(cmdStr)

cmdStr="ohs_deleteInstance(instanceName=\'" + OHS_NAME + "\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="state(\'" + OHS_NAME + "\')"
executeCommand(cmdStr)
cmdStr="shutdown(\'" + OHS_NAME + "\')"
executeCommand(cmdStr)
cmdStr="ohs_deleteInstance(instanceName=\'" + OHS_NAME + "\')"
executeCommand(cmdStr)

# Delete instance already deleted
cmdStr="ohs_deleteInstance(instanceName=\'" + OHS_NAME + "\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

# --------------------------------------------
# Disconnect

disconnectCommon()
printLog("Operation completed.")
