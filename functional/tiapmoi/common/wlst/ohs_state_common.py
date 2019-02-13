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

# Common Exceptions
cmdStr="state()"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="state(\'\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="state(\' \')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="state(\'SystemComponent\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="state(\'\',\'\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="state(\'\',\'SystemComponent\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="ohs_createInstance(instanceName=\'" + OHS_NAME + "\', machine=\'" + MACHINE + "\')"
executeCommand(cmdStr)

cmdStr="state(\'" + OHS_NAME + "\',\'\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

# No such instance
cmdStr="state(\'bogus\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="state(\'bogus\',\'SystemComponent\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

# Wrong/invalid system type
cmdStr="state(\'" + OHS_NAME + "\',\'Server\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="state(\'" + OHS_NAME + "\',\'Cluster\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="state(\'" + OHS_NAME + "\',\'bogus\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="ohs_deleteInstance(instanceName=\'" + OHS_NAME + "\')"
executeCommand(cmdStr)

# --------------------------------------------
# Disconnect

disconnectCommon()
printLog("Operation completed.")
