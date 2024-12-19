package ru.data.anonymization.tool.methods.options.type;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.data.anonymization.tool.methods.options.MaskItem;
import ru.data.anonymization.tool.service.DatabaseConnectionService;

import java.util.List;

@Data
@NoArgsConstructor
public class RoundDate implements MaskItem {
    private String nameTable;
    private String nameColumn;
    private String type;

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
        String typeFor = switch (type) {
            case "yyyy/MM/dd hh:mm:ss" -> "second";
            case "yyyy/MM/dd hh:mm" -> "minute";
            case "yyyy/MM/dd hh" -> "hour";
            case "yyyy/MM/dd" -> "day";
            case "yyyy/MM" -> "month";
            case "yyyy" -> "year";
            default -> null;
        };

        if (typeFor != null) {
            controllerDB.execute("update " + nameTable + " set " + nameColumn + " = date_trunc('" + typeFor + "', " + nameColumn + ") where " + nameColumn + " is not null;");
        } else {
            throw new Exception("Тип незадан!");
        }
    }
}
