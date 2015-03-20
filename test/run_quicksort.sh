#!/bin/bash

cd ..
make run FILE=test/quicksort.pbsc
cd pbasic-sos
java sos.Sim -r 5000 -l 0 ../out.asm
rm -f out.asm
