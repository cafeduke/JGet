import sys
import java.lang.System as System
from java.lang import String
execfile(System.getenv('ADE_VIEW_ROOT') + '/apache/test/functional/common/wlst/ohs_common.py')

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

cmdStr="set(\'KeyStores\', \'DemoIdentityAndDemoTrust\')"
executeCommand(cmdStr)
#cmdStr="set(\'CustomIdentityKeyStoreFileName\', \'null\')"
#executeCommand(cmdStr)
#cmdStr="set(\'CustomIdentityKeyStoreType\',\'null\')"
#executeCommand(cmdStr)
#cmdStr="set(\'CustomIdentityKeyStorePassPhrase\',\'XX\')"
#executeCommand(cmdStr)

#cmdStr="cd(\'SSL/AdminServer\')"
#executeCommand(cmdStr)

#cmdStr="set(\'Enabled\',\'XX\')"
#executeCommand(cmdStr)
#cmdStr="set(\'ServerPrivateKeyAlias\',\'XX\')"
#executeCommand(cmdStr)
#cmdStr="set(\'ServerPrivateKeyPassPhrase\',\'XX\')"
#executeCommand(cmdStr)

cmdStr="activate()"
executeCommand(cmdStr)

# --------------------------------------------
# Disconnect

disconnectCommon()
printLog("WLS update completed.")
