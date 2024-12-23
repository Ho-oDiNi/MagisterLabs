module ru.atlasyk {
    requires javafx.controls;
    requires javafx.fxml;


    opens ru.atlasyk to javafx.fxml;
    exports ru.atlasyk;
}