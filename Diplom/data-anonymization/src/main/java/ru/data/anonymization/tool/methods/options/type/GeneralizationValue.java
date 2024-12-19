package ru.data.anonymization.tool.methods.options.type;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.data.anonymization.tool.methods.options.MaskItem;
import ru.data.anonymization.tool.service.DatabaseConnectionService;

import java.sql.ResultSet;
import java.util.List;

@Data
@NoArgsConstructor
public class GeneralizationValue implements MaskItem {
    private String dateType;
    private String nameTable;
    private String nameColumn;
    private String generalizationTable;
    private List<String> generalizationName;
    private List<?> minValue;
    private List<?> maxValue;
    private boolean isDate;
    private String instruct;

    @Override
    public String getTable() {
        return nameTable;
    }

    @Override
    public List<String> getColumn() {
        return List.of(nameColumn);
    }

    @Override
    public void start(DatabaseConnectionService controllerDB) throws Exception {
        String isChangeNameColumn = "is_change_temp_column";
        String newColumn = "is_new_temp_column";
        int countGroup = 0;


        controllerDB.execute("ALTER TABLE " + nameTable + " ADD COLUMN " + isChangeNameColumn + " INT DEFAULT 0;");
        if (instruct.equals("default")) {
            controllerDB.execute("DROP TABLE IF EXISTS " + generalizationTable + ";");
            controllerDB.execute("CREATE TABLE IF NOT EXISTS " + generalizationTable + " (id SERIAL PRIMARY KEY, value text);");
        } else {
            if (isDate) {
                controllerDB.execute("ALTER TABLE " + nameTable + " ADD COLUMN " + newColumn + " DATE;");
            } else {
                controllerDB.execute("ALTER TABLE " + nameTable + " ADD COLUMN " + newColumn + " FLOAT DEFAULT 0;");
            }
        }


        for (int i = 0; i < generalizationName.size(); i++) {
            if (instruct.equals("default")) {
                controllerDB.execute("INSERT INTO " + generalizationTable + " (value) VALUES ('" + generalizationName.get(i) + "');");
            } else {
                countGroup++;
            }
            if (isDate) {
                System.out.println("UPDATE " + nameTable +
                        " SET " + isChangeNameColumn + "=" + (i + 1) +
                        " WHERE " + nameColumn + ">='" + minValue.get(i) +
                        "' AND " + nameColumn + "<'" + maxValue.get(i) +
                        "' AND " + isChangeNameColumn + "=0;");
                controllerDB.execute(
                        "UPDATE " + nameTable +
                                " SET " + isChangeNameColumn + "=" + (i + 1) +
                                " WHERE " + nameColumn + ">='" + minValue.get(i) +
                                "' AND " + nameColumn + "<'" + maxValue.get(i) +
                                "' AND " + isChangeNameColumn + "=0;"
                );
            } else {
                controllerDB.execute(
                        "UPDATE " + nameTable +
                                " SET " + isChangeNameColumn + "=" + (i + 1) +
                                " WHERE " + nameColumn + ">=" + minValue.get(i) +
                                " AND " + nameColumn + "<" + maxValue.get(i) +
                                " AND " + isChangeNameColumn + "=0;"
                );
            }
        }

        switch (instruct) {
            case "average" -> {
                for (int i = 1; i <= countGroup; i++) {
                    ResultSet resultSet = controllerDB.executeQuery("select avg(" + nameColumn + ")" + " FROM " + nameTable + " WHERE " + nameColumn + " is not null and " + isChangeNameColumn + "=" + i + ";");
                    resultSet.next();
                    Object value = resultSet.getObject(1);
                    if (isDate) {
                        controllerDB.execute("UPDATE " + nameTable + " SET " + newColumn + "='" + value + "' WHERE " + isChangeNameColumn + "=" + i + ";");
                    } else {
                        controllerDB.execute("UPDATE " + nameTable + " SET " + newColumn + "=" + value + " WHERE " + isChangeNameColumn + "=" + i + ";");
                    }
                }
            }
            case "median" -> {
                for (int i = 1; i <= generalizationName.size(); i++) {
                    ResultSet resultSet = controllerDB.executeQuery("select  count(*) from " + nameTable + " WHERE " + nameColumn + " IS NOT NULL and " + isChangeNameColumn + "=" + i + ";");
                    resultSet.next();
                    long size = resultSet.getLong(1);
                    long meddle = (size / 2) - 1;
                    long value;

                    if ((size % 2) == 1) {
                        resultSet = controllerDB.executeQuery("SELECT " + nameColumn + " FROM " + nameTable + " WHERE " + nameColumn + " IS NOT NULL and " + isChangeNameColumn + "=" + i + " ORDER BY " + nameColumn + " OFFSET " + meddle + " LIMIT 1;");
                        resultSet.next();
                        value = resultSet.getLong(1);
                    } else {
                        resultSet = controllerDB.executeQuery("SELECT " + nameColumn + " FROM " + nameTable + " WHERE " + nameColumn + " IS NOT NULL and " + isChangeNameColumn + "=" + i + " ORDER BY " + nameColumn + " OFFSET " + meddle + " LIMIT 2;");
                        resultSet.next();
                        value = resultSet.getLong(1);
                        resultSet.next();
                        value = (value + resultSet.getLong(1)) / 2;
                    }

                    if (isDate) {
                        controllerDB.execute("UPDATE " + nameTable + " SET " + newColumn + "='" + value + "' WHERE " + isChangeNameColumn + "=" + i + ";");
                    } else {
                        controllerDB.execute("UPDATE " + nameTable + " SET " + newColumn + "=" + value + " WHERE " + isChangeNameColumn + "=" + i + ";");
                    }
                }
            }
            case "mode" -> {
                for (int i = 1; i <= generalizationName.size(); i++) {
                    ResultSet resultSet = controllerDB.executeQuery("select  mode() within group (order by " + nameColumn + ") from " + nameTable + " WHERE " + isChangeNameColumn + "=" + i + ";");
                    resultSet.next();
                    Object value = resultSet.getObject(1);

                    if (isDate) {
                        controllerDB.execute("UPDATE " + nameTable + " SET " + newColumn + "='" + value + "' WHERE " + isChangeNameColumn + "=" + i + ";");
                    } else {
                        controllerDB.execute("UPDATE " + nameTable + " SET " + newColumn + "=" + value + " WHERE " + isChangeNameColumn + "=" + i + ";");
                    }
                }
            }
        }
        controllerDB.execute("ALTER TABLE " + nameTable + " DROP COLUMN " + nameColumn + ";");
        if (instruct.equals("default")) {
            controllerDB.execute("ALTER TABLE " + nameTable + " RENAME COLUMN " + isChangeNameColumn + " TO " + nameColumn + ";");
        } else {
            controllerDB.execute("ALTER TABLE " + nameTable + " DROP COLUMN " + isChangeNameColumn + ";");
            controllerDB.execute("ALTER TABLE " + nameTable + " RENAME COLUMN " + newColumn + " TO " + nameColumn + ";");
        }

    }
}
