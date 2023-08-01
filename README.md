# JGet
A Java HTTP library/tool to send HTTP/1.1 and HTTP/2 reqeusts

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
