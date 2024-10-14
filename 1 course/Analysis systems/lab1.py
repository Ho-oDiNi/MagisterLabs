from math import *
import numpy as np
from numpy.linalg import inv
import matplotlib.pyplot as plt

    # order = 1
    # C = 5
    # N = 3
    # ArrayX = [5.013, 6.933, 10.85]
    # ArrayYe = [15.376, 19.689, 26.874]

    # order = 2
    # C = 2
    # N = 4
    # ArrayX = [-5.087, 6.533, 2.318, 8.015]
    # ArrayY = [105.067707, 97.375267, 6.529372, 154.645675]
    # ArrayYe = [105.983, 97.458, 6.454, 155.391]


def srtingEquation(order, multipliers = False, C = False):
    
    if(multipliers == False):
        multipliers = [""] * order

    equation = f"y(x) = {multipliers[0]}x^{order}"
    
    for i in range(1, order):
        equation += f" + {multipliers[i]}x^{order-i}"

    if (C == False):
        equation += f" + C"
    else:
        equation += f" + {C}"

    return equation


def orderEquation():
    
    while(True):
        print("Input Order Equation")
        order = int(input())
        
        equation = srtingEquation(order)
        print(f"{equation} is right?")
        print("Print Y or N")

        request = input().lower()
        if (request == 'y'):
            break

    return order


def createEquation(order):
    
    while(True):
        
        multipliers = []
        for i in range(0, order):
            print(f"Input Value for x^{order-i}")
            aX = int(input())
            multipliers.append(aX)

        print("Input Value C")
        C = int(input())

        equation = srtingEquation(order, multipliers, C)
        print(f"{equation} is right?")
        print("Print Y or N")

        request = input().lower()
        if (request == 'y'):
            break

    print("Input N")
    N = int(input())

    return multipliers, C, N


def calculateY(ArrayX, multipliers, C, N):

    ArrayY = []
    order = len(multipliers)

    for i in range(N):
        y = 0
        for j in range(order):
            y += multipliers[j] * pow(ArrayX[i], order-j)
            # print(f"\n {multipliers[j]} * {ArrayX[i]}^{order-j}\n")
        ArrayY.append(y + C)

    return ArrayY


def randomEquation(multipliers, C, N):  
    
    print("Input X Interval")
    startX, endX = input().split()

    ArrayX = np.random.uniform(int(startX), int(endX), N)
    print(f"X = {ArrayX}")
    
    ArrayY = np.array(calculateY(ArrayX, multipliers, C, N))
    
    print(f"Y = {ArrayY}")

    return ArrayX, ArrayY



def addE(ArrayY, N):
    
    print("Input e Interval")
    startE, endE = input().split()

    ArrayE = np.random.uniform(int(startE), int(endE), N)
    print(f"e = {ArrayE}")

    ArrayYe = ArrayY + ArrayE
    print(f"Ye = {ArrayYe}")

    return ArrayYe


def createAMatrix(ArrayX, N, order):
    
    matrix = []

    for i in range(N):
        xPow = 0

        for j in range(order):
            xPow = pow(ArrayX[i], order-j)
            matrix.append(xPow)
        matrix.append(1.)

    aMatrix = np.array(matrix).reshape(N, order+1)
    print(f"A = {aMatrix}")

    return aMatrix



def findR(aMatrix, bMatrix):

    rMatrix = inv(np.dot(aMatrix.T, aMatrix)).dot(aMatrix.T).dot(bMatrix)
    print(f"\nR = {rMatrix}")

    return rMatrix


def Euclid():
    order = orderEquation()
    multipliers, C, N = createEquation(order)
    
    ArrayX, ArrayY = randomEquation(multipliers, C, N)
    ArrayYe = addE(ArrayY, N)

    aMatrix = createAMatrix(ArrayX, N, order)
    rMatrix = findR(aMatrix, ArrayYe)

    return multipliers, C


def createPT(N):

    print("Input X Interval")
    startX, endX = input().split()

    print("Input Y Interval")
    startY, endY = input().split()

    ArrayPx = np.random.uniform(int(startX), int(endX), N)

    indexSorted = np.argsort(ArrayPx)
    ArrayPx = np.sort(ArrayPx)

    ArrayTy = np.random.uniform(int(startY), int(endY), N)
    for i in range(0, N):
        ArrayTy[i]= ArrayTy[indexSorted[i]]


    print(f"P = {ArrayPx}")
    print(f"T = {ArrayTy}")

    return ArrayPx, ArrayTy


def getS(ArrayTy, ArrayGy, N):
    S = 0

    for i in range(N):
        S += pow(ArrayTy[i] - ArrayGy[i], 2)

    print(f"S = {S}")

    return S


def getR(S, N):

    R = sqrt(S)/N
    
    print(f"R = {R}")
    
    return R



def leastSquares(multipliers, C):
    print("Input I")
    I = int(input())

    ArrayPx, ArrayTy = createPT(I)
    ArrayGy = np.array(calculateY(ArrayPx, multipliers, C, I))

    plt.plot(ArrayPx, ArrayTy, color = "red")
    plt.plot(ArrayPx, ArrayGy, color = "green")

    plt.show()

    S = getS(ArrayTy, ArrayGy, I)
    R = getR(S, I)


    return



def main():

    print("\n============================ Euclid ============================")
    multipliers, C = Euclid()

    print("\nPlease Enter any Key")
    key = input()

    print("============================ Least Squares ============================")
    leastSquares(multipliers, C)

    return

if __name__ == '__main__':
    main()