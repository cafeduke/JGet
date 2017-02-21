import sys
import java.lang.System as System
from java.lang import String
execfile(System.getenv('ADE_VIEW_ROOT') + '/apache/test/functional/common/wlst/ohs_common.py')

OHS_NAME = getVar("OHS_NAME")

# Connect to WLST
nmConnectCommon()

# Capture state
cmdStr="nmServerStatus(serverType=\'OHS\', serverName=\'" + OHS_NAME + "\')"
executeCommand(cmdStr)

# Shutdown the OHS instance
cmdStr="nmKill(serverType=\'OHS\', serverName=\'" + OHS_NAME + "\')"
executeCommand(cmdStr)

# Capture state
cmdStr="nmServerStatus(serverType=\'OHS\', serverName=\'" + OHS_NAME + "\')"
executeCommand(cmdStr)

# Disconnect
nmDisconnectCommon()
printLog("Operation completed.")
