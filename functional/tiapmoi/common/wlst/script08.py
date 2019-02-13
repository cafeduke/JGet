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

cmdStr="ohs_createInstance(instanceName=\'" + OHS_NAME + "\',machine=\'" + MACHINE_NAME + "\')"
executeCommand(cmdStr)

cmdStr="state(\'" + OHS_NAME + "\',\'SystemComponent\')"
executeCommand(cmdStr)

print "--------------------------------------------"
print "Start1:  " + OHS_NAME
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
print "Start2:  " + OHS_NAME
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
print "Start3:  " + OHS_NAME
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
print "Start4:  " + OHS_NAME
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
print "Start5:  " + OHS_NAME
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
print "Start6:  " + OHS_NAME
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
print "Start7:  " + OHS_NAME
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
print "Start8:  " + OHS_NAME
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
print "Start9:  " + OHS_NAME
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
print "Start10:  " + OHS_NAME
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
