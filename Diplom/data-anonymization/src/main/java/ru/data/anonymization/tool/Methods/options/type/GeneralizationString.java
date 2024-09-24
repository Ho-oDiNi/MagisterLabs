package ru.data.anonymization.tool.Methods.options.type;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.data.anonymization.tool.Methods.options.MaskItem;
import ru.data.anonymization.tool.service.DatabaseConnectionService;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

@Data
@NoArgsConstructor
public class GeneralizationString implements MaskItem {
    private String nameTable;
    private String nameColumn;
    private String generalizationTable;
    private HashMap<String, String> value;

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
        controllerDB.execute("DROP TABLE IF EXISTS "+generalizationTable+";");
        controllerDB.execute("CREATE TABLE "+generalizationTable+"(generalization text,value text);");
        value.forEach((key, value) -> {
            try {
                controllerDB.execute("UPDATE "+nameTable+" SET "+nameColumn+"='"+value+"' WHERE "+nameColumn+"='"+key+"';");
                controllerDB.execute("INSERT INTO "+generalizationTable+" VALUES ('"+value+"', '"+key+"');");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}