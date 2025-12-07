module com.example.iterator {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;

    opens com.example.iterator to javafx.fxml;
    exports com.example.iterator;
}