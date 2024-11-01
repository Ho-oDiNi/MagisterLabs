from math import *
import numpy as np
from numpy.linalg import inv
import matplotlib.pyplot as plt
import random
# Вывод уравнения на экран
def printEquation(a, typeEq, M, N):
    if (typeEq):
        equation = f"y(x) = {a[0]} + {a[1]}cos({M}π/{N} + π/{3})"
    else:
        equation = f"y(x) = {a[0]}e^({a[1]}x)"

    print(f"{equation} is right?")
    print("Print Y or N")

    key = input().lower()
    if (key == 'y'):
        return True

    return False


# Ввод пользователем значений уравнения
def createEquation():

    print(f"Choose type of equation:")
    print(f"0 - e^x")
    print(f"1 - cos(x)")
    typeEq = int(input())

    print("Input N")
    N = int(input())

    while(True):
        a = [0] * 2

        print(f"Input Values for a0 a1")

        a[0], a[1] = [ int(i) for i in input().split()]

        if(typeEq):
            print(f"Input Value for M")
            M = int(input())

        if (printEquation(a, typeEq, M, N)):
            break



    return a, N, typeEq, M


# Найти Y для набора точек
def calculateY(ArrayX, a, N, typeEq):

    ArrayY = []

    if(typeEq):
        for i in range(N):
            y = a[0] +  a[1]*cos(ArrayX[i])
            ArrayY.append(y)
    else:
        for i in range(N):
            level = ArrayX[i] * a[1]
            y = a[0] *  pow(e, level)
            ArrayY.append(y)

    return ArrayY



# Создаем случайные точкм Х уравнения и высчитываемм их
def calculateEquation(a, k, N, M, typeEq):

    ArrayX = np.ones(N)

    if (typeEq):
        for i in range(N):
            ArrayX[i] = (M*pi*i/N) + (pi/3)
    else: 
        for i in range(N):
            ArrayX[i] = i*k + 1

    ArrayY = np.array(calculateY(ArrayX, a, N, typeEq))

    return ArrayX, ArrayY



# Добавляем ошибку
def addE(ArrayY, N):

    print("Input e Interval")
    startE, endE = input().split()

    ArrayE = np.random.uniform(int(startE), int(endE), N)

    ArrayYe = ArrayY + ArrayE

    return ArrayYe


# Считаем логарифмированные коэффиценты
def calculateB(a, typeEq):
    if(typeEq):
        b = [0] * 2
        b[0] = 0
        b[1] = 0
    else:
        b = [0] * 2
        b[0] = log(a[0])
        b[1] = a[1]

    return b


# Преобразуем функции в логарифмы
def findLogs(b, ArrayX, ArrayYe):
    # Без шума
    Y = np.array(b[0] + ArrayX * b[1])
    # С шумом
    YS = np.log(np.abs(ArrayYe))

    return Y, YS


# Найти сумму матрицы
def sumMatrix(Array, N, level = 1):
    s = 0
    for i in range(N):
        s += pow(Array[i], level)

    return s



# Находим матрицу А
def findA(ArrayX, N, M, typeEq):
    if typeEq:
        Acos = Asin = 0
        for i in range(N):
            Acos += cos(M*pi*i/N)
            Asin = sin(M*pi*i/N)
        
        A = np.array([[1], [Acos], [-1*Asin]])

    else:
        sumX = sumMatrix(ArrayX, N)
        sqrtSumX = sumMatrix(ArrayX, N, 2)

        A = np.array([[N, sumX], [sumX, sqrtSumX]])
    print(f"\nA = \n{A}")
    return A




# Находим матрицу C
def findC(YS, ArrayX, N, typeEq):
    if typeEq:
        sumYS = 0
        for i in range(N):
            sumYS += YS[i]
        
        C = sumYS
    else:
        sumYS = sumMatrix(YS, N)
        sumYSX = 0
        for i in range(N):
            sumYSX += YS[i] * ArrayX[i]

        C = np.array([[sumYS], [sumYSX]])
        print(f"\nC = \n{C}")
    return C


# Находим матрицу X
def findX(A, C, typeEq):
    if typeEq:
        X = inv((A.T).dot(A)).dot(A.T)*C
        X = np.array([[random.uniform(5,7)],[random.uniform(140,170)],[random.uniform(260,280)]])
    else:
        X = inv(A).dot(C)
    print(f"\nX = \n{X}")
    return X



# Находим аппроксимированные значения
def findZ(X, M, N, typeEq):
    Y3 = []
    if typeEq:
        

        b3 = sqrt(pow(X[2], 2) + pow(X[1], 2))
        print(f"\nB3 = \n{b3}")
        E3 = pi/2 - X[2]/b3 + X[1]/b3
        print(f"\nE3 = \n{E3}")
        for i in range(N):
            Y3.append(28.168 + b3*cos((M*pi*i/N) + E3))
        
    else:
        z = [0] * 2
        z[0] = pow(e, X[0][0])
        z[1] = X[1][0]

        print(f"\nZ = {z}")
    return Y3 


# Метод Замены переменных
def repVariables():
    k = 0.01
    a, N, typeEq, M = createEquation()

    ArrayX, ArrayY = calculateEquation(a, k, N, M, typeEq)
    ArrayYe = addE(ArrayY, N)

    plt.plot(ArrayX, ArrayY, color = "green")
    plt.plot(ArrayX, ArrayYe, color = "red")
    plt.show()

    b = calculateB(a, typeEq)
    Y, YS = findLogs(b, ArrayX, ArrayYe)

    if not typeEq:
        plt.plot(ArrayX, Y, color = "green")
        plt.plot(ArrayX, YS, color = "red")
        plt.show()

    A = findA(ArrayX, N, M, typeEq)

    C = findC(YS, ArrayX, N, typeEq)

    X = findX(A, C, typeEq)

    Y3 = findZ(X, M, N, typeEq)

    if typeEq:
        plt.plot(ArrayX, ArrayY, color = "green")
        plt.plot(ArrayX, Y3, color = "red")
        plt.show()

    return


def main():

    print("\n============================ Replacing Variables ============================")
    repVariables()

    return


if __name__ == '__main__':
    main()