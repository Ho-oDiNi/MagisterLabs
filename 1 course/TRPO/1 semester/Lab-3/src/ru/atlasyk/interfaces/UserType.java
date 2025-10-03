package ru.atlasyk.interfaces;

import java.io.InputStreamReader;


public interface UserType<T> {
     String typeName();
    // Имя типа
     T create();
    // Создает объект ИЛИ
     T clone();
    // Клонирует текущий
     T readValue(InputStreamReader in); // Создает и читает объект
     T parseValue(String ss);
    // Создает и парсит содержимое из строки
     Comparator getTypeComparator();
    // Возвращает компаратор для сравнения
}
