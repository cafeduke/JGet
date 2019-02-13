import sys
import java.lang.System as System
from java.lang import String
execfile(System.getenv('ADE_VIEW_ROOT') + '/apache/test/functional/common/wlst/ohs_common.py')

JKS_PATH = getVar("JKS_PATH")

# --------------------------------------------
# Connect

connectCommon()

# --------------------------------------------
# Configure the AdminServer

cmdStr="serverConfig()"
executeCommand(cmdStr)
cmdStr="edit()"
executeCommand(cmdStr)
cmdStr="startEdit()"
executeCommand(cmdStr)

cmdStr="cd(\'Servers/AdminServer\')"
executeCommand(cmdStr)

cmdStr="set(\'KeyStores\', \'CustomIdentityAndJavaStandardTrust\')"
executeCommand(cmdStr)
cmdStr="set(\'CustomIdentityKeyStoreFileName\', \'" + JKS_PATH + "\')"
executeCommand(cmdStr)
cmdStr="set(\'CustomIdentityKeyStoreType\',\'JKS\')"
executeCommand(cmdStr)
cmdStr="set(\'CustomIdentityKeyStorePassPhrase\',\'welcome1\')"
executeCommand(cmdStr)

cmdStr="cd(\'SSL/AdminServer\')"
executeCommand(cmdStr)

cmdStr="set(\'Enabled\',\'True\')"
executeCommand(cmdStr)
cmdStr="set(\'ServerPrivateKeyAlias\',\'sslcert\')"
executeCommand(cmdStr)
cmdStr="set(\'ServerPrivateKeyPassPhrase\',\'welcome1\')"
executeCommand(cmdStr)

cmdStr="activate()"
executeCommand(cmdStr)

# --------------------------------------------
# Disconnect

disconnectCommon()
printLog("WLS update completed.")
