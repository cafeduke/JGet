import sys
import java.lang.System as System
from java.lang import String
execfile(System.getenv('ADE_VIEW_ROOT') + '/apache/test/functional/common/wlst/ohs_common.py')

OHS_NAME = getVar("OHS_NAME")
KEY_NAME = OHS_NAME + "_k1"

# --------------------------------------------
# Connect

connectCommon()

# --------------------------------------------

# Get handle from KSS (OPSS)
printLog("svc=getOpssService(\'KeyStoreService\')")
svc=getOpssService('KeyStoreService')

cmdStr="svc.deleteKeyStore(appStripe=\'OHS\', name=\'" + KEY_NAME + "\', password=\'\')"
executeCommand(cmdStr)

# --------------------------------------------
# Disconnect

disconnectCommon()
printLog("Operation completed.")
