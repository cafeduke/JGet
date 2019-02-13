import sys
import java.lang.System as System
from java.lang import String
execfile(System.getenv('ADE_VIEW_ROOT') + '/apache/test/functional/common/wlst/ohs_common.py')

OHS_NAME = getVar("OHS_NAME")

# Connect to NM
nmConnectCommon()

# Get the state
cmdStr="nmServerStatus(serverName=\'" + OHS_NAME + "\',serverType=\'OHS\')"
executeCommand(cmdStr)

# Exit
nmDisconnectCommon()
