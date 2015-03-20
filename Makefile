CLASSPATH="src"
JAVAC=javac

SRCS = $(wildcard src/*/*.java)
CLASSES = $(SRCS:.java=.class)

all: $(CLASSES) sos

run: all
	CLASSPATH=${CLASSPATH} java pbsc.PbscCompiler ${FILE} out.asm

sos:
	make -C pbasic-sos

clean :
	rm -f src/*/*.class

%.class : %.java
	@if [[ -s $< ]]; then \
	echo "Compiling" $<; \
	CLASSPATH=${CLASSPATH} $(JAVAC) $<; \
	else \
	echo "Skipping file" $<; \
	touch $@; \
	fi

.PHONY: clean sos all run
