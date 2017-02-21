!include $(MAKEFILE_HOME)\common.mak

all: build

build:
  @echo "Building tiapfc scripts..."
  $(LD) /lib /OUT:
  @echo "Finished."
