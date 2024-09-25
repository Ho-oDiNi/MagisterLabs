#!/bin/bash

echo "MemoryType ; BlockSize ; ElementType ; BufferSize ;\
 LaunchNum ; Timer; WriteTime; AverageWriteTime;\
 WriteBandwidth; AbsError(write); RelError(write);\
 ReadTime; AverageReadTime; ReadBandwidth; AbsError(read);\
 RelError(read)" > result.csv

g++ bm.cpp -o benchmark

./benchmark -m RAM -l 5 -b 386Kb
./benchmark -m RAM -l 5 -b 1Mb
./benchmark -m RAM -l 5 -b 6Mb
./benchmark -m RAM -l 5 -b 7Mb

for ((i=1; i <= 20; i++))
do
    let m=$i*4*1024*1024
    ./benchmark -m SSD -l 5 -b $m
done

for ((i=1; i <= 20; i++))
do
    let m=$i*4*1024*1024
    ./benchmark -m flash -l 5 -b $m
done
