package ru.atlasyk;

import java.net.CacheResponse;
import ru.atlasyk.base.usertype.StringUserTypeImpl;
import ru.atlasyk.fraction.Fraction;
import ru.atlasyk.interfaces.Comparator;
import ru.atlasyk.list.LinkedList;

public class Main {

    public static void main(String[] args) {
        getTraceResultForFraction();
        getTraceResultForStringImpl();

    }

    public static void getTraceResultForFraction() {
        var linkedList = new LinkedList();
        var fraction1 = Fraction.of(1, 2, 3);
        var fraction2 = Fraction.of(-1, 2, 3);
        var fraction3 = Fraction.of(0, 9, 14);
        var fraction4 = Fraction.of(5, 3, 8);
        var fraction5 = Fraction.of(-8, 5, 6);
        linkedList.add(fraction1);
        linkedList.add(fraction2);
        linkedList.add(fraction3);
        linkedList.add(fraction4);
        linkedList.add(fraction5);
        linkedList.forEach(x -> System.out.print(x + ", "));
        System.out.println();
        System.out.println("Third element: " + linkedList.get(2));
        linkedList.insert(2, fraction1);
        System.out.println("After insert: ");
        linkedList.forEach(x -> System.out.print(x + " "));
        System.out.println();
        System.out.println("Third element: " + linkedList.get(2));
        linkedList.delete(2);
        System.out.println("After delete: ");
        linkedList.forEach(x -> System.out.print(x + " "));
        System.out.println();
        System.out.println("Third element: " + linkedList.get(2));
        System.out.println("After adding to end: ");
        linkedList.add(Fraction.of(1, 4, 5));
        linkedList.forEach(x -> System.out.print(x + " "));
        System.out.println();
        System.out.println("Last element: " + linkedList.get(linkedList.size() - 1));
        System.out.println("Before sort: ");
        linkedList.forEach(x -> System.out.print(x + " "));
        System.out.println();
        linkedList.sort();
        System.out.println("After sort:");
        linkedList.forEach(x -> System.out.print(x + " "));
        System.out.println();
    }

    public static void getTraceResultForStringImpl() {
        var someStr = new StringUserTypeImpl("some str");
        var someStr1 = new StringUserTypeImpl("abg");

        System.out.println(someStr.typeName());
        var stringUserType = someStr1.create();
        System.out.println(stringUserType.toString());
        var cloneStr = someStr1.clone();
        System.out.println("cloneStr value: " + cloneStr.getStringValue()
                           + " someStr1 value: " + someStr1.getStringValue());
        var exampleStr = "ex str";
        var stringUserType1 = someStr1.parseValue(exampleStr);
        System.out.println(stringUserType1.getStringValue());
        var typeComparator = someStr1.getTypeComparator();
        var compareValue = typeComparator.compare(someStr1, someStr1);
        if (compareValue > 0) {
            System.out.println(someStr1.getStringValue());
        } else if (compareValue == 0) {
            System.out.println("equal");
        } else {
            System.out.println(someStr.getStringValue());
        }

    }

}
