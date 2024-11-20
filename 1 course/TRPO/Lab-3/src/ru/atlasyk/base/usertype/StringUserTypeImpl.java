package ru.atlasyk.base.usertype;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import ru.atlasyk.fraction.Fraction;
import ru.atlasyk.interfaces.Comparator;
import ru.atlasyk.interfaces.UserType;

public class StringUserTypeImpl implements UserType<StringUserTypeImpl> {

    private String stringValue;

    public StringUserTypeImpl(String stringValue) {
        this.stringValue = stringValue;
    }

    private StringUserTypeImpl() {}

    ;

    @Override
    public String typeName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public StringUserTypeImpl create() {
        return new StringUserTypeImpl();
    }

    @Override
    public StringUserTypeImpl clone() {
        return new StringUserTypeImpl(stringValue);
    }

    public String getStringValue() {
        return stringValue;
    }

    @Override
    public StringUserTypeImpl readValue(InputStreamReader in) {
        try (var br = new BufferedReader(in)) {
            var input = br.readLine();
            return parseValue(input);
        } catch (IOException e) {
            throw new RuntimeException("some trouble in input stream");
        }
    }

    @Override
    public StringUserTypeImpl parseValue(String ss) {
        if (ss != null) {
            return new StringUserTypeImpl(ss);
        } else {
            throw new IllegalArgumentException("input param is null");
        }
    }

    @Override
    public Comparator getTypeComparator() {
        return ((o1, o2) -> {
            if (!(o1 instanceof StringUserTypeImpl str1 && o2 instanceof StringUserTypeImpl str2)) {
                throw new RuntimeException("Некорректные типы для сравнения");
            }
            return str1.stringValue.compareTo(str2.stringValue);
        });
    }

}
