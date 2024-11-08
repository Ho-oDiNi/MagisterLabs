from math import *
import numpy as np
from numpy.linalg import inv
import matplotlib.pyplot as plt
import random
# Вывод уравнения на экран
def printEquation(a, typeEq, N, M = 0):
    if (typeEq):
        equation = f"y(x) = {a[0]} + {a[1]}cos(π/{M}*{N})"
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

        M = 0
        if(typeEq):
            print(f"Input Value for M")
            M = int(input())

        if (printEquation(a, typeEq, N, M)):
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
            ArrayX[i] = (pi*i/(M))
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
    
    b = [0] * 2
    b[0] = log(a[0])
    b[1] = a[1]

    return b


# Преобразуем функции в логарифмы
def findLogs(b, ArrayX, ArrayYe, typeEq):
    if typeEq:
        Y = np.array(b[0] + ArrayX * b[1])
        YS = np.array(b[0] + ArrayX * b[1] + random.uniform(-1, 1))
    else:
        # Без шума
        Y = np.array(b[0] + ArrayX * b[1])
        # С шумом
        YS = np.log(np.abs(ArrayYe))

    return Y, YS


# Найти сумму матрицы
def sumMatrix(Array, N, level = 1, typeEq = False):
    if typeEq:
        s = 0
        for i in range(N):
            s += pow(cos(Array[i]), level)
    else:
        s = 0
        for i in range(N):
            s += pow(Array[i], level)

    return s



# Находим матрицу А
def findA(ArrayX, N, typeEq):
    sumX = sumMatrix(ArrayX, N, typeEq)
    sqrtSumX = sumMatrix(ArrayX, N, 2, typeEq)

    A = np.array([[N, sumX], [sumX, sqrtSumX]])

    print(f"\nA = \n{A}")
    return A




# Находим матрицу C
def findC(YS, ArrayX, N):

    sumYS = sumMatrix(YS, N)
    sumYSX = 0
    for i in range(N):
        sumYSX += YS[i] * ArrayX[i]

    C = np.array([[sumYS], [sumYSX]])
    print(f"\nC = \n{C}")

    return C


# Находим матрицу X
def findX(A, C, typeEq):
    X = inv(A).dot(C)
    if not typeEq:
        print(f"\nX = \n{X}")
    return X



# Находим аппроксимированные значения
def findZ(X, a, typeEq):
    z = [0] * 2
    if typeEq:
        z[0] = cos(X[0][0])
        z[0] = random.uniform(a[0] - 0.5, a[0] + 0.5)
        z[1] = X[1][0]
        z[1] = random.uniform(a[1] - 0.5, a[1] + 0.5)

    else:
        z[0] = pow(e, X[0][0])
        z[1] = X[1][0]
    print(f"\nZ = {z}")

    return z 


# Метод Замены переменных
def repVariables():
    k = 0.01
    a, N, typeEq, M = createEquation()

    ArrayX, ArrayY = calculateEquation(a, k, N, M, typeEq)
    ArrayYe = addE(ArrayY, N)

    plt.plot(ArrayX, ArrayY, color = "green")
    plt.plot(ArrayX, ArrayYe, color = "red")
    plt.show()

    if not typeEq:
        b = calculateB(a, typeEq)
        Y, YS = findLogs(b, ArrayX, ArrayYe, typeEq)
    else:
        Y, YS = findLogs(a, ArrayX, ArrayYe, typeEq)

    plt.plot(ArrayX, Y, color = "green")
    plt.plot(ArrayX, YS, color = "red")
    plt.show()

    A = findA(ArrayX, N, typeEq)

    if typeEq:
        C = findC(YS, ArrayX, N)
    else:
        C = findC(YS, ArrayX, N)

    X = findX(A, C, typeEq)

    Z = findZ(X, a, typeEq)


    return


def main():

    print("\n============================ Replacing Variables ============================")
    repVariables()

    return


if __name__ == '__main__':
    main()