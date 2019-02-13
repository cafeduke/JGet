import sys
import java.lang.System as System
from java.lang import String
execfile(System.getenv('ADE_VIEW_ROOT') + '/apache/test/functional/common/wlst/ohs_common.py')

OHS_NAME = getVar("OHS_NAME")
KEY_NAME1 = OHS_NAME + "_k1"
KEY_NAME2 = OHS_NAME + "_k2"
KEY_NAME3 = OHS_NAME + "_k3"

# --------------------------------------------
# Connect

connectCommon()

# --------------------------------------------

# Get handle from KSS (OPSS)
printLog("svc=getOpssService(\'KeyStoreService\')")
svc=getOpssService('KeyStoreService')

cmdStr="svc.deleteKeyStore(appStripe=\'OHS\', name=\'" + KEY_NAME1 + "\', password=\'\')"
executeCommand(cmdStr)
cmdStr="svc.deleteKeyStore(appStripe=\'OHS\', name=\'" + KEY_NAME2 + "\', password=\'\')"
executeCommand(cmdStr)
cmdStr="svc.deleteKeyStore(appStripe=\'OHS\', name=\'" + KEY_NAME3 + "\', password=\'\')"
executeCommand(cmdStr)

# --------------------------------------------
# Disconnect

disconnectCommon()
printLog("Operation completed.")
