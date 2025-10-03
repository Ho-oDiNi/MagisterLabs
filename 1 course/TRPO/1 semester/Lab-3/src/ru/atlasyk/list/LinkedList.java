package ru.atlasyk.list;

import java.util.Objects;
import java.util.function.Consumer;
import ru.atlasyk.interfaces.Comparator;
import ru.atlasyk.interfaces.UserType;

public class LinkedList {

    private ListNode head;
    private int size;

    // Получить элемент по индексу
    public UserType get(int index) {
        ListNode current = head;
        int count = 0;

        while (current != null) {
            if (count == index) {
                return current.getData();
            }
            count++;
            current = current.getNext();
        }

        throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + count);
    }

    // Вставить элемент по индексу
    public void insert(int index, UserType data) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index cannot be negative: " + index);
        }

        ListNode newNode = new ListNode(data);

        if (index == 0) {
            newNode.setNext(head);
            head = newNode;
            return;
        }

        ListNode current = head;
        for (int i = 0; i < index - 1; i++) {
            if (current == null) {
                throw new IndexOutOfBoundsException("Index: " + index);
            }
            current = current.getNext();
        }

        newNode.setNext(current.getNext());
        current.setNext(newNode);
        size++;
    }

    // Удалить элемент по индексу
    public void delete(int index) {
        if (index < 0 || head == null) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }

        if (index == 0) {
            head = head.getNext();
            return;
        }

        ListNode current = head;
        for (int i = 0; i < index - 1; i++) {
            if (current == null || current.getNext() == null) {
                throw new IndexOutOfBoundsException("Index: " + index);
            }
            current = current.getNext();
        }

        if (current.getNext() == null) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }

        current.setNext(current.getNext().getNext());
        size--;
    }

    // Добавить элемент в конец списка
    public void add(UserType data) {
        ListNode newNode = new ListNode(data);

        if (head == null) {
            head = newNode;
            size++;
            return;
        }

        ListNode current = head;
        while (current.getNext() != null) {
            current = current.getNext();
        }

        current.setNext(newNode);
        size++;
    }

    // Применить функцию к каждому элементу списка
    public void forEach(Consumer<? super UserType> func) {
        Objects.requireNonNull(func);
        ListNode current = head;
        while (current != null) {
            func.accept(current.getData());
            current = current.getNext();
        }
    }

    // Сортировка списка (метод пузырька)
    public void sort() {
        head = mergeSort(head);
    }

    public int size() {
        return size;
    }

    // Вспомогательный метод для сортировки
    private ListNode mergeSort(ListNode node) {
        if (node == null || node.getNext() == null) {
            return node; // База рекурсии
        }

        // Разделяем список на две половины
        ListNode middle = getMiddle(node);
        ListNode nextOfMiddle = middle.getNext();
        middle.setNext(null); // Разделяем на две части

        // Рекурсивно сортируем обе половины
        ListNode left = mergeSort(node);
        ListNode right = mergeSort(nextOfMiddle);

        // Объединяем отсортированные половины
        return sortedMerge(left, right);
    }

    // Метод для получения середины списка
    private ListNode getMiddle(ListNode node) {
        if (node == null) {
            return null;
        }

        ListNode slow = node;
        ListNode fast = node.getNext();

        // Используем два указателя для нахождения середины
        while (fast != null) {
            fast = fast.getNext();
            if (fast != null) {
                slow = slow.getNext();
                fast = fast.getNext();
            }
        }
        return slow;
    }

    // Метод для слияния двух отсортированных списков
    private ListNode sortedMerge(ListNode left, ListNode right) {
        ListNode result;

        // База рекурсии
        if (left == null) {
            return right;
        } else if (right == null) {
            return left;
        }

        // Сравниваем данные и сливаем
        Comparator typeComparator = left.getData().getTypeComparator();
        if (typeComparator.compare(right.getData(), left.getData()) < 0) {
            result = left;
            result.setNext(sortedMerge(left.getNext(), right));
        } else {
            result = right;
            result.setNext(sortedMerge(left, right.getNext()));
        }
        return result;
    }

}

class ListNode {

    UserType data;
    ListNode next;

    ListNode(UserType data, ListNode next) {
        this.data = data;
        this.next = next;
    }

    ListNode(UserType data) {
        this.data = data;
    }

    UserType getData() {
        return data;
    }

    void setData(UserType data) {
        this.data = data;
    }

    ListNode getNext() {
        return next;
    }

    void setNext(ListNode next) {
        this.next = next;
    }

}
