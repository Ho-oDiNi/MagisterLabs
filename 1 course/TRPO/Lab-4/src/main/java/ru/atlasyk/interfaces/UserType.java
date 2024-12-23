package ru.atlasyk.interfaces;

import java.io.IOException;
import java.io.InputStreamReader;

public interface UserType {

    String typeName();

    // Имя типа
    UserType create();

    // Создает объект ИЛИ
    UserType clone();

    // Клонирует текущий
    UserType readValue(InputStreamReader in); // Создает и читает объект

    UserType parseValue(String ss);

    // Создает и парсит содержимое из строки
    Comparator getTypeComparator();



    void writeToFileChar() throws IOException;

    // Возвращает компаратор для сравнения
}
