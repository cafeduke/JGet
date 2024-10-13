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
- Download and install the latest version of JGet to `$HOME/Programs/JGet`

```bash
wget -q https://github.com/cafeduke/JGet/releases/latest/download/jget.zip -O /tmp/jget.zip \
   && mkdir -p $HOME/Programs \
   && unzip -q /tmp/jget.zip -d $HOME/Programs/JGet \
   && chmod 755 $HOME/Programs/JGet/bin/* \
   && rm /tmp/jget.zip
```
- Update $HOME/.profile to add `$HOME/Programs/JGet/bin` to PATH

# JGet as library

## Request URL and record headers

```java
  JGet.ArgBuilder builder = JGet.newBuilder()
      .url("https://www.google.co.in/")
      .outputHeadersToFile("file.head.out")
      .showAllHeaders()
      .quiet();
  int respCode = jget.sendRequest(builder.build())[0];
```

## Parallel requests
```java
List<String> listArg = JGet.newBuilder()
   .url("https://www.google.co.in/")
   .outputHeadersToFile("file.head.")
   .mode(MultiThreadMode.MSC)
   .threadCount(5)
   .showAllHeaders()
   .quiet()
   .build();
int respCode[] = jget.sendRequest(listArg);
```

# JGet as command line utility

# Sample HTTP/1.1 request

```bash
> jget -q -u "https://www.google.co.in" -sh -ho head.txt
-------------------------------------------------------------------------------------------------
JGet
-------------------------------------------------------------------------------------------------
[Sun, 13-Oct-2024 01:54:10 pm] Started executing
ResponseCode=200
[Sun, 13-Oct-2024 01:54:12 pm] Finished executing
[Sun, 13-Oct-2024 01:54:12 pm] Time taken = 1.1264s

> grep "Status" head.txt
Status: HTTP/1.1 200 OK
```
# Sample HTTP/2 request

```bash
> jget -q -u "https://www.google.co.in" -sh -ho head.txt -http 2
-------------------------------------------------------------------------------------------------
JGet
-------------------------------------------------------------------------------------------------
[Sun, 13-Oct-2024 02:04:47 pm] Started executing
ResponseCode=200
[Sun, 13-Oct-2024 02:04:48 pm] Finished executing
[Sun, 13-Oct-2024 02:04:48 pm] Time taken = 1.1210s

> grep "Status" head.txt                                        
Status: HTTP/2 200 OK
```
## Options
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
