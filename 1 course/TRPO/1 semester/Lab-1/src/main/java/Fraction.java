package main.java;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Fraction {

    private int wholePart;

    private int numerator;

    private int denominator;

    private Fraction(int wholePart, int numerator, int denominator) {
        this.wholePart = wholePart;
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public static Fraction of(int wholePart, int numerator, int denominator) {
        if (isValid(wholePart, numerator, denominator)) {
            return new Fraction(wholePart, numerator, denominator);
        } else {
            throw new IllegalArgumentException("Введены не корректные данные");
        }
    }

    public Fraction addition(Fraction fraction) {
        int currentFractionNumerator = getCorrectNumerator(this);
        int additionalFractionNumerator = getCorrectNumerator(fraction);
        int minDenominator = this.getNOD(this.denominator, fraction.denominator);
        minDenominator = (this.denominator * fraction.denominator) / minDenominator;
        int commonNumerator = currentFractionNumerator * minDenominator / this.denominator
                              + additionalFractionNumerator * minDenominator / fraction.denominator;
        int commonNODNumeratorDenominator = this.getNOD(commonNumerator, minDenominator);
        if (commonNODNumeratorDenominator != 1) {
            commonNumerator /= commonNODNumeratorDenominator;
            minDenominator /= commonNODNumeratorDenominator;
        }
        int currentWholePart = commonNumerator / minDenominator;
        commonNumerator -= currentWholePart * minDenominator;
        if (commonNumerator < 0) {
            commonNumerator *= -1;
        }
        return Fraction.of(currentWholePart, commonNumerator, minDenominator);
    }

    public Fraction subtraction(Fraction fraction) {
        return addition(new Fraction(
                0,
                -getCorrectNumerator(fraction),
                fraction.denominator
        ));
    }

    public Fraction multiplication(Fraction fraction) {
        var tmpFraction = getFraction(fraction);

        int currentWholePart = tmpFraction.numerator / tmpFraction.denominator;
        int currentNumerator = tmpFraction.numerator - currentWholePart * tmpFraction.denominator;
        int nod = getNOD(currentNumerator, tmpFraction.denominator);
        if (currentWholePart < 0 && currentNumerator < 0) {
            currentNumerator *= -1;
        }
        return Fraction.of(currentWholePart, currentNumerator / nod, tmpFraction.denominator / nod);
    }

    public Fraction division(Fraction fraction) {
        var tmpNumerator = getCorrectNumerator(fraction);
        var tmpDenominator = fraction.denominator;
        if (tmpNumerator < 0) {
            tmpNumerator *= -1;
            tmpDenominator *= -1;
        }
        return multiplication(new Fraction(
                0,
                tmpDenominator,
                tmpNumerator
        ));
    }

    public int getWholePart() {
        return wholePart;
    }

    public void setWholePart(int wholePart) {
        try {
            if (!isValid(wholePart, numerator, denominator)) {
                throw new IllegalArgumentException(
                        "Предоставлен не корректные данные");

            }
            this.wholePart = wholePart;
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }

    public int getNumerator() {
        return numerator;
    }

    public void setNumerator(int numerator) {
        try {
            if (!isValid(wholePart, numerator, denominator)) {
                throw new IllegalArgumentException(
                        "Предоставлен не корректный числитель "
                        + "который больше либо равен знаменателю");

            }
            this.numerator = numerator;
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }

    public int getDenominator() {
        return denominator;
    }

    public void setDenominator(int denominator) {
        try {
            if (!isValid(wholePart, numerator, denominator)) {
                throw new IllegalArgumentException(
                        "Предоставлен не корректный знаменатель "
                        + "который меньше либо равен числителю");

            }
            this.denominator = denominator;
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }

    public void writeToFileChar() throws IOException {
        var path = Path.of(System.getProperty("user.dir"), "/char.txt");
        Files.deleteIfExists(path);
        Files.createFile(path);
        try (var bf = Files.newBufferedWriter(path)) {
            bf.write(this.toString());
        }
    }

    public void writeToFileBinary() throws IOException {
        var path = Path.of(System.getProperty("user.dir"), "/byte.txt");
        Files.deleteIfExists(path);
        Files.createFile(path);
        try (var dos = new DataOutputStream(Files.newOutputStream(path))) {
            dos.write(wholePart);
            dos.write(numerator);
            dos.write(denominator);
        }
    }

    @Override
    public String toString() {
        String str = wholePart + " " + numerator + "/" + denominator;
        if (numerator == 0) {
            str = String.valueOf(wholePart);
        }
        if (wholePart == 0 && numerator != 0) {
            str = numerator + "/" + denominator;
        }
        return str;
    }

    private Fraction getFraction(Fraction fraction) {
        int currentFractionNumerator = getCorrectNumerator(this);
        int additionalFractionNumerator = getCorrectNumerator(fraction);
        var tmpFraction = new Fraction(
                0,
                additionalFractionNumerator,
                fraction.denominator
        );

        var currentFraction = new Fraction(
                0,
                currentFractionNumerator,
                denominator
        );

        tmpFraction = new Fraction(
                0,
                tmpFraction.numerator * currentFraction.numerator,
                tmpFraction.denominator * currentFraction.denominator
        );
        return tmpFraction;
    }

    private int getNOD(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return Math.abs(a);
    }

    private int getCorrectNumerator(Fraction fraction) {
        int fractionNumerator = fraction.numerator;
        if (fraction.wholePart > 0) {
            fractionNumerator += fraction.denominator * fraction.wholePart;
        }
        if (fraction.wholePart < 0) {
            fractionNumerator += fraction.denominator * (-fraction.wholePart);
            fractionNumerator *= -1;
        }
        return fractionNumerator;
    }

    public static boolean isValid(int wholePart, int numerator, int denominator) {
        boolean isCorrectNumerator = true;
        if (wholePart != 0) {
            isCorrectNumerator = numerator >= 0;
        }
        var isValidNumerator = (Math.abs(numerator) < Math.abs(denominator)) && isCorrectNumerator;
        var isNonZeroDenominator = denominator > 0;
        return isValidNumerator && isNonZeroDenominator;

    }

}
