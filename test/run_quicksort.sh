#!/bin/bash

echo There should be a list of numbers in a random order followed by ten 0\'s
echo and finally the same list of numbers in sorted order.
echo

cd ..
make run FILE=test/quicksort.pbsc
cd pbasic-sos
java sos.Sim -r 5000 -l 0 ../out.asm
rm -f out.asm
