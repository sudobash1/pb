#!/bin/bash

echo There should be the same number output twice. If there are different
echo numbers output then there was a leak in the stack
echo

cd ..
make run FILE=test/stack_leak.pbsc
cd pbasic-sos
java sos.Sim -r 5000 ../out.asm
rm -f out.asm
