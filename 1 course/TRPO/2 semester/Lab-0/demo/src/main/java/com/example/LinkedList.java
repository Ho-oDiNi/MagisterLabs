package com.example;

import java.io.*;
import java.util.Comparator;

public class LinkedList<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Node<T> head;
    private Node<T> tail;
    private int size;
    private Class<T> type;

    // Конструктор с указанием типа
    public LinkedList(Class<T> type) {
        this.type = type;
    }

    // Внутренний класс узла
    private static class Node<T> implements Serializable {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
        }
    }

    // Добавление в конец
    public void add(T data) {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if (!type.isInstance(data)) {
            throw new IllegalArgumentException("Invalid data type");
        }

        Node<T> newNode = new Node<>(data);
        if (head == null) {
            head = newNode;
        } else {
            tail.next = newNode;
        }
        tail = newNode;
        size++;
    }

    // Получение по индексу
    public T get(int index) {
        checkIndex(index);
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }

    // Вставка по индексу
    public void insert(int index, T data) {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if (!type.isInstance(data)) {
            throw new IllegalArgumentException("Invalid data type");
        }

        if (index == 0) {
            Node<T> newNode = new Node<>(data);
            newNode.next = head;
            head = newNode;
            if (tail == null) {
                tail = head;
            }
        } else if (index == size) {
            add(data);
            return;
        } else {
            checkIndex(index);
            Node<T> prev = getNode(index - 1);
            Node<T> newNode = new Node<>(data);
            newNode.next = prev.next;
            prev.next = newNode;
        }
        size++;
    }

    // Удаление по индексу
    public T remove(int index) {
        checkIndex(index);
        T removed;
        if (index == 0) {
            removed = head.data;
            head = head.next;
            if (head == null) {
                tail = null;
            }
        } else {
            Node<T> prev = getNode(index - 1);
            removed = prev.next.data;
            prev.next = prev.next.next;
            if (prev.next == null) {
                tail = prev;
            }
        }
        size--;
        return removed;
    }

    // Итерация с callback
    public void forEach(Callback<T> callback) {
        Node<T> current = head;
        while (current != null) {
            callback.toDo(current.data);
            current = current.next;
        }
    }

    // Сортировка с компаратором
    public void sort(Comparator<? super T> comparator) {
        if (size <= 1) return;
        
        // Реализация сортировки слиянием
        head = mergeSort(head, comparator);
        
        // Обновляем tail после сортировки
        tail = head;
        while (tail != null && tail.next != null) {
            tail = tail.next;
        }
    }

    private Node<T> mergeSort(Node<T> head, Comparator<? super T> comparator) {
        if (head == null || head.next == null) {
            return head;
        }
        
        // Разделение списка
        Node<T> middle = getMiddle(head);
        Node<T> nextOfMiddle = middle.next;
        middle.next = null;
        
        // Рекурсивная сортировка
        Node<T> left = mergeSort(head, comparator);
        Node<T> right = mergeSort(nextOfMiddle, comparator);
        
        // Слияние
        return merge(left, right, comparator);
    }

    private Node<T> merge(Node<T> left, Node<T> right, Comparator<? super T> comparator) {
        Node<T> result;
        
        if (left == null) return right;
        if (right == null) return left;
        
        if (comparator.compare(left.data, right.data) <= 0) {
            result = left;
            result.next = merge(left.next, right, comparator);
        } else {
            result = right;
            result.next = merge(left, right.next, comparator);
        }
        
        return result;
    }

    private Node<T> getMiddle(Node<T> head) {
        if (head == null) return null;
        
        Node<T> slow = head;
        Node<T> fast = head.next;
        
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        
        return slow;
    }

    // Сериализация в текстовый файл
    public void saveToTextFile(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(filename)) {
            forEach(data -> writer.println(data.toString()));
        }
    }

    // Десериализация из текстового файла
    @SuppressWarnings("unchecked")
    public void loadFromTextFile(String filename) throws IOException {
        clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (type == Integer.class) {
                    add((T) Integer.valueOf(line));
                } else if (type.getName().equals("main.java.Fraction")) {
                    try {
                        Class<?> fractionClass = Class.forName("main.java.Fraction");
                        Object fraction = fractionClass.getMethod("parseFromString", String.class).invoke(null, line);
                        add((T) fraction);
                    } catch (Exception e) {
                        throw new IOException("Failed to parse Fraction", e);
                    }
                }
            }
        }
    }

    // Сериализация в бинарный файл
    public void saveToBinaryFile(String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(this);
        }
    }

    // Десериализация из бинарного файла
    @SuppressWarnings("unchecked")
    public static <T> LinkedList<T> loadFromBinaryFile(String filename, Class<T> type) 
            throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            LinkedList<T> list = (LinkedList<T>) ois.readObject();
            if (!list.type.equals(type)) {
                throw new ClassCastException("Incompatible types");
            }
            return list;
        }
    }

    // Вспомогательные методы
    private Node<T> getNode(int index) {
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        head = tail = null;
        size = 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node<T> current = head;
        while (current != null) {
            sb.append(current.data);
            if (current.next != null) {
                sb.append(", ");
            }
            current = current.next;
        }
        sb.append("]");
        return sb.toString();
    }
}