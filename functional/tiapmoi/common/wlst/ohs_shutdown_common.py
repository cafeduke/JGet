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

cmdStr="shutdown()"
TSTCASE=TSTCASE + 1
printLog("########################################")
printLog("#### - BEGIN " + str(TSTCASE))
printLog("shutdown()")
printLog("WARNING:  shutdown() will kill the AdminServer - bug 14702412")
printLog("This is a usability issue as effectively running shutdown() is a")
printLog("kill-switch for the environment.  It should be harder for the user")
printLog("to shoot themselves in the foot here.")
printLog("#### - END " + str(TSTCASE))
printLog("########################################")
#exeCmd(cmdStr, TSTCASE)

cmdStr="shutdown(\'\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="shutdown(\' \')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="shutdown(\'SystemComponent\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="shutdown(\'\',\'\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="shutdown(\'\',\'SystemComponent\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="ohs_createInstance(instanceName=\'" + OHS_NAME + "\', machine=\'" + MACHINE + "\')"
executeCommand(cmdStr)

cmdStr="shutdown(\'" + OHS_NAME + "\',\'\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

# No such instance
cmdStr="shutdown(\'bogus\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="shutdown(\'bogus\',\'SystemComponent\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

# Wrong/invalid system type
cmdStr="shutdown(\'" + OHS_NAME + "\',\'Server\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="shutdown(\'" + OHS_NAME + "\',\'Cluster\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="shutdown(\'" + OHS_NAME + "\',\'bogus\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

# Try to stop an instance already in SHUTDOWN
cmdStr="state(\'" + OHS_NAME + "\')"
executeCommand(cmdStr)

cmdStr="shutdown(\'" + OHS_NAME + "\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="shutdown(\'" + OHS_NAME + "\', \'SystemComponent\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="ohs_deleteInstance(instanceName=\'" + OHS_NAME + "\')"
executeCommand(cmdStr)

# --------------------------------------------
# Disconnect

disconnectCommon()
printLog("Operation completed.")
