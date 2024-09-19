# JGet
A Java HTTP library/tool to send HTTP/1.1 and HTTP/2 requests sequentially or in parallel. 
- JGet supports various request methods (GET, HEAD, POST, PUT, DELETE, OPTIONS). 
- JGet supports forcing given http protocol, TLS version and cipher list.
- Parallel request modes
   - JGet mode MSC (Multiple Similar Clients): Create 'n' threads each requesting the same URL
   - JGet mode MUC (Multiple Unique  Clients): Create 'n' threads one per URL read from input file

# Pre-requisite
JDK11+

# Install
- Download the latest `jget-<version>.zip` from [Releases](https://github.com/cafeduke/JGet/releases)
- Extract archive to an empty directory (say $HOME/Programs/JGet)
- Provide execute permission to `$HOME/Programs/JGet/bin/*`

```bash
wget -q https://github.com/cafeduke/JGet/releases/download/v1.0/jget-1.0.zip -O /tmp/jget.zip \
   && unzip -q /tmp/jget.zip -d $HOME/Programs/JGet \
   && chmod 755 $HOME/Programs/JGet/bin/* \
   && rm /tmp/jget.zip 
```
# Sample request

```bash
cd $HOME/Programs/JGet
> bin/jget -q -u "https://www.google.co.in" -sh -ho head.txt
-------------------------------------------------------------------------------------------------
CafeDuke JGet
-------------------------------------------------------------------------------------------------
[Thu, 22-Sep-2022 04:46:30 pm] Started executing
ResponseCode=200
[Thu, 22-Sep-2022 04:46:31 pm] Finished executing
[Thu, 22-Sep-2022 04:46:31 pm] Time taken = 1.1338s

> cat head.txt 
Status: HTTP/2 200 OK
:status: 200
accept-ranges: none
alt-svc: h3=":443"; ma=2592000,h3-29=":443"; ma=2592000,h3-Q050=":443"; ma=2592000,h3-Q046=":443"; ma=2592000,h3-Q043=":443"; ma=2592000,quic=":443"; ma=2592000; v="46,43"
cache-control: private, max-age=0
content-type: text/html; charset=ISO-8859-1
date: Thu, 22 Sep 2022 11:16:31 GMT
expires: -1
p3p: CP="This is not a P3P policy! See g.co/p3phelp for more info."
server: gws
set-cookie: 1P_JAR=2022-09-22-11; expires=Sat, 22-Oct-2022 11:16:31 GMT; path=/; domain=.google.co.in; Secure,AEC=AakniGNedjZkEyWVmUzSYLMxOkLFaQ5nkLQyCD-3x0dzCfdLYqSdUsjKaho; expires=Tue, 21-Mar-2023 11:16:31 GMT; path=/; domain=.google.co.in; Secure; HttpOnly; SameSite=lax,NID=511=OPWBmtiVTZL_0LefXZ70F49U0Bmy4_yq_z-gqJ3FeaGIMyADaDUzt2WHncEeLcwNS_PxjF52E68AHmzr9FVFS4a0iSnK4hIYy8zI5ELbZ2hcXcrPegXOmXOapxZvYp6hpmr61l11Svu_JFFFeLxicTWYFbQb-Hw5m7ggd3hPbmA; expires=Fri, 24-Mar-2023 11:16:31 GMT; path=/; domain=.google.co.in; HttpOnly
vary: Accept-Encoding
x-frame-options: SAMEORIGIN
x-xss-protection: 0
```
# Options
```bash
Usage:
java JGet
     [-v|-version]
     [-u <URL>]
     [-f <File having URLs>]
     [
        -h|-host <Host name> -p|-port <Port>
        [-f <File having URIs>]
        [-uri <URI>]
     ]
     [-login <Login> -password <Password>]
     [-proxyHost|-proxy <ProxyHost:ProxyPort> ]
     [-proxyAuth <Username:Password> ]
     [
        -ssl
        -keystore <Path to Java Key Store (JKS)>
        -storepass <Password to access JKS>
     ]
     [-http <HTTP protocol version 2|1.1> ]
     [-ciphers <cipher1>[,<cipher2>,<cipher3>...<cipherN>]]
     [-tls <tls version Eg: 1.3|1.2|1.1|1>]
     [-H|-hostHeader <Host header>]
     [-c|-cookie <Cookie header>]
     [-e|-encoding <Accept-Encoding header>]
     [-inm <If-None-Match header>]
     [-ims <If-Modified-Since header>]
     [-R|-range <Range header>]
     [-b|-browser <User-Agent header>]
     [-cert|-clientCert <SSL-Client-Cert header>]
     [-k|-keepAlive <Connection header>]
     [-s|-stdout <Standard ouput>]
     [-o|-output <Overwrite file>]
     [-a|-append <Append file>]
     [-q|-quiet <Quiet mode>]
     [-get (Use HTTP method GET)]
     [-P|-post (Use HTTP method POST)]
     [-head (Use HTTP method HEAD)]
     [-put (Use HTTP method PUT)]
     [-delete (Use HTTP method DELETE)]
     [-trace (Use HTTP method TRACE)]
     [-options (Use HTTP method OPTIONS)]
     [-pb|-postBody <Post body>]
     [-chunklen <Number of bytes each chunked request body should have> ]
     [-byteSendDelay <Time in milliseonds to sleep after sending each byte of post body> ]
     [-byteReceiveDelay <Time in milliseonds to sleep after receiving each byte of response body> ]
     [-pbf|-postBodyFile <post1>[|<post2>|<post3>...<postN>]] (Files having post body)
     [(-hdr|-header <Header name>:<Header value> )*] (Any number of occurence of header argument)
     [-rqh|-requestHeaderFile <req1>[,<req1>,<req3>...<reqN>]] (Files having request headers)
     [
        -sh|-showHeader
        [
         -sah|-showAllHeader <Show All Headers>
         -sph<h1>[,<h2>,<h3>...<hN> (Show Perticular Headers)
        ]
        -ho|-headerOutput<Filename to store response headers>
     ]
     [-rco|-respCodeOutput <Filename to store response code per request>]
     [-n|-threadCount <Number of threads>]
     [-r|-repeat <Number of sequential repeated requests per thread>]
     [-mode SC | MSC | MUC ]
     [-meta|-metaData (Record meta data. Stored in <output file>.jget.properties)]
     [-socketTimeout <Socket timeout in milliseconds for each thread> ]
     [-respBodyTimeout <Timeout after which all threads shall abort processing of response body.
                       Applicable with MSC/MUC only.>
     [-disableFollowRedirect (Do not follow redirection. Default=false) ]
     [-nonBlock (Send non-blocking request. Default=false) ]
     [-disableErrorLog (Disable logging error messages. Default=false) ]
     [-disableClientId (Do not send the OtdClientId header. Default=false)]
```
