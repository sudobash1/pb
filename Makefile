
JAVAC=javac
sources = $(wildcard pbsc/*.java)
classes = $(sources:.java=.class)

all: $(classes)

run: all
	java pbsc.Compiler ${FILE}

clean :
	rm -f src/*.class

%.class : %.java
	$(JAVAC) $<
