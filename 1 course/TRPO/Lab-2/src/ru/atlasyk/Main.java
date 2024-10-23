package ru.atlasyk;

import list.LinkedList;

public class Main {

    public static void main(String[] args) {
        var linkedList = new LinkedList();
        linkedList.add(1);
        linkedList.add(8);
        linkedList.add(2);
        linkedList.add(3);
        linkedList.add(0);
        linkedList.add(1);
        linkedList.add(5);
        linkedList.forEach(x -> System.out.print(x + " "));
        System.out.println();
        System.out.println("Third element: " + linkedList.get(2));
        linkedList.insert(2, 66);
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
        linkedList.add(666);
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

}
