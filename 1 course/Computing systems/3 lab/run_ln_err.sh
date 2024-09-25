#!/bin/bash

echo "MemoryType ; BlockSize ; ElementType ; BufferSize ;\
 LaunchNum ; Timer; WriteTime; AverageWriteTime;\
 WriteBandwidth; AbsError(write); RelError(write);\
 ReadTime; AverageReadTime; ReadBandwidth; AbsError(read);\
 RelError(read)" > result_ln_err.csv

g++ bm_ln_err.cpp -o benchmark

./benchmark -m RAM -l 5 -b 3Mb
./benchmark -m RAM -l 15 -b 3Mb
./benchmark -m RAM -l 30 -b 3Mb

./benchmark -m SSD -l 5 -b 40Mb
./benchmark -m SSD -l 15 -b 40Mb
./benchmark -m SSD -l 30 -b 40Mb

./benchmark -m flash -l 5 -b 40Mb
./benchmark -m flash -l 15 -b 40Mb
./benchmark -m flash -l 30 -b 40Mb
