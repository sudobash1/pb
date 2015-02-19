CLASSPATH="src"
JAVAC=javac

all: src/pbsc/PbscCompiler.class

run: all
	CLASSPATH=${CLASSPATH} java pbsc.PbscCompiler ${FILE}

clean :
	rm -f src/*/*.class

%.class : %.java
	CLASSPATH=${CLASSPATH} $(JAVAC) $<
