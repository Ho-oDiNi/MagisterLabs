from math import *
import numpy as np
from numpy.linalg import inv
import matplotlib.pyplot as plt

# Вывод уравнения на экран
def printEquation(a):
    
    equation = f"y(x) = {a[0]}e^({a[1]}x)"

    print(f"{equation} is right?")
    print("Print Y or N")

    key = input().lower()
    if (key == 'y'):
        return True

    return False


# Ввод пользователем значений уравнения
def createEquation():
    
    while(True):
        a = [0] * 2
        
        print(f"Input Values for a0 a1")

        a[0], a[1] = [ int(i) for i in input().split()]
        
        if (printEquation(a)):
            break
        
    
    print("Input N")
    N = int(input())

    return a, N


# Найти Y для набора точек
def calculateY(ArrayX, a, N):

    ArrayY = []

    for i in range(N):
        level = ArrayX[i] * a[1]
        y = a[0] *  pow(e, level)
        ArrayY.append(y)

    return ArrayY


# Создаем случайные точкм Х уравнения и высчитываемм их
def calculateEquation(a, k, N):  
    
    ArrayX = np.ones(N)
    for i in range(N):
        ArrayX[i] = i*k + 1

    ArrayY = np.array(calculateY(ArrayX, a, N))

    return ArrayX, ArrayY



# Добавляем ошибку
def addE(ArrayY, N):
    
    print("Input e Interval")
    startE, endE = input().split()

    ArrayE = np.random.uniform(int(startE), int(endE), N)

    ArrayYe = ArrayY + ArrayE

    return ArrayYe


# Считаем логарифмированные коэффиценты
def calculateB(a):
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
def findA(ArrayX, N):
    sumX = sumMatrix(ArrayX, N)
    sqrtSumX = sumMatrix(ArrayX, N, 2)

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
def findX(A, C):
    X = inv(A).dot(C)
    print(f"\nX = \n{X}")
    return X



# Находим аппроксимированные значения
def findZ(X):
    z = [0] * 2
    z[0] = pow(e, X[0][0])
    z[1] = X[1][0]

    print(f"\nZ = {z}")
    return z


# Метод Замены переменных
def repVariables():
    k = 0.01
    a, N = createEquation()
    
    ArrayX, ArrayY = calculateEquation(a, k, N)
    ArrayYe = addE(ArrayY, N)

    plt.plot(ArrayX, ArrayY, color = "green")
    plt.plot(ArrayX, ArrayYe, color = "red")
    plt.show()

    b = calculateB(a)
    Y, YS = findLogs(b, ArrayX, ArrayYe)

    plt.plot(ArrayX, Y, color = "green")
    plt.plot(ArrayX, YS, color = "red")
    plt.show()

    A = findA(ArrayX, N)
    C = findC(YS, ArrayX, N)
    X = findX(A, C)

    z = findZ(X)

    return


def main():

    print("\n============================ Replacing Variables ============================")
    repVariables()

    return


if __name__ == '__main__':
    main()