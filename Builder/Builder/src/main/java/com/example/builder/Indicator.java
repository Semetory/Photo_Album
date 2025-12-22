package com.example.builder;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class Indicator {
    private VBox container;

    public Indicator() {
        this.container = new VBox(10);
        container.setStyle("-fx-padding: 10px; -fx-border-color: #ccc; -fx-border-radius: 5px;");
    }

    public void addPane(Pane pane) {
        container.getChildren().add(pane);
    }

    public void show(Pane parentPane) {
        parentPane.getChildren().add(container);
    }

    public void clear() {
        container.getChildren().clear();
    }

    public VBox getContainer() {
        return container;
    }
}