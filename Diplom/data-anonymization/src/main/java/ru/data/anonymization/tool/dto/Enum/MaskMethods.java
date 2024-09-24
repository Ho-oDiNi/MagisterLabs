package ru.data.anonymization.tool.dto.Enum;

import lombok.Getter;
import ru.data.anonymization.tool.Methods.options.type.*;

@Getter
public enum MaskMethods {
    DateAging("DateAging", ru.data.anonymization.tool.Methods.options.type.DateAging.class, "Старение даты"),
    Decomposition("Decomposition", ru.data.anonymization.tool.Methods.options.type.Decomposition.class, "Декомпозиция"),
    GeneralizationString("GeneralizationString", ru.data.anonymization.tool.Methods.options.type.GeneralizationString.class, "Обобщение строк"),
    GeneralizationValue("GeneralizationValue", ru.data.anonymization.tool.Methods.options.type.GeneralizationValue.class, "Обобщение значений и дат"),
    Identifier("Identifier", ru.data.anonymization.tool.Methods.options.type.Identifier.class, "Идентификатор"),
    MicroAggregation("MicroAggregation", MicroAggregation.class, "Микроагрегирование"),
    MicroAggregationBySingleAxis("MicroAggregationBySingleAxis", MicroAggregationBySingleAxis.class, "Микроагрегирование по оси"),
    Round("Round", Round.class, "Округление"),
    Shuffle("Shuffle", Shuffle.class, "Перемешивание"),
    ValueReplacement("ValueReplacement", ValueReplacement.class, "Замена"),
    ValueReplacementByPattern("ValueReplacementByPattern", ValueReplacementByPattern.class, "Замена по паттерну"),
    ValueReplacementFromFile("ValueReplacementFromFile", ValueReplacementFromFile.class, "Замена из файла"),
    ValueVariance("ValueVariance", ValueVariance.class, "Шум"),
    RoundDate("RoundDate", RoundDate.class, "Округление даты");

    final String name;
    final Class<?> methodClass;
    final String nameRus;

    MaskMethods(String name, Class<?> methodClass, String nameRus) {
        this.name = name;
        this.methodClass = methodClass;
        this.nameRus = nameRus;
    }
}
