package ru.data.anonymization.tool.Methods.options.type;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.data.anonymization.tool.Methods.options.MaskItem;
import ru.data.anonymization.tool.service.DatabaseConnectionService;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.List;

@Data
@NoArgsConstructor
public class ValueReplacementByPattern implements MaskItem {
    private String nameTable;
    private String nameColumn;
    private String regex;
    private String replacement;

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
        controllerDB.execute("ALTER TABLE "+nameTable+" ADD COLUMN masking_method_temp_id INT GENERATED BY DEFAULT AS IDENTITY UNIQUE;");
        ResultSet resultSet = controllerDB.executeQuery("SELECT "+nameColumn+", masking_method_temp_id FROM "+nameTable+";");

        String newValue;
        while(resultSet.next()){
            try {
                newValue = resultSet.getString(1).replaceAll(regex,replacement);
                resultSet.updateString(1, newValue);
                resultSet.updateRow();
            }catch (Exception ignored){}
        }
        controllerDB.execute("ALTER TABLE "+nameTable+" DROP COLUMN masking_method_temp_id;");
    }
}