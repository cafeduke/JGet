# JReq
A Java HTTP library/tool to send HTTP/1.1 and HTTP/2 reqeusts

# Pre-requisite
JDK15+

# Install
- Download the latest `jreq-<version>.zip` from [Releases](https://github.com/cafeduke/JReq/releases)
- Extract archive to an empty directory (say $HOME/Programs/JReq)
- Provide execute permission to `$HOME/Programs/JReq/bin/*`

```bash
wget -q https://github.com/cafeduke/JReq/releases/download/v3.0/jreq-3.0.zip -O /tmp/jreq.zip \
   && unzip -q /tmp/jreq.zip -d $HOME/Programs/JReq \
   && chmod 755 $HOME/Programs/JReq/bin/* \
   && rm /tmp/jreq.zip 
```
# Sample request

```bash
cd $HOME/Programs/JReq
> bin/jreq -q -u "https://www.google.co.in" -sh -ho head.txt
-------------------------------------------------------------------------------------------------
CafeDuke JReq
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
