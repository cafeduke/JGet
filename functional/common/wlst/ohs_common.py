import sys
import java.lang.System as System
import re
import string
import os
from java.lang import String

############################################
# ohs_common.py
#
# File for common operations
############################################

# ------------------------------------------
# printLog:
# prints the given message

def printLog(message):
  sys.stdout.write(message + "\n")

# ------------------------------------------
# printMsg:
# prints the given message for debugging purposes

def printMsg(message):
  sys.stderr.write(message + "\n")

# ------------------------------------------
# getVar:
# retrieves the given variable value from the Jython command-line
# arguments, or the environment if not set as a command-line argument

def getVar(varName):
  try:
    if script_options.has_key(varName):
      return script_options[varName]
    else:
      return ""

  except NameError:
    for arg in sys.argv[0:]:
      argFlag = '--' + varName + '='

      if arg.startswith(argFlag):
        return arg[len(argFlag) : len(arg)]

    return ""

# ------------------------------------------
# executeCommand:
# execute a command and print out the return object

def executeCommand(command, expected_value=None ):
  try:
    printLog(command);
    # todo eval(command)

    value = eval(command)
    if (expected_value != None and expected_value != value):
      raise Exception(command + " failed: expected return value is " + str(expected_value) + ", got " + str(value));


  except Exception, jythonEx:
    print "JythonException:", jythonEx 
    debugCommand="dumpStack()"
    printLog(debugCommand);
    eval(debugCommand);
    # todo
    raise
  return

# -----------------------------------------
# exeCmd
# Execute given command and print output within a BEGIN-END box
# This way the output from the script can be easily parsed

def exeCmd(command,val):
    block = str(val)
    printLog("########################################")
    printLog("#### - BEGIN " + block)
    printLog(command);
    try:
      eval(command)
    except:
      (type, value, trace)=sys.exc_info()
      printLog("Error: " + str(type))
      printLog(str(value))
      # command1="dumpStack()"
      # printLog(command1)
      # eval(command1)
      # todo
      # raise

    printLog("#### - END " + block)
    printLog("########################################")
    return

# -----------------------------------------
# executeCommandWithReturn
# execute a command and return any created object

def executeCommandWithReturn(command):
  try:
    printLog(command);
    return eval(command)
  except Exception, jythonEx:
    print "JythonException:", jythonEx
    debugCommand="dumpStack()"
    printLog(debugCommand);
    eval(debugCommand);
    # todo
    raise

  return

# -----------------------------------------
# connectCommon
# connects to the mbean server

def connectCommon():
  global WLS_USER, WLS_PWD, ADMIN_HOST, ADMIN_PORT

  WLS_USER = getVar("WLS_USER")
  WLS_PWD  = getVar("WLS_PWD")
  ADMIN_HOST = getVar("ADMIN_HOST")
  ADMIN_PORT = getVar("ADMIN_PORT")

  if CONNECTED == "TRUE":
    cmdStr="connect(\"" + WLS_USER + "\", \"" + WLS_PWD + "\", \"" + ADMIN_HOST + ":" + ADMIN_PORT + "\")"
    executeCommand(cmdStr)

# -----------------------------------------
# nmConnectCommon
# connects to the Node Manager (standalone)

def nmConnectCommon():
  global WLS_USER, WLS_PWD, DOMAIN_NAME

  WLS_USER = getVar("WLS_USER")
  WLS_PWD  = getVar("WLS_PWD")
  DOMAIN_NAME = getVar("DOMAIN_NAME")

  if CONNECTED == "TRUE":
    cmdStr="nmConnect(\"" + WLS_USER + "\", \"" + WLS_PWD + "\", \"localhost\",\"5556\", \"" + DOMAIN_NAME + "\")"
    executeCommand(cmdStr)

# ------------------------------------------
# disconnectCommon
# Disconnect from the mbean server

def disconnectCommon():

  if CONNECTED == "TRUE":
    cmdStr="disconnect()"
    executeCommand(cmdStr)
    cmdStr="exit()"
    executeCommand(cmdStr)

# ------------------------------------------
# nmDisconnectCommon
# Disconnect from NodeManager

def nmDisconnectCommon():

  if CONNECTED == "TRUE":
    cmdStr="nmDisconnect()"
    executeCommand(cmdStr)

# ------------------------------------------------
# Global Variables
WLS_USER       = getVar("WLS_USER")
WLS_PWD        = getVar("WLS_PWD")
ADMIN_HOST     = getVar("ADMIN_HOST")
ADMIN_PORT     = getVar("ADMIN_PORT")
ADMIN_SSL_PORT = getVar("ADMIN_SSL_PORT")
DOMAIN_NAME    = getVar("DOMAIN_NAME")
CONNECTED = getVar("CONNECTED")

if CONNECTED == '':
  CONNECTED = "TRUE"

