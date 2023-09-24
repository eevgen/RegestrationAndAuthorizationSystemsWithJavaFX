module com.example.javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.javafx to javafx.fxml;
    exports com.example.javafx;
    exports com.example.javafx.controllers;
    opens com.example.javafx.controllers to javafx.fxml;
    exports com.example.javafx.DBConnection;
    opens com.example.javafx.DBConnection to javafx.fxml;
}