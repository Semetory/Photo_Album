package com.example.builder;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Text;
import javafx.scene.control.Label;

public class CircularIndicatorBuilder implements IndicatorBuilder {
    private Indicator indicator;
    private double startValue;
    private double endValue;
    private double currentValue;
    private String title;
    private int segments;
    private boolean showScale;
    private boolean showMarkers;

    public CircularIndicatorBuilder() {
        this.indicator = new Indicator();
        this.showScale = true;
        this.showMarkers = true;
    }

    @Override
    public void setView(int segments, char normalChar, char selectedChar) {
        this.segments = segments;
    }

    @Override
    public void setBounds(double start, double end) {
        this.startValue = start;
        this.endValue = end;
    }

    @Override
    public void setCurrentValue(double value) {
        this.currentValue = value;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void setScaleVisible(boolean visible) {
        this.showScale = visible;
    }

    @Override
    public void setMarkersVisible(boolean visible) {
        this.showMarkers = visible;
    }

    @Override
    public Indicator build() {
        indicator.clear();

        // Добавляем заголовок
        if (title != null && !title.isEmpty()) {
            Label titleLabel = new Label(title);
            titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            indicator.addPane(new HBox(titleLabel));
        }

        // Создаем круговой индикатор
        VBox circleContainer = new VBox(5);

        double range = endValue - startValue;
        double percentage = Math.min(1.0, Math.max(0.0, (currentValue - startValue) / range));
        double angle = 360 * percentage;

        // Внешний круг (фон)
        Circle backgroundCircle = new Circle(50);
        backgroundCircle.setFill(Color.TRANSPARENT);
        backgroundCircle.setStroke(Color.LIGHTGRAY);
        backgroundCircle.setStrokeWidth(10);

        // Дуга прогресса
        Arc progressArc = new Arc(0, 0, 45, 45, 90, -angle);
        progressArc.setType(ArcType.OPEN);
        progressArc.setStroke(Color.LIGHTGREEN);
        progressArc.setStrokeWidth(10);
        progressArc.setFill(Color.TRANSPARENT);

        HBox circleBox = new HBox();
        circleBox.getChildren().addAll(backgroundCircle, progressArc);
        circleContainer.getChildren().add(circleBox);

        // Добавляем текстовое значение
        Text valueText = new Text(String.format("%.1f", currentValue));
        valueText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        circleContainer.getChildren().add(valueText);

        indicator.addPane(circleContainer);

        return indicator;
    }
}
