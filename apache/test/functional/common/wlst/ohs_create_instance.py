import sys
import java.lang.System as System
from java.lang import String
execfile(System.getenv('ADE_VIEW_ROOT') + '/apache/test/functional/common/wlst/ohs_common.py')

OHS_NAME         = getVar("OHS_NAME")
MACHINE_NAME     = getVar("MACHINE_NAME")
OHS_LISTEN_PORT  = getVar("OHS_LISTEN_PORT")
OHS_SSL_PORT     = getVar("OHS_SSL_PORT")
OHS_ADMIN_PORT   = getVar("OHS_ADMIN_PORT")
USE_OLD          = getVar("USE_OLD")

# Connect to WLST
connectCommon()

# Create the OHS instance
printLog("*** Creating OHS instance: " + OHS_NAME + " ***")

if USE_OLD:
  # Use the older WLST commands
  cmdStr="editCustom()"
  executeCommand(cmdStr)
  ls()
  cmdStr="cd(\"oracle.ohs\")"
  executeCommand(cmdStr)
  ls()
  cmdStr="cd(\"oracle.ohs:type=OHSSystemComponent,name=OHSInstanceManager\")"
  executeCommand(cmdStr)
  params=jarray.array([java.lang.String(OHS_NAME), java.lang.String(MACHINE_NAME)], java.lang.Object)
  sig=jarray.array(["java.lang.String", "java.lang.String"], java.lang.String)
  cmdStr="startEdit()"
  executeCommand(cmdStr)
  cmdStr="invoke(\'createInstance\', params, sig)"
  executeCommand(cmdStr)
  cmdStr="activate()"
  executeCommand(cmdStr)
  cmdStr="cd(\"../..\")"
  executeCommand(cmdStr)

else:
  # Use the custom script
  cmdStr="ohs_createInstance(instanceName=\'" + OHS_NAME + "\',machine=\'" + MACHINE_NAME + "\'"
  # If any ports provided, include them
  if OHS_LISTEN_PORT:
    cmdStr = cmdStr + ", listenPort=" + OHS_LISTEN_PORT
  if OHS_SSL_PORT:
    cmdStr = cmdStr + ", sslPort=" + OHS_SSL_PORT
  if OHS_ADMIN_PORT:
    cmdStr = cmdStr + ", adminPort=" + OHS_ADMIN_PORT
  cmdStr = cmdStr + ")"
  
  executeCommand(cmdStr)

# Exit
disconnectCommon()
