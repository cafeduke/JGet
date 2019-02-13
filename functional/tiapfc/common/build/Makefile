#
# Copyright (c) 2002, 2005, Oracle. All rights reserved.  
#
# Makefile
#
#    MODIFIED    (MM/DD/YY)
#    kdclark      06/07/07 - add platform.mk for linux
#    kdclark      08/01/06 - update variable settings
#    lima         03/15/05   - 
#    schristl     12/20/02   - schristl_apache_1217
#    schristl     12/20/02   - Creation

#include $(ADE_VIEW_ROOT)/buildtools/mkfile/platform.mk
include $(ADE_VIEW_ROOT)/asg_apache/common.mk

# Solaris
ifeq ($(PLATFORM),solaris)
LIBS=-lfcgi -lnsl -lresolv -lsocket -lm
endif

# Solaris.X64
ifeq ($(PLATFORM),i86pc)
LIBS=-lfcgi -lnsl -lresolv -lsocket -lm
endif

ifeq ($(PLATFORM),linux)
LIBS=-lfcgi -lnsl  -lm
endif

ifeq ($(PLATFORM),x86_64)
LIBS=-lfcgi -lnsl  -lm -L$(SRCHOME)/buildtools/lib -lirc
endif

ifeq ($(PLATFORM),hpux)
LIBS=-lfcgi -lnsl -lm
ifeq ($(OS_TYPE),ia64hpi)
CFLAGS+=+DD64
endif
endif

ifeq ($(PLATFORM),aix)
LIBS=-lfcgi -lnsl  -lm
endif

CFLAGS+=-I$(ORACLE_HOME)/ohs/fastcgi/include\
-L$(ORACLE_HOME)/ohs/fastcgi\
-D_REENTRANT

# ifeq ($(PLATFORM),aix)
# all: aix_setup build
# else
all: build
# endif

build: tiapfc_echo1 tiapfc_echo2

# aix_setup:
#	cp -f $(SRCHOME)/buildtools/bin/oraxlc $(SRCHOME)/bin/.
#	cp -f $(SRCHOME)/buildtools/lib/xlc.cfg $(SRCHOME)/lib/.
#	chmod 755 $(SRCHOME)/bin/oraxlc
#	chmod 755 $(SRCHOME)/lib/xlc.cfg

tiapfc_echo1: tiapfc_echo1.c
	$(CC) $(CFLAGS) tiapfc_echo1.c -o tiapfc_echo1 $(LIBS)

tiapfc_echo2: tiapfc_echo2.c
	$(CC) $(CFLAGS) tiapfc_echo2.c -o tiapfc_echo2 $(LIBS)

clean: 
	rm -f tiapfc_echo1 tiapfc_echo2
