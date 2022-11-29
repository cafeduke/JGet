#!/bin/bash

# -------------------------------------------------------------------------------------------------
# Functions
# -------------------------------------------------------------------------------------------------
BASEDIR=$(dirname $(readlink -f ${0}))
BASENAME=$(basename ${0})

function runCmd {
   cmd="${@}"
   echo "Executing: ${cmd}"
   eval "${cmd}"
}

function heading {
  echo ""
  echo "---------------------------------------------------------------------------------------------------"
  for mesg in "$@"
  do
    echo "${mesg}"
  done
  echo "---------------------------------------------------------------------------------------------------"
}

# -------------------------------------------------------------------------------------------------
# Parse Arg
# -------------------------------------------------------------------------------------------------
if [ ${#} -ne 3 -a ${#} -ne 4 ]
then
   echo "Usage: bash ${0} <HostName> <JKS Name> <CA to sign> [<Comma separted list of CAs to trust>]"
   exit 1
fi

if [ ${#} -ge 3 ]
then
   HostName=${1}
   JKSName=${2}
   CAName=${3}
fi

CATrustCSV=""
if [ ${#} -eq 4 ]
then
   CATrustCSV=${4}
fi

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

# CA Properties
RootDir="${BASEDIR}/${CAName}"
RootDN="CN=${CAName},OU=ST,O=Oracle,L=Bangalore,ST=Karnataka,C=IN"
RootCert="${BASEDIR}/${CAName}/${CAName}.crt"
RootKey="${BASEDIR}/${CAName}/${CAName}.key"

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

heading "Gen  key pair"
runCmd keytool -genkeypair -alias ${JKSName} -keystore ${KeyStore} -storepass ${Password} -keypass ${Password} -dname "${DN}" -keyalg RSA -keysize 2048 -validity 7300

heading "Convert JKS to PKCS12"
runCmd keytool -importkeystore -srckeystore ${KeyStore} -srcstorepass ${Password} -destkeystore ${P12Store} -deststorepass ${Password} -deststoretype PKCS12 -srcalias ${JKSName} -destkeypass ${Password}

heading "Create cert request"
runCmd keytool -certreq -alias ${JKSName} -keystore ${KeyStore} -storepass ${Password} -file ${CertReq}

heading "Sign cert request using ${CAName}"
runCmd openssl x509 -req -in ${CertReq} -days 7300 -CA ${RootCert} -CAkey ${RootKey} -CAcreateserial -out ${Cert} -passin pass:${Password}

if [ ! -z "${CATrustCSV}" ]
then

   heading " Install trust list ${CATrustCSV} "

   for currCA in `echo ${CATrustCSV} | tr -s ',' ' '`
   do
      currCACert="${BASEDIR}/${currCA}/${currCA}.crt"
      runCmd keytool -importcert -trustcacerts -alias ${currCA} -keystore ${KeyStore} -storepass ${Password} -file ${currCACert} "<" input.txt
   done

fi

heading "Import Root cert as trusted certificate"
runCmd keytool -importcert -trustcacerts -alias ${CAName} -keystore ${KeyStore} -storepass ${Password} -file ${RootCert} "<" input.txt

heading "Install signed cert"
runCmd keytool -importcert -trustcacerts -alias ${JKSName} -keystore ${KeyStore} -storepass ${Password} -file ${Cert} "<" input.txt

heading "Extract private key"
runCmd openssl pkcs12 -in ${P12Store} -nodes -nocerts -out ${Key} -passin pass:${Password}

heading "Extract Signed Cert"
openssl pkcs12 -in ${P12Store} -nokeys -out ${Cert} -passin pass:${Password}

heading "Certificate ${JKSName}"
runCmd keytool -list -v -keystore ${KeyStore} -storepass ${Password}
