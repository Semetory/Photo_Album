package com.example.builder;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.control.Label;

public class LinearIndicatorBuilder implements IndicatorBuilder {
    private Indicator indicator;
    private double startValue;
    private double endValue;
    private double currentValue;
    private String title;
    private int segments;
    private boolean showScale;
    private boolean showMarkers;

    public LinearIndicatorBuilder() {
        this.indicator = new Indicator();
        this.showScale = true;
        this.showMarkers = true;
        this.segments = 10;
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

        // Создаем линейный индикатор
        HBox progressBar = new HBox(2);
        double range = endValue - startValue;
        double filledPercentage = Math.min(1.0, Math.max(0.0, (currentValue - startValue) / range));

        // Заполненная часть
        Rectangle filled = new Rectangle(200 * filledPercentage, 20);
        filled.setFill(Color.LIGHTGREEN);
        filled.setArcWidth(10);
        filled.setArcHeight(10);

        // Незаполненная часть
        Rectangle unfilled = new Rectangle(200 * (1 - filledPercentage), 20);
        unfilled.setFill(Color.LIGHTGRAY);
        unfilled.setArcWidth(10);
        unfilled.setArcHeight(10);

        progressBar.getChildren().addAll(filled, unfilled);
        indicator.addPane(progressBar);

        // Добавляем текстовое значение
        Text valueText = new Text(String.format("%.1f", currentValue));
        valueText.setStyle("-fx-font-size: 12px;");
        indicator.addPane(new HBox(valueText));

        // Добавляем шкалу, если нужно
        if (showScale) {
            HBox scale = new HBox();
            Text startText = new Text(String.format("%.1f", startValue));
            Text endText = new Text(String.format("%.1f", endValue));
            scale.getChildren().addAll(startText, new Text(" — "), endText);
            scale.setSpacing(150);
            indicator.addPane(scale);
        }

        return indicator;
    }
}
