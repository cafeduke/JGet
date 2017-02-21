import sys
import java.lang.System as System
from java.lang import String
execfile(System.getenv('ADE_VIEW_ROOT') + '/apache/test/functional/common/wlst/ohs_common.py')

OHS_NAME = getVar("OHS_NAME")

# Connect to WLST
connectCommon()

# Get the state
cmdStr="state(\'" + OHS_NAME + "\')"
executeCommand(cmdStr)

# Exit
disconnectCommon()
