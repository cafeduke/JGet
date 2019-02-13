import getopt
import sys

long_option = ['host=','port=','username=', 'password=', 'domain=', 'machine=', 
               'keystore=', 'storepass=', 'ms-http-port=', 'ms-ssl-port=', 'web-app-path=', 
               'alias=']

def usage ():
    print ('create_managed_server.py --host=<Admin hostname> --port=<Admin port> --username=<Admin username> --password=<Admin password> '
                                    '--domain=<Domain home> '
                                    '--machine=<Node manager machine> '
                                    '--alias=<WebLogic keystore alias>'
                                    '--keystore=<WebLogic keystore> '
                                    '--storepass=<WebLogic storepass> '
                                    '--ms-http-port=<Comma separated list if http port> '
                                    '--ms-ssl-port=<Comma separated list of ssl port> '
                                    '--web-app-path=<Path to web-app to be deployed> ')

try:
    options, args = getopt.getopt(sys.argv[1:], '', long_option)
except getopt.GetoptError:
    usage()
    sys.exit(1)

arg = {}
for opt, val in options:
    opt = opt.replace('--', '')
    arg[opt] = val
    
for curr_key in long_option:
    curr_key = curr_key.replace('=', '')
    if curr_key not in arg:
        print ('Argument --' + curr_key + " is mandatory")
        usage()
        sys.exit(1)

arg['port']         = int(arg['port'])
arg['ms-http-port'] = map(int, arg['ms-http-port'].split(','))
arg['ms-ssl-port']  = map(int, arg['ms-ssl-port'].split(','))

ms_count = len(arg['ms-http-port'])
print ('MSCount=', ms_count)
print ('Arg=', arg)
 
try:
    connect(arg['username'], arg['password'], arg['host'] + ':' + str(arg['port']))
    
    list_ms = []
    for index in range(ms_count):
     
        curr_ms = 'ManagedServer' + str(index+1)
        curr_ms_http_port = arg['ms-http-port'][index]
        curr_ms_ssl_port  = arg['ms-ssl-port'][index]
        list_ms.append(curr_ms)
        
        print ('---------------------------------------------------------------------')
        print ('Creating ' + curr_ms)
        print ('---------------------------------------------------------------------')

        edit()
        startEdit()

        print ('Create Server')        
        cd('/')
        cmo.createServer(curr_ms)
        
        print ('Configure Server')
        cd('/Servers/' + curr_ms)
        cmo.setListenAddress('slc15grf.us.oracle.com')
        cmo.setListenPort(curr_ms_http_port)
        cmo.setListenPortEnabled(true)
        cmo.setMachine(getMBean('/Machines/' + arg['machine']))
        cmo.setCluster(None)
        cmo.setKeyStores('CustomIdentityAndCustomTrust')
        cmo.setCustomIdentityKeyStoreFileName(arg['keystore'])
        cmo.setCustomIdentityKeyStoreType('jks')
        cmo.setCustomIdentityKeyStorePassPhrase(arg['storepass'])
        cmo.setCustomTrustKeyStoreFileName(arg['keystore'])
        cmo.setCustomTrustKeyStoreType('jks')
        cmo.setCustomTrustKeyStorePassPhrase(arg['storepass'])
        save()
        
        print ('Configure SSL')
        edit()
        startEdit()
        cd('/Servers/' + curr_ms + '/SSL/' + curr_ms)
        cmo.setEnabled(true)
        cmo.setListenPort(curr_ms_ssl_port)
        cmo.setUseServerCerts(true)
        cmo.setHostnameVerificationIgnored(true)
        cmo.setHostnameVerifier(None)
        cmo.setTwoWaySSLEnabled(false)
        cmo.setServerPrivateKeyAlias(arg['alias'])
        cmo.setServerPrivateKeyPassPhrase(arg['storepass'])        
        save()
        
        activate(block='true')
    
    # Deploy App in all managed servers
    target_servers = ",".join(list_ms)
    print ('---------------------------------------------------------------------')
    print ('Deploying OtdApp in ' + target_servers)
    print ('---------------------------------------------------------------------')
    print ('Deploying ' + arg['web-app-path'] + ' to ' + target_servers)
    deploy(appName='OtdApp', path=arg['web-app-path'], targets=target_servers)
    
    # Start managed servers
    for index in range(ms_count):
        curr_ms = 'ManagedServer' + str(index+1)
        print ('---------------------------------------------------------------------')
        print ('Starting ' + curr_ms)
        print ('---------------------------------------------------------------------')
        cd('/') 
        start(curr_ms,'Server')
    
except Exception:
    print ('---------------------------------------------------------------------')
    print ('Stacktrace')
    print ('---------------------------------------------------------------------')
    dumpStack()
    raise



