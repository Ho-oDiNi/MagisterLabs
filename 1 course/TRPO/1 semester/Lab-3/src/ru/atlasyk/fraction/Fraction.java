package ru.atlasyk.fraction;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import ru.atlasyk.interfaces.Comparator;
import ru.atlasyk.interfaces.UserType;

public class Fraction implements UserType<Fraction> {

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

    @Override
    public String typeName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Fraction create() {
        return new Fraction(0, 0, 1);
    }

    @Override
    public Fraction clone() {
        return Fraction.of(this.getWholePart(), this.getNumerator(), this.getDenominator());
    }

    @Override
    public Fraction readValue(InputStreamReader in) {
        try (var br = new BufferedReader(in)) {
            var input = br.readLine();
            return parseValue(input);

        } catch (IOException e) {
            throw new RuntimeException("some trouble in input stream");
        }
    }

    @Override
    public Fraction parseValue(String ss) {
        if (!ss.matches("-?\\d\\s-?\\d*\\/[1-9]\\d*")) {
            throw new IllegalArgumentException("некорректные данные");
        }
        var stringTokenizer = new StringTokenizer(ss, " /");
        List<Integer> integers = new ArrayList<>();
        while (stringTokenizer.hasMoreElements()) {
            integers.add(Integer.valueOf(stringTokenizer.nextToken()));
        }
        return Fraction.of(integers.get(0), integers.get(1), integers.get(2));
    }

    @Override
    public Comparator getTypeComparator() {
        return (o1, o2) -> {
            if (!(o1 instanceof Fraction thisFraction && o2 instanceof Fraction otherFraction)) {
                throw new RuntimeException("Некорректные типы для сравнения");
            }
            if (thisFraction.wholePart != otherFraction.wholePart) {
                return Integer.compare(otherFraction.wholePart, thisFraction.wholePart);
            }

            int thisNumerator = thisFraction.wholePart < 0 ? -Math.abs(thisFraction.numerator)
                    : thisFraction.numerator;
            int otherNumerator = otherFraction.wholePart < 0 ? -Math.abs(otherFraction.numerator)
                    : otherFraction.numerator;

            int lcm = lcm(thisFraction.denominator, otherFraction.denominator);  // НОК для знаменателей
            int thisAdjustedNumerator = thisNumerator * (lcm / thisFraction.denominator);
            int otherAdjustedNumerator = otherNumerator * (lcm / otherFraction.denominator);
            return Integer.compare(otherAdjustedNumerator, thisAdjustedNumerator);
        };
    }
    //NOK
    private int lcm(int a, int b) {
        return Math.abs(a * b) / getNOD(a, b);
    }


}
