#include <iostream>
#include <time.h>
#include <fstream> 
#include <math.h>

#define eps 0.00000000000001
#define N 100000
#define M 10

using namespace std;

double exp_(const int x) 
{
	double s = 1;  
	double n = 1; 
	double a = 1; 
	while (1)     
	{
		a = a * x / n;  
		if (fabs(a) <= eps) break;
		s = s + a;    
		n++;       
	}
	return s; 
}

double cos_(const int x) 
{
	double s = 0;  
	double n = 1; 
	double a = 1; 
	while (1)     
	{
		s = s + a; 
		a = a * (-1)*x*x/((2 * n - 1) * (2 * n));  
		if (fabs(a) <= eps) break;   
		n++;       
	}
	return s; 
}

double ln_(const int x) 
{
	double s = 0;  
	double n = 1; 
	double a = (x-1)/x; 
	while (1)     
	{
		s = s + a; 
		a = a * n * (x - 1)/((n + 1)*x); 
		if (fabs(a) <= eps) break;   
		n++;       
	}
	return s; 
}

void Clock_exp(const int x, double &absolute, double &relative, double &result);
void Clock_cos(const int x, double &absolute, double &relative, double &result);
void Clock_ln(const int x, double &absolute, double &relative, double &result);

int main()
{
 	const int x =5;
	cout.precision(15); 
	
	double absolute_exp = 0;
	double relative_exp = 0;
	double result_exp = 0;
	double absolute_cos = 0;
	double relative_cos = 0;
	double result_cos = 0;
	double absolute_ln = 0;
	double relative_ln = 0;
	double result_ln = 0;
	cout << "Start tests..." << endl;
	Clock_exp(x, absolute_exp, relative_exp, result_exp);
	Clock_cos(x, absolute_cos, relative_cos, result_cos);
	Clock_ln(x, absolute_ln, relative_ln, result_ln);
	cout << "All tests done" << endl;
	
	FILE *process;
    char CPU_name[1024];
	
    ofstream benchmark_output;
	benchmark_output.open("bench result.csv", ios_base::app);
	benchmark_output << "PModel;Task;OpType;Opt;LNum;InsCount;Timer;AvTime;AbsErr;RelErr;TaskPerf" << endl;
	
	benchmark_output << CPU_name << ";TaylorSeriesExp;int;None;" << M << ";" << N << ";Clock;" <<
	result_exp << ";" << absolute_exp << ";" << relative_exp << ";" << 1/ result_exp << endl;
	benchmark_output << CPU_name << ";TaylorSeriesCos;int;None;" << M << ";" << N << ";Clock;" <<
	result_cos << ";" << absolute_cos << ";" << relative_cos << ";" << 1/ result_cos << endl;
	benchmark_output << CPU_name << ";TaylorSeriesLn;int;None;" << M << ";" << N << ";Clock;" <<
	result_ln << ";" << absolute_ln << ";" << relative_ln << ";" << 1/ result_ln << endl;

	benchmark_output.close();
	
	cout << "Results saved" << endl;
    
	return 0;
}

void Clock_exp(const int x, double &absolute, double &relative, double &result) {
	unsigned int start, stop;
	for (int i = 0; i < M; i++) {
		start = (double)clock();
		for (int j = 0; j < N; j++)
			exp_(x);
		stop = (double)clock();
		result += stop - start;
	}
	absolute = fabs((result / M - (double)(stop - start)) / CLOCKS_PER_SEC);
	relative = fabs((result / M - (double)(stop - start)) / CLOCKS_PER_SEC) / ((double)(stop - start) / CLOCKS_PER_SEC) * 100;
	result = result / M / CLOCKS_PER_SEC;
}

void Clock_cos(const int x, double &absolute, double &relative, double &result) {
	unsigned int start, stop;
	for (int i = 0; i < M; i++) {
		start = (double)clock();
		for (int j = 0; j < N; j++)
			cos_(x);
		stop = (double)clock();
		result += stop - start;
	}
	absolute = fabs((result / M - (double)(stop - start)) / CLOCKS_PER_SEC);
	relative = fabs((result / M - (double)(stop - start)) / CLOCKS_PER_SEC) / ((double)(stop - start) / CLOCKS_PER_SEC) * 100;
	result = result / M / CLOCKS_PER_SEC;
}

void Clock_ln(const int x, double &absolute, double &relative, double &result) {
	unsigned int start, stop;
	for (int i = 0; i < M; i++) {
		start = (double)clock();
		for (int j = 0; j < N; j++)
			ln_(x);
		stop = (double)clock();
		result += stop - start;
	}
	absolute = fabs((result / M - (double)(stop - start)) / CLOCKS_PER_SEC);
	relative = fabs((result / M - (double)(stop - start)) / CLOCKS_PER_SEC) / ((double)(stop - start) / CLOCKS_PER_SEC) * 100;
	result = result / M / CLOCKS_PER_SEC;
}
