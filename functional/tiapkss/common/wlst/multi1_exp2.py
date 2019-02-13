import sys
import java.lang.System as System
from java.lang import String
execfile(System.getenv('ADE_VIEW_ROOT') + '/apache/test/functional/common/wlst/ohs_common.py')

OHS_NAME = getVar("OHS_NAME")
KEY_NAME1 = OHS_NAME + "_k1"
KEY_NAME2 = OHS_NAME + "_k2"
KEY_NAME3 = OHS_NAME + "_k3"

TSTCASE = 0

# --------------------------------------------
# Connect

connectCommon()

# --------------------------------------------
# Multiple Keystore Handling

# Get handle from KSS (OPSS)
printLog("svc=getOpssService(\'KeyStoreService\')")
svc=getOpssService('KeyStoreService')

# K1:  Remove one cert and add another.
cmdStr="svc.deleteKeyStoreEntry(appStripe=\'OHS\',name=\'" + KEY_NAME1 + "\',alias=\'self-signed-k1.1\', password=\'\',keypassword=\'\')"
executeCommand(cmdStr)

cmdStr="svc.generateKeyPair(appStripe=\'OHS\', name=\'" + KEY_NAME1 + "\', dn=\'cn=www.oracle1.2.com\', keysize=\'1024\', alias=\'self-signed-k1.2\', password=\'\', keypassword=\'\')"
executeCommand(cmdStr)

# Do nothing to K2

# K3:  Remove two certs, add one.
cmdStr="svc.deleteKeyStoreEntry(appStripe=\'OHS\',name=\'" + KEY_NAME3 + "\',alias=\'self-signed-k3.1\', password=\'\',keypassword=\'\')"
executeCommand(cmdStr)
cmdStr="svc.deleteKeyStoreEntry(appStripe=\'OHS\',name=\'" + KEY_NAME3 + "\',alias=\'self-signed-k3.3\', password=\'\',keypassword=\'\')"
executeCommand(cmdStr)
cmdStr="svc.generateKeyPair(appStripe=\'OHS\', name=\'" + KEY_NAME3 + "\', dn=\'cn=www.oracle3.4.com\', keysize=\'1024\', alias=\'self-signed-k3.4\', password=\'\', keypassword=\'\')"
executeCommand(cmdStr)

# Verify one cert in the k1 keystore
cmdStr="svc.listKeyStoreAliases(appStripe=\'OHS\', name=\'" + KEY_NAME1 + "\', type=\'*\', password=\'\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

# Verify two certs now appear in the k2 keystore
cmdStr="svc.listKeyStoreAliases(appStripe=\'OHS\', name=\'" + KEY_NAME2 + "\', type=\'*\', password=\'\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

# Verify the three certs now appear in the k3 keystore
cmdStr="svc.listKeyStoreAliases(appStripe=\'OHS\', name=\'" + KEY_NAME3 + "\', type=\'*\', password=\'\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

# Export the k1 KSS keystore to the OHS wallet
cmdStr="ohs_exportKeyStore(instanceName=\'" + OHS_NAME + "\', keyStoreName=\'" + KEY_NAME1 + "\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

# Export the k2 KSS keystore to the OHS wallet
cmdStr="ohs_exportKeyStore(instanceName=\'" + OHS_NAME + "\', keyStoreName=\'" + KEY_NAME2 + "\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

# Export the k3 KSS keystore to the OHS wallet
cmdStr="ohs_exportKeyStore(instanceName=\'" + OHS_NAME + "\', keyStoreName=\'" + KEY_NAME3 + "\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

# --------------------------------------------
# Disconnect

disconnectCommon()
printLog("Operation completed.")
