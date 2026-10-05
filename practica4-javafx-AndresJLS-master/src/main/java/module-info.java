module com.example.crudjavafx {
    requires javafx.controls;
    requires javafx.fxml;


    requires javafx.graphics;
    requires java.sql;


    opens com.example.crudjavafx to javafx.fxml;
    exports com.example.crudjavafx;
}