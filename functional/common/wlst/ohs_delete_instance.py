import sys
import java.lang.System as System
from java.lang import String
execfile(System.getenv('ADE_VIEW_ROOT') + '/apache/test/functional/common/wlst/ohs_common.py')

OHS_NAME = getVar("OHS_NAME")
USE_OLD  = getVar("USE_OLD")
DELETE   = getVar("DELETE")

# Connect to WLST
connectCommon()

# Delete the OHS Instance
printLog("*** Removing OHS instance: " + OHS_NAME + " ***")

# If forcing delete, shut instance down if running...
if DELETE:
  cmdStr="domainRuntime()"
  executeCommand(cmdStr)
  cmdStr="cd(\"SystemComponentLifeCycleRuntimes\")"
  executeCommand(cmdStr)
  cmdStr="cd(\"" + OHS_NAME + "\")"
  executeCommand(cmdStr)
  val=get("State")
  printLog("OHS State: " + val)
  cmdStr="editCustom()"
  executeCommand(cmdStr)
  if val == 'RUNNING':
    printLog("Shutting down " + OHS_NAME + "...")
    cmdStr="shutdown(\"" + OHS_NAME + "\")"
    executeCommand(cmdStr)


if USE_OLD:
  # Use older WLST commands
  cmdStr="editCustom()"
  executeCommand(cmdStr)
  ls()
  cmdStr="cd(\"oracle.ohs\")"
  executeCommand(cmdStr)
  ls()
  cmdStr="cd(\"oracle.ohs:type=OHSSystemComponent,name=OHSInstanceManager\")"
  executeCommand(cmdStr)
  params=jarray.array([java.lang.String(OHS_NAME)], java.lang.Object)
  sig=jarray.array(["java.lang.String"], java.lang.String)
  cmdStr="startEdit()"
  executeCommand(cmdStr)
  cmdStr="invoke(\'removeInstance\', params, sig)"
  executeCommand(cmdStr)
  cmdStr="activate()"
  executeCommand(cmdStr)
  cmdStr="cd(\"../..\")"
  executeCommand(cmdStr)

else:
  # Use the custom script
  cmdStr="ohs_deleteInstance(instanceName=\'" + OHS_NAME + "\')"
  executeCommand(cmdStr)


# Exit
disconnectCommon()
