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
# JGet native binary using GraalVM
This section details the steps to optionally generate a native binary from jget.jar using [GraalVM](https://www.graalvm.org/). If a native binary exists and is placed in `JGet/native` then the JGet scripts shall use the same for a faster request/response cycle.

- Download and install [GraalVM](https://www.graalvm.org/downloads/)

- Generate native binary

```bash
# Add GraalVM software binaries to PATH
# Verify version
native-image --version
native-image 21.0.6 2025-01-21
GraalVM Runtime Environment Oracle GraalVM 21.0.6+8.1 (build 21.0.6+8-LTS-jvmci-23.1-b55)
Substrate VM Oracle GraalVM 21.0.6+8.1 (build 21.0.6+8-LTS, serial gc, compressed references)

# Change to JGet install directory
cd $HOME/Programs/JGet

# Generate native image
native-image --enable-http --enable-https --enable-all-security-services -jar jget.jar -o native/jget
```

#  JGet as command line utility

## HTTP/1.1 request

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
## HTTP/2 request

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
## HTTP/1.1 request using native binary

```bash
> jget -q -u "https://www.google.co.in" -sh -ho head.txt
-------------------------------------------------------------------------------------------------
JGet Native
-------------------------------------------------------------------------------------------------
[Wed, 05-Feb-2025 08:03:53 pm] Started executing
ResponseCode=200
[Wed, 05-Feb-2025 08:03:53 pm] Finished executing
[Wed, 05-Feb-2025 08:03:53 pm] Time taken = 0.301s

```

## HTTP/2 request using native binary

```bash
> jget -q -u "https://www.google.co.in" -sh -ho head.txt -http 2
-------------------------------------------------------------------------------------------------
JGet Native
-------------------------------------------------------------------------------------------------
[Wed, 05-Feb-2025 08:04:36 pm] Started executing
ResponseCode=200
[Wed, 05-Feb-2025 08:04:36 pm] Finished executing
[Wed, 05-Feb-2025 08:04:36 pm] Time taken = 0.251s
```

## HTTP/2 requests in parallel

```bash
# Send 10 requests in parallel and caputure reponse headers
> jget -q -http 2 -u "https://www.google.co.in" -mode MSC -n 10 -sh -sah -ho head.
-------------------------------------------------------------------------------------------------
JGet
-------------------------------------------------------------------------------------------------
[Mon, 18-Nov-2024 11:59:23 am] Started executing
1-10=200
[Mon, 18-Nov-2024 11:59:25 am] Finished executing
[Mon, 18-Nov-2024 11:59:25 am] Time taken = 1.1305s

# List header files
> ls head.*
head.0001.out  head.0002.out  head.0003.out  head.0004.out  head.0005.out  head.0006.out  head.0007.out  head.0008.out  head.0009.out  head.0010.out

# Look for HTTP/2 in reponse header files
> grep 'Status: HTTP/2' head.* | wc -l
10
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
     [-pbf|-postBodyFile <post1>[,<post2>,<post3>...<postN>]] (Files having post body)
     [(-hdr|-header <Header name>:<Header value> )*] (Any number of occurence of header argument)
     [-rqh|-requestHeaderFile <req1>[,<req1>,<req3>...<reqN>]] (Files having request headers)
     [-sni <sni1>[,<sni2>,<sni3>...<sniN>]] (Server Name Indicator host names)
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
