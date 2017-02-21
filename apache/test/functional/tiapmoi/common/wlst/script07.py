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
print "Create1:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="ohs_createInstance(instanceName=\'" + OHS_NAME + "\',machine=\'" + MACHINE_NAME + "\')"
executeCommand(cmdStr)

cmdStr="state(\'" + OHS_NAME + "\',\'SystemComponent\')"
executeCommand(cmdStr)

print "--------------------------------------------"
print "Remove1:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="ohs_deleteInstance(instanceName=\'" + OHS_NAME + "\')"
executeCommand(cmdStr)

print "--------------------------------------------"
print "Create2:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="ohs_createInstance(instanceName=\'" + OHS_NAME + "\',machine=\'" + MACHINE_NAME + "\')"
executeCommand(cmdStr)

cmdStr="state(\'" + OHS_NAME + "\',\'SystemComponent\')"
executeCommand(cmdStr)

print "--------------------------------------------"
print "Remove2:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="ohs_deleteInstance(instanceName=\'" + OHS_NAME + "\')"
executeCommand(cmdStr)

print "--------------------------------------------"
print "Create3:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="ohs_createInstance(instanceName=\'" + OHS_NAME + "\',machine=\'" + MACHINE_NAME + "\')"
executeCommand(cmdStr)

cmdStr="state(\'" + OHS_NAME + "\',\'SystemComponent\')"
executeCommand(cmdStr)

print "--------------------------------------------"
print "Remove3:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="ohs_deleteInstance(instanceName=\'" + OHS_NAME + "\')"
executeCommand(cmdStr)

print "--------------------------------------------"
print "Create4:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="ohs_createInstance(instanceName=\'" + OHS_NAME + "\',machine=\'" + MACHINE_NAME + "\')"
executeCommand(cmdStr)

cmdStr="state(\'" + OHS_NAME + "\',\'SystemComponent\')"
executeCommand(cmdStr)

print "--------------------------------------------"
print "Remove4:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="ohs_deleteInstance(instanceName=\'" + OHS_NAME + "\')"
executeCommand(cmdStr)

print "--------------------------------------------"
print "Create5:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="ohs_createInstance(instanceName=\'" + OHS_NAME + "\',machine=\'" + MACHINE_NAME + "\')"
executeCommand(cmdStr)

cmdStr="state(\'" + OHS_NAME + "\',\'SystemComponent\')"
executeCommand(cmdStr)

print "--------------------------------------------"
print "Remove5:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="ohs_deleteInstance(instanceName=\'" + OHS_NAME + "\')"
executeCommand(cmdStr)

print "--------------------------------------------"
print "Create6:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="ohs_createInstance(instanceName=\'" + OHS_NAME + "\',machine=\'" + MACHINE_NAME + "\')"
executeCommand(cmdStr)

cmdStr="state(\'" + OHS_NAME + "\',\'SystemComponent\')"
executeCommand(cmdStr)

print "--------------------------------------------"
print "Remove6:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="ohs_deleteInstance(instanceName=\'" + OHS_NAME + "\')"
executeCommand(cmdStr)

print "--------------------------------------------"
print "Create7:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="ohs_createInstance(instanceName=\'" + OHS_NAME + "\',machine=\'" + MACHINE_NAME + "\')"
executeCommand(cmdStr)

cmdStr="state(\'" + OHS_NAME + "\',\'SystemComponent\')"
executeCommand(cmdStr)

print "--------------------------------------------"
print "Remove7:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="ohs_deleteInstance(instanceName=\'" + OHS_NAME + "\')"
executeCommand(cmdStr)

print "--------------------------------------------"
print "Create8:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="ohs_createInstance(instanceName=\'" + OHS_NAME + "\',machine=\'" + MACHINE_NAME + "\')"
executeCommand(cmdStr)

cmdStr="state(\'" + OHS_NAME + "\',\'SystemComponent\')"
executeCommand(cmdStr)

print "--------------------------------------------"
print "Remove8:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="ohs_deleteInstance(instanceName=\'" + OHS_NAME + "\')"
executeCommand(cmdStr)

print "--------------------------------------------"
print "Create9:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="ohs_createInstance(instanceName=\'" + OHS_NAME + "\',machine=\'" + MACHINE_NAME + "\')"
executeCommand(cmdStr)

cmdStr="state(\'" + OHS_NAME + "\',\'SystemComponent\')"
executeCommand(cmdStr)

print "--------------------------------------------"
print "Remove9:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="ohs_deleteInstance(instanceName=\'" + OHS_NAME + "\')"
executeCommand(cmdStr)

print "--------------------------------------------"
print "Create10:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="ohs_createInstance(instanceName=\'" + OHS_NAME + "\',machine=\'" + MACHINE_NAME + "\')"
executeCommand(cmdStr)

cmdStr="state(\'" + OHS_NAME + "\',\'SystemComponent\')"
executeCommand(cmdStr)

print "--------------------------------------------"
print "Remove10:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="ohs_deleteInstance(instanceName=\'" + OHS_NAME + "\')"
executeCommand(cmdStr)

# --------------------------------------------
# Disconnect

disconnectCommon()
printLog("Operation completed.")
