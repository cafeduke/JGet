import sys
import java.lang.System as System
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
print "Update 1:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="editCustom()"
executeCommand(cmdStr)
cmdStr="cd(\'oracle.ohs\')"
executeCommand(cmdStr)
cmdStr="cd(\'oracle.ohs:name=" + OHS_NAME + ",type=OHSInstance\')"
executeCommand(cmdStr)

cmdStr="startEdit()"
executeCommand(cmdStr)
cmdStr="set(\'KeepAliveTimeout\', 1)"
executeCommand(cmdStr)
cmdStr="activate()"
executeCommand(cmdStr)

val=get("KeepAliveTimeout")
val=str(val)
print "KeepAliveTimeout: " + val

print "--------------------------------------------"
print "Update 2:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="startEdit()"
executeCommand(cmdStr)
cmdStr="set(\'KeepAliveTimeout\', 2)"
executeCommand(cmdStr)
cmdStr="activate()"
executeCommand(cmdStr)

val=get("KeepAliveTimeout")
val=str(val)
print "KeepAliveTimeout: " + val

print "--------------------------------------------"
print "Update 3:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="startEdit()"
executeCommand(cmdStr)
cmdStr="set(\'KeepAliveTimeout\', 3)"
executeCommand(cmdStr)
cmdStr="activate()"
executeCommand(cmdStr)

val=get("KeepAliveTimeout")
val=str(val)
print "KeepAliveTimeout: " + val

print "--------------------------------------------"
print "Update 4:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="startEdit()"
executeCommand(cmdStr)
cmdStr="set(\'KeepAliveTimeout\', 4)"
executeCommand(cmdStr)
cmdStr="activate()"
executeCommand(cmdStr)

val=get("KeepAliveTimeout")
val=str(val)
print "KeepAliveTimeout: " + val

print "--------------------------------------------"
print "Update 5:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="startEdit()"
executeCommand(cmdStr)
cmdStr="set(\'KeepAliveTimeout\', 5)"
executeCommand(cmdStr)
cmdStr="activate()"
executeCommand(cmdStr)

val=get("KeepAliveTimeout")
val=str(val)
print "KeepAliveTimeout: " + val

print "--------------------------------------------"
print "Update 6:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="startEdit()"
executeCommand(cmdStr)
cmdStr="set(\'KeepAliveTimeout\', 6)"
executeCommand(cmdStr)
cmdStr="activate()"
executeCommand(cmdStr)

val=get("KeepAliveTimeout")
val=str(val)
print "KeepAliveTimeout: " + val

print "--------------------------------------------"
print "Update 7:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="startEdit()"
executeCommand(cmdStr)
cmdStr="set(\'KeepAliveTimeout\', 7)"
executeCommand(cmdStr)
cmdStr="activate()"
executeCommand(cmdStr)

val=get("KeepAliveTimeout")
val=str(val)
print "KeepAliveTimeout: " + val

print "--------------------------------------------"
print "Update 8:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="startEdit()"
executeCommand(cmdStr)
cmdStr="set(\'KeepAliveTimeout\', 8)"
executeCommand(cmdStr)
cmdStr="activate()"
executeCommand(cmdStr)

val=get("KeepAliveTimeout")
val=str(val)
print "KeepAliveTimeout: " + val

print "--------------------------------------------"
print "Update 9:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="startEdit()"
executeCommand(cmdStr)
cmdStr="set(\'KeepAliveTimeout\', 9)"
executeCommand(cmdStr)
cmdStr="activate()"
executeCommand(cmdStr)

val=get("KeepAliveTimeout")
val=str(val)
print "KeepAliveTimeout: " + val

print "--------------------------------------------"
print "Update 10:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="startEdit()"
executeCommand(cmdStr)
cmdStr="set(\'KeepAliveTimeout\', 10)"
executeCommand(cmdStr)
cmdStr="activate()"
executeCommand(cmdStr)

val=get("KeepAliveTimeout")
val=str(val)
print "KeepAliveTimeout: " + val

print "--------------------------------------------"
print "Start:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="start(\'" + OHS_NAME + "\',\'SystemComponent\')"
executeCommand(cmdStr)
cmdStr="state(\'" + OHS_NAME + "\',\'SystemComponent\')"
executeCommand(cmdStr)

print "--------------------------------------------"
print "Update Started 11:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="startEdit()"
executeCommand(cmdStr)
cmdStr="set(\'KeepAliveTimeout\', 11)"
executeCommand(cmdStr)
cmdStr="activate()"
executeCommand(cmdStr)

val=get("KeepAliveTimeout")
val=str(val)
print "KeepAliveTimeout: " + val

print "--------------------------------------------"
print "Update Started 12:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="startEdit()"
executeCommand(cmdStr)
cmdStr="set(\'KeepAliveTimeout\', 12)"
executeCommand(cmdStr)
cmdStr="activate()"
executeCommand(cmdStr)

val=get("KeepAliveTimeout")
val=str(val)
print "KeepAliveTimeout: " + val

print "--------------------------------------------"
print "Update Started 13:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="startEdit()"
executeCommand(cmdStr)
cmdStr="set(\'KeepAliveTimeout\', 13)"
executeCommand(cmdStr)
cmdStr="activate()"
executeCommand(cmdStr)

val=get("KeepAliveTimeout")
val=str(val)
print "KeepAliveTimeout: " + val

print "--------------------------------------------"
print "Update Started 14:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="startEdit()"
executeCommand(cmdStr)
cmdStr="set(\'KeepAliveTimeout\', 14)"
executeCommand(cmdStr)
cmdStr="activate()"
executeCommand(cmdStr)

val=get("KeepAliveTimeout")
val=str(val)
print "KeepAliveTimeout: " + val

print "--------------------------------------------"
print "Update Started 15:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="startEdit()"
executeCommand(cmdStr)
cmdStr="set(\'KeepAliveTimeout\', 15)"
executeCommand(cmdStr)
cmdStr="activate()"
executeCommand(cmdStr)

val=get("KeepAliveTimeout")
val=str(val)
print "KeepAliveTimeout: " + val

print "--------------------------------------------"
print "Update Started 16:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="startEdit()"
executeCommand(cmdStr)
cmdStr="set(\'KeepAliveTimeout\', 16)"
executeCommand(cmdStr)
cmdStr="activate()"
executeCommand(cmdStr)

val=get("KeepAliveTimeout")
val=str(val)
print "KeepAliveTimeout: " + val

print "--------------------------------------------"
print "Update Started 17:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="startEdit()"
executeCommand(cmdStr)
cmdStr="set(\'KeepAliveTimeout\', 17)"
executeCommand(cmdStr)
cmdStr="activate()"
executeCommand(cmdStr)

val=get("KeepAliveTimeout")
val=str(val)
print "KeepAliveTimeout: " + val

print "--------------------------------------------"
print "Update Started 18:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="startEdit()"
executeCommand(cmdStr)
cmdStr="set(\'KeepAliveTimeout\', 18)"
executeCommand(cmdStr)
cmdStr="activate()"
executeCommand(cmdStr)

val=get("KeepAliveTimeout")
val=str(val)
print "KeepAliveTimeout: " + val

print "--------------------------------------------"
print "Update Started 19:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="startEdit()"
executeCommand(cmdStr)
cmdStr="set(\'KeepAliveTimeout\', 19)"
executeCommand(cmdStr)
cmdStr="activate()"
executeCommand(cmdStr)

val=get("KeepAliveTimeout")
val=str(val)
print "KeepAliveTimeout: " + val

print "--------------------------------------------"
print "Update Started 20:  " + OHS_NAME
print "--------------------------------------------"

cmdStr="startEdit()"
executeCommand(cmdStr)
cmdStr="set(\'KeepAliveTimeout\', 20)"
executeCommand(cmdStr)
cmdStr="activate()"
executeCommand(cmdStr)

val=get("KeepAliveTimeout")
val=str(val)
print "KeepAliveTimeout: " + val

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
