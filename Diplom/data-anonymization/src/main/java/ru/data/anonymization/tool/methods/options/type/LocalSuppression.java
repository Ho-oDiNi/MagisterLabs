package ru.data.anonymization.tool.methods.options.type;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.data.anonymization.tool.methods.options.MaskItem;
import ru.data.anonymization.tool.service.DatabaseConnectionService;

@Data
@NoArgsConstructor
public class LocalSuppression implements MaskItem {

    private String nameTable;
    private List<String> namesColumn;
    private int k;
    private int n;
    private String replacementValue;

    private static final String nameFieldIsChange = "is_change_for_micro_aggregation";
    private static final String nameIdField = "temp_id_group_by_micro_aggregation";

    private String columnsRow;
    private String columnsRowCount;

    @Override
    public void start(DatabaseConnectionService controllerDB) throws Exception {
        Map<String, List<Object>> uniqueRecords = findUniqueRecords(controllerDB);
       switch (n) {
            case 1 -> deleteRecords(uniqueRecords, controllerDB);
           /*  case 2 -> replaceWithNone(uniqueRecords, controllerDB);
            case 3 -> smoothValues(uniqueRecords, k, controllerDB);*/
            default -> System.out.println("Invalid suppression option.");
        }

    }

    @Override
    public String getTable() {
        return nameTable;
    }

    @Override
    public List<String> getColumn() {
        return namesColumn;
    }

    private Map<String, List<Object>> findUniqueRecords(DatabaseConnectionService controllerDB)
            throws
            SQLException {
        Map<String, List<Object>> uniqueRecords = new HashMap<>();

        String columnsPart = getColumnsNameAsString(namesColumn);
        String sql = "SELECT %s ,COUNT(*)  FROM %s GROUP BY %s HAVING COUNT(*) = 1;".formatted(columnsPart, nameTable, columnsPart);

        try (PreparedStatement stmt = controllerDB.getPrepareStatement(sql)) {
            System.out.println(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    List<Object> values = new ArrayList<>();
                    for (String column : namesColumn) {
                        values.add(rs.getObject(column));
                    }
                    uniqueRecords.put(UUID.randomUUID().toString(), values);
                }
            }
            System.out.println("Found " + uniqueRecords.size() + " unique records.");
            return uniqueRecords;
        }

    }

    // n=1: Удаление записей
    private void deleteRecords(
            Map<String, List<Object>> uniqueRecords,
            DatabaseConnectionService controllerDB) throws SQLException {
        for (List<Object> values : uniqueRecords.values()) {
            String whereClause = buildWhereClause(values);
            String deleteSQL = "DELETE FROM " + nameTable + " WHERE " + whereClause;
            try (Statement stmt = controllerDB.getPrepareStatement(deleteSQL)) {
                stmt.executeUpdate(deleteSQL);
            }
        }
        System.out.println("Deleted unique records.");
    }

    // n=2: Замена уникальных значений на NULL
    /*private void replaceWithNone(
            Map<String, List<Object>> uniqueRecords,
            DatabaseConnectionService controllerDB)
            throws SQLException {
        for (List<Object> values : uniqueRecords.values()) {
            String whereClause = buildWhereClause(values);
            StringBuilder updateSQL = new StringBuilder("UPDATE " + TABLE_NAME + " SET ");
            for (int i = 0; i < COLUMNS.size(); i++) {
                updateSQL.append(COLUMNS.get(i)).append(" = NULL");
                if (i < COLUMNS.size() - 1) {
                    updateSQL.append(", ");
                }
            }
            updateSQL.append(" WHERE ").append(whereClause);

            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate(updateSQL.toString());
            }
        }
        System.out.println("Replaced unique values with NULL.");
    }

    // n=3: Сглаживание значений
    private void smoothValues(
            Map<String, List<Object>> uniqueRecords,
            int smoothingOption,
            DatabaseConnectionService controllerDB)
            throws SQLException {
        Map<String, Object> replacementValues = new HashMap<>();

        if (smoothingOption == 1) { // Средние или ближайшие значения
            for (String column : COLUMNS) {
                String sql =
                        "SELECT AVG(CAST(" + column + " AS DOUBLE)) AS avg_val FROM " + TABLE_NAME;
                try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(
                        sql)) {
                    if (rs.next()) {
                        replacementValues.put(column, rs.getDouble("avg_val"));
                    }
                }
            }
        } else if (smoothingOption == 2) { // Ввод вручную
            Scanner scanner = new Scanner(System.in);
            for (String column : COLUMNS) {
                System.out.print("Enter replacement value for " + column + ": ");
                String value = scanner.nextLine();
                replacementValues.put(column, value);
            }
        } else {
            System.out.println("Invalid smoothing option.");
            return;
        }

        for (List<Object> values : uniqueRecords.values()) {
            String whereClause = buildWhereClause(values);
            StringBuilder updateSQL = new StringBuilder("UPDATE " + TABLE_NAME + " SET ");
            int i = 0;
            for (String column : COLUMNS) {
                updateSQL.append(column).append(" = ");
                Object replacement = replacementValues.get(column);
                if (replacement instanceof Number) {
                    updateSQL.append(replacement);
                } else {
                    updateSQL.append("'").append(replacement).append("'");
                }
                if (i < COLUMNS.size() - 1) {
                    updateSQL.append(", ");
                }
                i++;
            }
            updateSQL.append(" WHERE ").append(whereClause);

            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate(updateSQL.toString());
            }
        }
        System.out.println("Smoothed unique values.");
    }*/

    // Вспомогательный метод для создания WHERE-условия
    private String buildWhereClause(List<Object> values) {
        StringBuilder where = new StringBuilder();
        for (int i = 0; i < namesColumn.size(); i++) {
            where.append(namesColumn.get(i));
            if (values.get(i) == null) {
                where.append(" IS NULL");
            } else {
                where.append(" = ");
                if (values.get(i) instanceof Number) {
                    where.append(values.get(i));
                } else {
                    where.append("'").append(values.get(i)).append("'");
                }
            }
            if (i < namesColumn.size() - 1) {
                where.append(" AND ");
            }
        }
        return where.toString();
    }

    private String getColumnsNameAsString(List<String> objects) {
        var stringBuilder = new StringBuilder();
        objects.forEach(object -> {
            stringBuilder.append(object);
            stringBuilder.append(", ");
        });
        return stringBuilder.substring(0, stringBuilder.lastIndexOf(","));
    }

}
