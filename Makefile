CLASSPATH="src"
JAVAC=javac
sources = $(wildcard src/*/*.java)
classes = $(sources:.java=.class)

all: $(classes)

run: all
	CLASSPATH=${CLASSPATH} java pbsc.Compiler ${FILE}

clean :
	rm -f src/*.class

%.class : %.java
	CLASSPATH=${CLASSPATH} $(JAVAC) $<
