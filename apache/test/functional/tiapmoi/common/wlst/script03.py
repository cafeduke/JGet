import sys
import java.lang.System as System
from java.lang import String
execfile(System.getenv('ADE_VIEW_ROOT') + '/apache/test/functional/common/wlst/ohs_common.py')

OHS_NAME = getVar("OHS_NAME")
MACHINE_NAME = getVar("MACHINE_NAME")

# --------------------------------------------
# Connect

connectCommon()

print "--------------------------------------------"
print "Create:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="domainRuntime()"
executeCommand(cmdStr)

cmdStr="ohs_createInstance(instanceName=\'" + OHS_NAME + "\',machine=\'" + MACHINE_NAME + "\')"
executeCommand(cmdStr)

cmdStr="state(\'" + OHS_NAME + "\',\'SystemComponent\')"
executeCommand(cmdStr)

print "--------------------------------------------"
print "Start:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="start(\'" + OHS_NAME + "\',\'SystemComponent\')"
executeCommand(cmdStr)
cmdStr="state(\'" + OHS_NAME + "\',\'SystemComponent\')"
executeCommand(cmdStr)

print "--------------------------------------------"
print "Stop:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="shutdown(\'" + OHS_NAME + "\',\'SystemComponent\')"
executeCommand(cmdStr)
cmdStr="state(\'" + OHS_NAME + "\',\'SystemComponent\')"
executeCommand(cmdStr)

print "--------------------------------------------"
print "Remove:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="ohs_deleteInstance(instanceName=\'" + OHS_NAME + "\')"
executeCommand(cmdStr)

# --------------------------------------------
# Disconnect

disconnectCommon()
printLog("Operation completed.")
