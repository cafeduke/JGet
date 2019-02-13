import sys
import java.lang.System as System
from java.lang import String
execfile(System.getenv('ADE_VIEW_ROOT') + '/apache/test/functional/common/wlst/ohs_common.py')

OHS_NAME = getVar("OHS_NAME")
TSTCASE  = 0

# --------------------------------------------
# Connect

connectCommon()

# --------------------------------------------

# Get handle from KSS (OPSS)
printLog("svc=getOpssService(\'KeyStoreService\')")
svc=getOpssService('KeyStoreService')

# Default keystore already exists
cmdStr="svc.createKeyStore(appStripe=\'OHS\',name=\'kss_default\',password=\'\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

# Keystore already exists
cmdStr="svc.createKeyStore(appStripe=\'OHS\',name=\'kss_k1\',password=\'\')"
executeCommand(cmdStr)
cmdStr="svc.generateKeyPair(appStripe=\'OHS\', name=\'kss_k1\', dn=\'cn=www.oracle.com\', keysize=\'1024\', alias=\'self-signed\', password=\'\', keypassword=\'\')"
executeCommand(cmdStr)
cmdStr="svc.createKeyStore(appStripe=\'OHS\',name=\'kss_k1\',password=\'\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

# Exporting nonexistent keystores
cmdStr="ohs_exportKeyStore(keyStoreName=\'\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="ohs_exportKeyStore(instanceName=\'\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="ohs_exportKeyStore(instanceName=\'kss\', keyStoreName=\'\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="ohs_exportKeyStore(instanceName=\'kss\', keyStoreName=\'kss_bogus\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="ohs_exportKeyStore(instanceName=\'bogus\', keyStoreName=\'\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="ohs_exportKeyStore(instanceName=\'bogus\', keyStoreName=\'k1\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

# Accidentally flip OHS Instance and KSS name
cmdStr="ohs_exportKeyStore(instanceName=\'k1\', keyStoreName=\'k1_kss\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

# Try to export a keystore that is empty
cmdStr="svc.createKeyStore(appStripe=\'OHS\',name=\'kss_test99\', password=\'\')"
executeCommand(cmdStr)

cmdStr="ohs_exportKeyStore(instanceName=\'kss\', keyStoreName=\'kss_test99\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="svc.deleteKeyStore(appStripe=\'OHS\',name=\'kss_test99\',password=\'\')"
executeCommand(cmdStr)

# Try to export a keystore that has been deleted
cmdStr="ohs_exportKeyStore(instanceName=\'kss\', keyStoreName=\'kss_test99\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

# Delete OHS instance, then try to export a keystore to it
cmdStr="shutdown(\'" + OHS_NAME + "\')"
executeCommand(cmdStr)
cmdStr="deleteOHSInstance(instanceName=\'" + OHS_NAME + "\')"
executeCommand(cmdStr)

cmdStr="ohs_exportKeyStore(instanceName=\'" + OHS_NAME + "\', keyStoreName=\'kss_k1\')"
TSTCASE=TSTCASE + 1
exeCmd(cmdStr, TSTCASE)

cmdStr="svc.deleteKeyStore(appStripe=\'OHS\',name=\'kss_k1\',password=\'\')"
executeCommand(cmdStr)

# --------------------------------------------
# Disconnect

disconnectCommon()
printLog("Operation completed.")
