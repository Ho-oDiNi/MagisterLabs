package main.java;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        var firstFraction = getFraction();
        var secondFraction = getFraction();
        System.out.println("Результат сложения : " + firstFraction.addition(secondFraction));
        System.out.println("Результат умножения : " + firstFraction.multiplication(secondFraction));
        System.out.println("Результат вычитания : " + firstFraction.subtraction(secondFraction));
        System.out.println("Результат деления : " + firstFraction.division(secondFraction));
       //firstFraction.setNumerator(-1);
        firstFraction.writeToFileChar();
        firstFraction.writeToFileBinary();
    }

    public static Fraction getFraction() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите целую часть");
        int wholePart = scanner.nextInt();
        System.out.println("Введите числитель дроби");
        int numerator = scanner.nextInt();
        System.out.println("Введите знаменатель дроби");
        int denominator = scanner.nextInt();
        return Fraction.of(wholePart, numerator, denominator);
    }

}
