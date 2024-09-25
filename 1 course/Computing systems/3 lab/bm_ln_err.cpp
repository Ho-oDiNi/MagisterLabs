#include <iostream>
#include <fstream>
#include <cstring>
#include <string>
#include <ctime>
#include <cmath>
#include <Windows.h>

// L1 386 Kb
// L2 1 Mb
// L3 6 Mb

//clock_gettime in windows//
LARGE_INTEGER getFILETIMEoffset()
{
    SYSTEMTIME s;
    FILETIME f;
    LARGE_INTEGER t;

    s.wYear = 1970;
    s.wMonth = 1;
    s.wDay = 1;
    s.wHour = 0;
    s.wMinute = 0;
    s.wSecond = 0;
    s.wMilliseconds = 0;
    SystemTimeToFileTime(&s, &f);
    t.QuadPart = f.dwHighDateTime;
    t.QuadPart <<= 32;
    t.QuadPart |= f.dwLowDateTime;
    return (t);
}

int clock_gettime(int X, struct timeval *tv)
{
    LARGE_INTEGER           t;
    FILETIME            f;
    double                  microseconds;
    static LARGE_INTEGER    offset;
    static double           frequencyToMicroseconds;
    static int              initialized = 0;
    static BOOL             usePerformanceCounter = 0;

    if (!initialized) {
        LARGE_INTEGER performanceFrequency;
        initialized = 1;
        usePerformanceCounter = QueryPerformanceFrequency(&performanceFrequency);
        if (usePerformanceCounter) {
            QueryPerformanceCounter(&offset);
            frequencyToMicroseconds = (double)performanceFrequency.QuadPart / 1000000.;
        } else {
            offset = getFILETIMEoffset();
            frequencyToMicroseconds = 10.;
        }
    }
    if (usePerformanceCounter) QueryPerformanceCounter(&t);
    else {
        GetSystemTimeAsFileTime(&f);
        t.QuadPart = f.dwHighDateTime;
        t.QuadPart <<= 32;
        t.QuadPart |= f.dwLowDateTime;
    }

    t.QuadPart -= offset.QuadPart;
    microseconds = (double)t.QuadPart / frequencyToMicroseconds;
    t.QuadPart = microseconds;
    tv->tv_sec = t.QuadPart / 1000000;
    tv->tv_usec = t.QuadPart % 1000000;
    return (0);
}
//clock_gettime in windows//

typedef unsigned int uint;

#define NANOS_IN_SEC 1000000000

double* test_ram_w(uint block_size, uint launch_count)
{
    srand(time(0));
    double* result = new double[launch_count];
    timeval begin, end; 

    for (int launch = 0; launch < launch_count; launch++)
    {
        uint arr_size = block_size / sizeof(uint);
        uint* initial_arr = new uint[arr_size];
        uint* test_arr = new uint[arr_size];

        for (uint i = 0; i < arr_size; i++) initial_arr[i] = rand() % 100;


        clock_gettime (0, &begin);
        for (uint i = 0; i < arr_size; i++) test_arr[i] = initial_arr[i];
        clock_gettime (0, &end);

        //std::cout << "sec: " << end.tv_sec - begin.tv_sec << '\n';
        //std::cout << "nanosec: " << end.tv_nsec - begin.tv_nsec << '\n';
        result[launch] = (double)(end.tv_sec - begin.tv_sec) / NANOS_IN_SEC;
        std::cout << result[launch] << '\n';
        delete test_arr;
    }

    return result;
}

double* test_ssd_w(uint block_size, uint launch_count)
{
    srand(time(0));
    uint arr_size = block_size / sizeof(uint);
    double* result = new double[launch_count];
    timeval begin, end; 

    for (int launch = 0; launch < launch_count; launch++)
    {
        double duration = 0;
        
        std::ofstream out("mem_test", std::ios::binary | std::ios::out);
        
        clock_gettime (0, &begin);
        for (uint i = 0; i < arr_size; i++)
        {
            uint randval = rand() % 100;
            out.write((char*) &randval, sizeof(uint));
        }   
        clock_gettime (0, &end);

        out.close();

        if (end.tv_sec - begin.tv_sec > 0) 
            duration = (double)((end.tv_sec - begin.tv_sec) * NANOS_IN_SEC + end.tv_usec - begin.tv_usec) / NANOS_IN_SEC;
        else 
            duration = (double)(end.tv_usec - begin.tv_usec) / NANOS_IN_SEC;
        
        std::cout << "duration: " << duration << '\n';
        // std::cout << "sec: " << end.tv_sec - begin.tv_sec << '\n';
        // std::cout << "nanosec: " << end.tv_nsec - begin.tv_nsec << '\n';
        result[launch] = duration;
    }

    return result;
}

double* test_flash_w(uint block_size, uint launch_count)
{
    srand(time(0));
    uint arr_size = block_size / sizeof(uint);
    double* result = new double[launch_count];
    timeval begin, end; 

    for (int launch = 0; launch < launch_count; launch++)
    {
        double duration = 0;
        
        std::ofstream out("/media/zer0chance/Transcend/mem_test", std::ios::binary | std::ios::out);
        
        clock_gettime (0, &begin);
        for (uint i = 0; i < arr_size; i++)
        {
            uint randval = rand() % 100;
            out.write((char*) &randval, sizeof(uint));
        }   
        clock_gettime (0, &end);

        out.close();

        if (end.tv_sec - begin.tv_sec > 0) 
            duration = (double)((end.tv_sec - begin.tv_sec) * NANOS_IN_SEC + end.tv_usec - begin.tv_usec) / NANOS_IN_SEC;
        else 
            duration = (double)(end.tv_usec - begin.tv_usec) / NANOS_IN_SEC;
        
        std::cout << "duration: " << duration << '\n';
        // std::cout << "sec: " << end.tv_sec - begin.tv_sec << '\n';
        // std::cout << "nanosec: " << end.tv_nsec - begin.tv_nsec << '\n';
        result[launch] = duration;
    }

    return result;
}

double* test_ram_r(uint block_size, uint launch_count)
{
    srand(time(0));
    double* result = new double[launch_count];
    timeval begin, end; 

    for (int launch = 0; launch < launch_count; launch++)
    {
        uint arr_size = block_size / sizeof(uint);
        uint* test_arr = new uint[arr_size];
        uint* initial_arr = new uint[arr_size];

        for (uint i = 0; i < arr_size; i++) initial_arr[i] = rand() % 100;

        clock_gettime (0, &begin);
        for (uint i = 0; i < arr_size; i++) test_arr[i] = initial_arr[i];
        clock_gettime (0, &end);

        //std::cout << "sec: " << end.tv_sec - begin.tv_sec << '\n';
        //std::cout << "nanosec: " << end.tv_nsec - begin.tv_nsec << '\n';
        result[launch] = (double)(end.tv_usec - begin.tv_usec) / NANOS_IN_SEC;
        std::cout << result[launch] << '\n';
        delete initial_arr;
    }

    return result;
}

double* test_ssd_r(uint block_size, uint launch_count)
{
    srand(time(0));
    uint arr_size = block_size / sizeof(uint);
    double* result = new double[launch_count];
    timeval begin, end; 

    for (int launch = 0; launch < launch_count; launch++)
    {
        double duration = 0;
        
        std::ifstream ifs("mem_test", std::ios::binary | std::ios::in);
        
        clock_gettime (0, &begin);
        for (uint i = 0; i < arr_size; i++)
        {
            uint val;
            ifs.read((char*) &val, sizeof(uint));
        }   
        clock_gettime (0, &end);

        ifs.close();

        if (end.tv_sec - begin.tv_sec > 0) 
            duration = (double)((end.tv_sec - begin.tv_sec) * NANOS_IN_SEC + end.tv_usec - begin.tv_usec) / NANOS_IN_SEC;
        else 
            duration = (double)(end.tv_usec - begin.tv_usec) / NANOS_IN_SEC;
        
        std::cout << "duration: " << duration << '\n';
        // std::cout << "sec: " << end.tv_sec - begin.tv_sec << '\n';
        // std::cout << "nanosec: " << end.tv_nsec - begin.tv_nsec << '\n';
        result[launch] = duration;
    }

    return result;
}

double* test_flash_r(uint block_size, uint launch_count)
{
    srand(time(0));
    uint arr_size = block_size / sizeof(uint);
    double* result = new double[launch_count];
    timeval begin, end; 

    for (int launch = 0; launch < launch_count; launch++)
    {
        double duration = 0;
        
        std::ifstream ifs("/media/zer0chance/Transcend/mem_test", std::ios::binary | std::ios::in);
        
        clock_gettime (0, &begin);
        for (uint i = 0; i < arr_size; i++)
        {
            uint val;
            ifs.read((char*) &val, sizeof(uint));
        }   
        clock_gettime (0, &end);

        ifs.close();

        if (end.tv_sec - begin.tv_sec > 0) 
            duration = (double)((end.tv_sec - begin.tv_sec) * NANOS_IN_SEC + end.tv_usec - begin.tv_usec) / NANOS_IN_SEC;
        else 
            duration = (double)(end.tv_usec - begin.tv_usec) / NANOS_IN_SEC;
        
        std::cout << "duration: " << duration << '\n';
        // std::cout << "sec: " << end.tv_sec - begin.tv_sec << '\n';
        // std::cout << "nanosec: " << end.tv_nsec - begin.tv_nsec << '\n';
        result[launch] = duration;
    }

    return result;
}

inline double count_avgTime(double* result, uint launch_count) 
{
    double sum = 0;

    for (int i = 0; i < launch_count; i++)
        sum += result[i];

    return sum / launch_count;    
}

inline double count_absErr(double* result, uint launch_count, double avgWrtTime)
{
    double max = fabs(result[0] - avgWrtTime);

    for (int i = 1; i < launch_count; i++)
        if (fabs(result[i] - avgWrtTime) > max) 
            max = fabs(result[i] - avgWrtTime);

    return max;
}

int main(int argc, char** argv)
{
    std::string mem_type("");
    uint block_size(0);
    uint launch_count(0);

    for (int i = 1; i < argc; i++)
    {
        if (!strcmp(argv[i], "-m") || !strcmp(argv[i], "--memory-type")) {
            if(i + 1 < argc) {
                mem_type = argv[i + 1];
                i++;
            }
            else {
                std::cout << "Memory type specified incorrectly\n";
                return 1;
            }
        } else if (!strcmp(argv[i], "-b") || !strcmp(argv[i], "--block-size")) {
            if(i + 1 < argc) {
                if (argv[i + 1][strlen(argv[i + 1]) - 1] != 'b') {   // not Mb or Kb -> Byte
                    block_size = strtol(argv[i + 1], NULL, 10);
                } 
                else if (argv[i + 1][strlen(argv[i + 1]) - 2] == 'K') {  // Kb
                    block_size = strtol(argv[i + 1], NULL, 10) * 1024;
                }
                else if (argv[i + 1][strlen(argv[i + 1]) - 2] == 'M') {  // Mb
                    block_size = strtol(argv[i + 1], NULL, 10) * 1024 * 1024;
                } else {
                    std::cout << "Block size specified incorrectly\n";
                    return 1;
                }
                i++;
            }
            else {
                std::cout << "Block size specified incorrectly\n";
                return 1;
            }
        } else if (!strcmp(argv[i], "-l") || !strcmp(argv[i], "--launch-count")) {
            if(i + 1 < argc) {
                launch_count = strtol(argv[i + 1], NULL, 10);
                i++;
            } else {
                std::cout << "Launch count specified incorrectly\n";
                return 1;
            }
        } else {
            std::cout << "Unknown option: " << argv[i] << '\n';
            return 1;
        }   
    }

    if (launch_count == 0) {
        std::cout << "Launch count is not specified\n";
        return 1;
    }
    if (block_size == 0) {
        std::cout << "Block size is not specified\n";
        return 1;
    }
    if (mem_type == "") {
        std::cout << "Memory type is not specified\n";
        return 1;
    }

    double* result_w = nullptr;
    double* result_r = nullptr;

    if (mem_type == "RAM") {
        result_w = test_ram_w(block_size, launch_count);
        result_r = test_ram_r(block_size, launch_count);
    } else if (mem_type == "HDD" || mem_type == "SSD") {
        result_w = test_ssd_w(block_size, launch_count);
        result_r = test_ssd_r(block_size, launch_count);
    } else if (mem_type == "flash") {
        result_w = test_flash_w(block_size, launch_count);
        result_r = test_flash_r(block_size, launch_count);
    } else {
        std::cout << "Unknown memory type\n";
        return 1;
    }

    std::ofstream out("result_ln_err.csv", std::ios::app);

    double avgWrtTime = count_avgTime(result_w, launch_count);
    double absErr_w = count_absErr(result_w, launch_count, avgWrtTime);
    double realErr_w = (absErr_w / avgWrtTime) * 100;
    
    out << mem_type << ';' << block_size << ";int;" << block_size << ';'
        << launch_count << ";clock_gettime();" << result_w[launch_count - 1]
        << ';' << avgWrtTime << ';' << (block_size / avgWrtTime) / 1000000
        << ';' << absErr_w << ';' << realErr_w << ';';

    double avgReadTime = count_avgTime(result_r, launch_count);
    double absErr_r = count_absErr(result_r, launch_count, avgReadTime);
    double realErr_r = (absErr_r / avgReadTime) * 100;
    
    out << result_r[launch_count - 1] << ';' << avgReadTime << ';' 
        << (block_size / avgReadTime) / 1000000 << ';' << absErr_r << ';' 
        << realErr_r << '\n';

    delete result_w;
    delete result_r;
    out.close();
}