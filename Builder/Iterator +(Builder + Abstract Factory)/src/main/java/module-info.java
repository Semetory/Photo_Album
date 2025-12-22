module com.example.iterator {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;

    opens com.example.iterator to javafx.fxml;
    exports com.example.iterator;
    exports com.example.iterator.emotions;
    opens com.example.iterator.emotions to javafx.fxml;
    exports com.example.iterator.emotions.factory;
    opens com.example.iterator.emotions.factory to javafx.fxml;
}