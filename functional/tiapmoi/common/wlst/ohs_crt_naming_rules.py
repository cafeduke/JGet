import sys
import java.lang.System as System
from java.lang import String
execfile(System.getenv('ADE_VIEW_ROOT') + '/apache/test/functional/common/wlst/ohs_common.py')

OHS_NAME = getVar("OHS_NAME")
MACHINE = getVar("MACHINE_NAME")
TSTCASE = 0

# --------------------------------------------
# Easily generate the commands

def mkCmd(ohsName, mach):
   cmdS="ohs_createInstance(instanceName=\'"
   cmdE="\',machine=\'" + mach + "\')"
   cmdStr=cmdS + ohsName + cmdE
   return cmdStr

# --------------------------------------------
# Connect

connectCommon()

# -------------------------------------------
# Length
# These should not be permitted

OHS = "" # zero length
TSTCASE = TSTCASE + 1
cmdStr=mkCmd(OHS, MACHINE)
exeCmd(cmdStr, TSTCASE)

OHS = "abcdefghijklmnopqrstuvwxyzabcde"  # 31 char
TSTCASE = TSTCASE + 1
cmdStr=mkCmd(OHS, MACHINE)
exeCmd(cmdStr, TSTCASE)

# -------------------------------------------
# Allowed length:  test borders
# These should pass

OHS = "a"    # single char
TSTCASE = TSTCASE + 1
cmdStr=mkCmd(OHS, MACHINE)
exeCmd(cmdStr, TSTCASE)
cmdStr="ohs_deleteInstance(instanceName=\'" + OHS + "\')"
executeCommand(cmdStr)

OHS = "abcdefghijklmnopqrstuvwxyzabcd"  # 30 char
TSTCASE = TSTCASE + 1
cmdStr=mkCmd(OHS, MACHINE)
exeCmd(cmdStr, TSTCASE)
cmdStr="ohs_deleteInstance(instanceName=\'" + OHS + "\')"
executeCommand(cmdStr)

# -------------------------------------------
# Invalid starting character

OHS = "1ohs"
TSTCASE = TSTCASE + 1
cmdStr=mkCmd(OHS, MACHINE)
exeCmd(cmdStr, TSTCASE)

OHS = "-ohs1"  # linux issues?
TSTCASE = TSTCASE + 1
cmdStr=mkCmd(OHS, MACHINE)
exeCmd(cmdStr, TSTCASE)

OHS = "_ohs1"
TSTCASE = TSTCASE + 1
cmdStr=mkCmd(OHS, MACHINE)
exeCmd(cmdStr, TSTCASE)

OHS = ".ohs1"  # disappears on linux
TSTCASE = TSTCASE + 1
cmdStr=mkCmd(OHS, MACHINE)
exeCmd(cmdStr, TSTCASE)

OHS = "@ohs1"
TSTCASE = TSTCASE + 1
cmdStr=mkCmd(OHS, MACHINE)
exeCmd(cmdStr, TSTCASE)

# -------------------------------------------
# Invalid charaters

OHS = "$fakevar"
TSTCASE = TSTCASE + 1
cmdStr=mkCmd(OHS, MACHINE)
exeCmd(cmdStr, TSTCASE)

OHS = "%fakevar%"
TSTCASE = TSTCASE + 1
cmdStr=mkCmd(OHS, MACHINE)
exeCmd(cmdStr, TSTCASE)

OHS = "ohs*"
TSTCASE = TSTCASE + 1
cmdStr=mkCmd(OHS, MACHINE)
exeCmd(cmdStr, TSTCASE)

OHS = "ohs:1"
TSTCASE = TSTCASE + 1
cmdStr=mkCmd(OHS, MACHINE)
exeCmd(cmdStr, TSTCASE)

OHS = "ohs;1"
TSTCASE = TSTCASE + 1
cmdStr=mkCmd(OHS, MACHINE)
exeCmd(cmdStr, TSTCASE)

OHS = "ohs.1"
TSTCASE = TSTCASE + 1
cmdStr=mkCmd(OHS, MACHINE)
exeCmd(cmdStr, TSTCASE)

OHS = "ohs1."
TSTCASE = TSTCASE + 1
cmdStr=mkCmd(OHS, MACHINE)
exeCmd(cmdStr, TSTCASE)

OHS = "ohs=1"
TSTCASE = TSTCASE + 1
cmdStr=mkCmd(OHS, MACHINE)
exeCmd(cmdStr, TSTCASE)

OHS = "ohs1,ohs2"
TSTCASE = TSTCASE + 1
cmdStr=mkCmd(OHS, MACHINE)
exeCmd(cmdStr, TSTCASE)

# -------------------------------------------
# Spaces

OHS = " "
TSTCASE = TSTCASE + 1
cmdStr=mkCmd(OHS, MACHINE)
exeCmd(cmdStr, TSTCASE)

OHS = " ohs1"
TSTCASE = TSTCASE + 1
cmdStr=mkCmd(OHS, MACHINE)
exeCmd(cmdStr, TSTCASE)

OHS = "ohs1 "
TSTCASE = TSTCASE + 1
cmdStr=mkCmd(OHS, MACHINE)
exeCmd(cmdStr, TSTCASE)

OHS = "ohs1 ohs2"
TSTCASE = TSTCASE + 1
cmdStr=mkCmd(OHS, MACHINE)
exeCmd(cmdStr, TSTCASE)

# -------------------------------------------
# Provide path instead of name

OHS = "/this/is/a/path"
TSTCASE = TSTCASE + 1
cmdStr=mkCmd(OHS, MACHINE)
exeCmd(cmdStr, TSTCASE)

OHS = "this/is/a/path"
TSTCASE = TSTCASE + 1
cmdStr=mkCmd(OHS, MACHINE)
exeCmd(cmdStr, TSTCASE)

OHS = "c:\\work"
TSTCASE = TSTCASE + 1
cmdStr=mkCmd(OHS, MACHINE)
exeCmd(cmdStr, TSTCASE)

# -------------------------------------------
# Special cases
# Avoid logfile collisions

OHS = "AdminServer"
TSTCASE = TSTCASE + 1
cmdStr=mkCmd(OHS, MACHINE)
exeCmd(cmdStr, TSTCASE)

OHS = "AdminServerTag"
TSTCASE = TSTCASE + 1
cmdStr=mkCmd(OHS, MACHINE)
exeCmd(cmdStr, TSTCASE)

OHS = "domain_bak"
TSTCASE = TSTCASE + 1
cmdStr=mkCmd(OHS, MACHINE)
exeCmd(cmdStr, TSTCASE)

# -------------------------------------------
# Avoid internal OHS configuration collisions

OHS = "mbeans"
TSTCASE = TSTCASE + 1
cmdStr=mkCmd(OHS, MACHINE)
exeCmd(cmdStr, TSTCASE)

OHS = "instances"
TSTCASE = TSTCASE + 1
cmdStr=mkCmd(OHS, MACHINE)
exeCmd(cmdStr, TSTCASE)

# -------------------------------------------
# Check for capitalization issues

OHS = "newohs"
cmdStr="ohs_createInstance(instanceName=\'" + OHS + "\',machine=\'" + MACHINE + "\')"
executeCommand(cmdStr)

OHS = "newOHS"
TSTCASE = TSTCASE + 1
cmdStr=mkCmd(OHS, MACHINE)
exeCmd(cmdStr, TSTCASE)

OHS = "newohs"
cmdStr="ohs_deleteInstance(instanceName=\'" + OHS + "\')"
executeCommand(cmdStr)

# -------------------------------------------
# Disconnect

disconnectCommon()
printLog("Operation completed.")
