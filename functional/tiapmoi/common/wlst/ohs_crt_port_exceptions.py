import sys
import java.lang.System as System
from java.lang import String
execfile(System.getenv('ADE_VIEW_ROOT') + '/apache/test/functional/common/wlst/ohs_common.py')

OHS_NAME = getVar("OHS_NAME")
MACHINE  = getVar("MACHINE_NAME")
ADMIN_PORT = getVar("ADMIN_PORT")
ADMIN_SSL_PORT = getVar("ADMIN_SSL_PORT")
TSTCASE = 0

# --------------------------------------------
# Easily generate the commands

def mkCmd(ohsName, mach, portInfo):
   cmdS="ohs_createInstance(instanceName=\'"
   cmdM="\',machine=\'" + mach + "\', "
   cmdE=")";
   cmdStr=cmdS + ohsName + cmdM + portInfo + cmdE
   return cmdStr

# --------------------------------------------
# Connect

connectCommon()

# -------------------------------------------
# No ports provided

PORT = "listenPort=\'\'"
TSTCASE = TSTCASE + 1
cmdStr = mkCmd(OHS_NAME, MACHINE, PORT)
exeCmd(cmdStr, TSTCASE)

PORT = "listenPort=\' \'"
TSTCASE = TSTCASE + 1
cmdStr = mkCmd(OHS_NAME, MACHINE, PORT)
exeCmd(cmdStr, TSTCASE)

PORT = "sslPort=\'\'"
TSTCASE = TSTCASE + 1
cmdStr = mkCmd(OHS_NAME, MACHINE, PORT)
exeCmd(cmdStr, TSTCASE)

PORT = "sslPort=\' \'"
TSTCASE = TSTCASE + 1
cmdStr = mkCmd(OHS_NAME, MACHINE, PORT)
exeCmd(cmdStr, TSTCASE)

PORT = "adminPort=\'\'"
TSTCASE = TSTCASE + 1
cmdStr = mkCmd(OHS_NAME, MACHINE, PORT)
exeCmd(cmdStr, TSTCASE)

PORT = "adminPort=\' \'"
TSTCASE = TSTCASE + 1
cmdStr = mkCmd(OHS_NAME, MACHINE, PORT)
exeCmd(cmdStr, TSTCASE)

# ------------------------------------------
# Invalid format

PORT = "listenPort=7778.9"
TSTCASE = TSTCASE + 1
cmdStr = mkCmd(OHS_NAME, MACHINE, PORT)
exeCmd(cmdStr, TSTCASE)

PORT = "listenPort=badFormat"
TSTCASE = TSTCASE + 1
cmdStr = mkCmd(OHS_NAME, MACHINE, PORT)
exeCmd(cmdStr, TSTCASE)

PORT = "listenPort=\'badFormat\'"
TSTCASE = TSTCASE + 1
cmdStr = mkCmd(OHS_NAME, MACHINE, PORT)
exeCmd(cmdStr, TSTCASE)

PORT = "listenPort=7779,7780"
TSTCASE = TSTCASE + 1
cmdStr = mkCmd(OHS_NAME, MACHINE, PORT)
exeCmd(cmdStr, TSTCASE)

# ------------------------------------------
# Bad ports

PORT = "listenPort=-1"
TSTCASE = TSTCASE + 1
cmdStr = mkCmd(OHS_NAME, MACHINE, PORT)
exeCmd(cmdStr, TSTCASE)

PORT = "listenPort=0"
TSTCASE = TSTCASE + 1
cmdStr = mkCmd(OHS_NAME, MACHINE, PORT)
exeCmd(cmdStr, TSTCASE)

PORT = "listenPort=000"
TSTCASE = TSTCASE + 1
cmdStr = mkCmd(OHS_NAME, MACHINE, PORT)
exeCmd(cmdStr, TSTCASE)

PORT = "listenPort=18000000000000"
TSTCASE = TSTCASE + 1
cmdStr = mkCmd(OHS_NAME, MACHINE, PORT)
exeCmd(cmdStr, TSTCASE)

# ------------------------------------------
# Priviledged ports

PORT = "listenPort=1"
TSTCASE = TSTCASE + 1
cmdStr = mkCmd(OHS_NAME, MACHINE, PORT)
exeCmd(cmdStr, TSTCASE)

cmdStr = "ohs_deleteInstance(instanceName=\'" + OHS_NAME + "\')"
executeCommand(cmdStr)

# Note port 80 is legal on Windows
PORT = "listenPort=79"
TSTCASE = TSTCASE + 1
cmdStr = mkCmd(OHS_NAME, MACHINE, PORT)
exeCmd(cmdStr, TSTCASE)

cmdStr = "ohs_deleteInstance(instanceName=\'" + OHS_NAME + "\')"
executeCommand(cmdStr)

PORT = "listenPort=1023"
TSTCASE = TSTCASE + 1
cmdStr = mkCmd(OHS_NAME, MACHINE, PORT)
exeCmd(cmdStr, TSTCASE)

cmdStr = "ohs_deleteInstance(instanceName=\'" + OHS_NAME + "\')"
executeCommand(cmdStr)

# ------------------------------------------
# Identical port values

PORT = "listenPort=9000, sslPort=9000"
TSTCASE = TSTCASE + 1
cmdStr = mkCmd(OHS_NAME, MACHINE, PORT)
exeCmd(cmdStr, TSTCASE)

PORT = "adminPort=9000, listenPort=9000"
TSTCASE = TSTCASE + 1
cmdStr = mkCmd(OHS_NAME, MACHINE, PORT)
exeCmd(cmdStr, TSTCASE)

PORT = "adminPort=9000, sslPort=9000"
TSTCASE = TSTCASE + 1
cmdStr = mkCmd(OHS_NAME, MACHINE, PORT)
exeCmd(cmdStr, TSTCASE)

# ------------------------------------------
# Try to assign port value of port already in use

myOHS1=OHS_NAME + "_1"
myOHS2=OHS_NAME + "_2"
cmdStr = "ohs_createInstance(instanceName=\'" + myOHS1 + "\',machine=\'" + MACHINE + "\', listenPort=8000)"
executeCommand(cmdStr)

cmdStr = "ohs_createInstance(instanceName=\'" + myOHS2 + "\',machine=\'" + MACHINE + "\', listenPort=8000)"
TSTCASE = TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="ohs_deleteInstance(instanceName=\'" + myOHS1 + "\')"
executeCommand(cmdStr)

# ------------------------------------------
# Port is WLS Console Port

PORT = "listenPort=" + ADMIN_PORT
TSTCASE = TSTCASE + 1
cmdStr = mkCmd(OHS_NAME, MACHINE, PORT)
exeCmd(cmdStr, TSTCASE)

PORT = "sslPort=" + ADMIN_PORT
TSTCASE = TSTCASE + 1
cmdStr = mkCmd(OHS_NAME, MACHINE, PORT)
exeCmd(cmdStr, TSTCASE)

PORT = "listenPort=" + ADMIN_SSL_PORT
TSTCASE = TSTCASE + 1
cmdStr = mkCmd(OHS_NAME, MACHINE, PORT)
exeCmd(cmdStr, TSTCASE)

PORT = "adminPort=" + ADMIN_SSL_PORT
TSTCASE = TSTCASE + 1
cmdStr = mkCmd(OHS_NAME, MACHINE, PORT)
exeCmd(cmdStr, TSTCASE)

# ------------------------------------------
# Port is NM port

PORT = "listenPort=5556"
TSTCASE = TSTCASE + 1
cmdStr = mkCmd(OHS_NAME, MACHINE, PORT)
exeCmd(cmdStr, TSTCASE)

PORT = "sslPort=5556"
TSTCASE = TSTCASE + 1
cmdStr = mkCmd(OHS_NAME, MACHINE, PORT)
exeCmd(cmdStr, TSTCASE)

# --------------------------------------------
# Disconnect

disconnectCommon()
printLog("Operation completed.")
