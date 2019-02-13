import sys
import java.lang.System as System
from java.lang import String
execfile(System.getenv('ADE_VIEW_ROOT') + '/apache/test/functional/common/wlst/ohs_common.py')

OHS_NAME = getVar("OHS_NAME")
KEY_NAME = OHS_NAME + "_k1"
TSTCASE  = 0

# --------------------------------------------
# Connect

connectCommon()

# --------------------------------------------
# Single keystore handling
# --------------------------------------------

# Get handle from KSS (OPSS)
printLog("svc=getOpssService(\'KeyStoreService\')")
svc=getOpssService('KeyStoreService')

# Delete two certs from the k1 KSS
cmdStr="svc.deleteKeyStoreEntry(appStripe=\'OHS\',name=\'" + KEY_NAME + "\',alias=\'self-signed1\', password=\'\',keypassword=\'\')"
executeCommand(cmdStr)
cmdStr="svc.deleteKeyStoreEntry(appStripe=\'OHS\',name=\'" + KEY_NAME + "\',alias=\'self-signed3\', password=\'\',keypassword=\'\')"
executeCommand(cmdStr)

# Verify only one cert now appears in the k1 keystore
cmdStr="svc.listKeyStoreAliases(appStripe=\'OHS\', name=\'" + KEY_NAME + "\', type=\'*\', password=\'\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

# Export the k1 KSS keystore to the OHS wallet
cmdStr="ohs_exportKeyStore(instanceName=\'" + OHS_NAME + "\', keyStoreName=\'" + KEY_NAME + "\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

# --------------------------------------------
# Disconnect

disconnectCommon()
printLog("Operation completed.")
