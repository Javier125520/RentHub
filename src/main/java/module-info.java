module org.example.renthub {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.example.renthub to javafx.fxml;
    exports org.example.renthub;
}