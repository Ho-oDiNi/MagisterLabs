package ru.data.anonymization.tool.dto.enums;

import lombok.Getter;
import ru.data.anonymization.tool.methods.options.type.*;

@Getter
public enum MaskMethods {
    DateAging(
            "DateAging",
            ru.data.anonymization.tool.methods.options.type.DateAging.class,
            "Стирание даты"
    ),
    Decomposition(
            "Decomposition",
            ru.data.anonymization.tool.methods.options.type.Decomposition.class,
            "Метод Декомпозиции"
    ),
    GeneralizationString(
            "GeneralizationString",
            ru.data.anonymization.tool.methods.options.type.GeneralizationString.class,
            "Обобщение строк"
    ),
    GeneralizationValue(
            "GeneralizationValue",
            ru.data.anonymization.tool.methods.options.type.GeneralizationValue.class,
            "Обобщение значений и дат"
    ),
    Identifier(
            "Identifier",
            ru.data.anonymization.tool.methods.options.type.Identifier.class,
            "Введение Идентификаторов"
    ),
    MicroAggregation("MicroAggregation", MicroAggregation.class, "Микроагрегирование"),
    MicroAggregationBySingleAxis(
            "MicroAggregationBySingleAxis",
            MicroAggregationBySingleAxis.class,
            "Микроагрегирование по оси"
    ),
    Round("Round", Round.class, "Метод Округления"),
    RoundDate("RoundDate", RoundDate.class, "Округление даты"),
    Shuffle("Shuffle", Shuffle.class, "Метод Перемешивания"),
    ValueReplacement("ValueReplacement", ValueReplacement.class, "Метод маскирования"),
    ValueReplacementByPattern(
            "ValueReplacementByPattern",
            ValueReplacementByPattern.class,
            "Метод маскирования"
    ),
    ValueReplacementFromFile(
            "ValueReplacementFromFile",
            ValueReplacementFromFile.class,
            "Метод маскирования из файла"
    ),
    ValueVariance("ValueVariance", ValueVariance.class, "Метод добавления Шума");

    final String name;
    final Class<?> methodClass;
    final String nameRus;

    MaskMethods(String name, Class<?> methodClass, String nameRus) {
        this.name = name;
        this.methodClass = methodClass;
        this.nameRus = nameRus;
    }
}
