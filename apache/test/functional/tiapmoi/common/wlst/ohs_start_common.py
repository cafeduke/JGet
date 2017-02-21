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

cmdStr="start()"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="start(\'\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="start(\' \')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="start(\'SystemComponent\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="start(\'\',\'\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="start(\'\',\'SystemComponent\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="ohs_createInstance(instanceName=\'" + OHS_NAME + "\',machine=\'" + MACHINE + "\')"
executeCommand(cmdStr)

cmdStr="start(\'" + OHS_NAME + "\',\'\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

# No such instance
cmdStr="start(\'bogus\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="start(\'bogus\',\'SystemComponent\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

# Wrong/invalid system type
cmdStr="start(\'" + OHS_NAME + "\',\'Server\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="start(\'" + OHS_NAME + "\',\'Cluster\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="start(\'" + OHS_NAME + "\',\'bogus\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

# Instance is already running
cmdStr="start(\'" + OHS_NAME + "\')"
executeCommand(cmdStr)

cmdStr="start(\'" + OHS_NAME + "\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="start(\'" + OHS_NAME + "\', \'SystemComponent\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="shutdown(\'" + OHS_NAME + "\')"
executeCommand(cmdStr)
cmdStr="ohs_deleteInstance(instanceName=\'" + OHS_NAME + "\')"
executeCommand(cmdStr)

# --------------------------------------------
# Disconnect

disconnectCommon()
printLog("Operation completed.")
