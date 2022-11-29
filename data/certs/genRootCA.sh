#!/bin/bash

# -------------------------------------------------------------------------------------------------
# Functions
# -------------------------------------------------------------------------------------------------
BASEDIR=$(dirname $(readlink -f ${0}))
BASENAME=$(basename ${0})

# -------------------------------------------------------------------------------------------------
# Parse Arg
# -------------------------------------------------------------------------------------------------
if [ ${#} -ne 2 ]
then
   echo "Usage: bash ${0} <HostName> <JKS Name>"
   exit 1
fi

HostName=${1}
JKSName=${2}

# -------------------------------------------------------------------------------------------------
# Variables
# -------------------------------------------------------------------------------------------------
KeyStoreDir="${BASEDIR}/${JKSName}"
Cert="${KeyStoreDir}/${JKSName}.crt"
Key="${KeyStoreDir}/${JKSName}.key"
CertReq="${KeyStoreDir}/${JKSName}.csr"
KeyStore="${KeyStoreDir}/${JKSName}.jks"
P12Store="${KeyStoreDir}/${JKSName}.p12"
Password="welcome1"
DN="CN=$HostName,OU=ST,O=Oracle,L=Bangalore,ST=Karnataka,C=IN"

# -------------------------------------------------------------------------------------------------
# Perform Operation
# -------------------------------------------------------------------------------------------------
if [[ -e ${KeyStoreDir} ]]
then
   rm -rf ${KeyStoreDir}
fi

mkdir -p ${KeyStoreDir}
echo "yes" > input.txt

set -e

# Gen  key pair
keytool -genkeypair -alias ${JKSName} -keystore ${KeyStore} -storepass ${Password} -keypass ${Password} -dname "${DN}" -keyalg RSA -keysize 2048 -validity 7300

# Convert JKS to PKCS12
keytool -importkeystore -srckeystore ${KeyStore} -srcstorepass ${Password} -destkeystore ${P12Store} -deststorepass ${Password} -deststoretype PKCS12 -srcalias ${JKSName} -destkeypass ${Password}

# Extract private key cert
openssl pkcs12 -in ${P12Store} -nodes -nocerts -out ${Key} -passin pass:${Password}

# Extract public key cert
openssl pkcs12 -in ${P12Store} -nokeys -out ${Cert} -passin pass:${Password}

# Print Cert
echo "-------------------------------------------------------------------------"
echo " JKS = ${JKSName} "
echo "-------------------------------------------------------------------------"
keytool -list -v -keystore ${KeyStore} -storepass ${Password}
