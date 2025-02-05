# Introduction
Native jget binary, created by GraalVM (for example), is picked up from this directory, by scripts in JGet/bin
Add native jget binary in this folder

# GraalVM commands to create native binary

```bash
# Download GraalVM for suitable JDK and <GraalVM>/bin to path

# Generate native binary
cd $HOME/Programs/JGet
native-image --enable-http --enable-https --enable-all-security-services -jar jget.jar -o native/jget
```
