package com.example.builder;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HelloController {
    @FXML private ComboBox<String> indicatorType;
    @FXML private TextField startValue;
    @FXML private TextField endValue;
    @FXML private TextField currentValue;
    @FXML private TextField title;
    @FXML private CheckBox showScale;
    @FXML private CheckBox showMarkers;
    @FXML private VBox indicatorContainer;

    private IndicatorDirector director = new IndicatorDirector();

    @FXML
    public void initialize() {
        indicatorType.getSelectionModel().selectFirst();
        ObservableList<String> types = FXCollections.observableArrayList(
                "Линейный",
                "Круговой"
        );
        indicatorType.setItems(types);
        indicatorType.getSelectionModel().selectFirst();
    }

    @FXML
    private void createIndicator() {
        try {
            double start = Double.parseDouble(startValue.getText());
            double end = Double.parseDouble(endValue.getText());
            double current = Double.parseDouble(currentValue.getText());
            String indicatorTitle = title.getText();
            String type = indicatorType.getValue();

            IndicatorBuilder builder;

            if ("Круговой".equals(type)) {
                builder = new CircularIndicatorBuilder();
            } else {
                builder = new LinearIndicatorBuilder();
            }

            Indicator indicator = director.constructProgressIndicator(builder, start, end, current, indicatorTitle);
            indicator.show(indicatorContainer);

        } catch (NumberFormatException e) {
            showAlert("Ошибка", "Введите корректные числовые значения");
        }
    }

    @FXML
    private void clearIndicators() {
        indicatorContainer.getChildren().clear();
    }

    // Пример создания индикатора с датами
    public void createDateIndicator() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            Date startDate = sdf.parse("01.01.2024");
            Date endDate = sdf.parse("31.12.2024");
            Date currentDate = new Date();

            // Преобразуем даты в дни для индикатора
            long totalDays = (endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24);
            long passedDays = (currentDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24);

            IndicatorBuilder builder = new LinearIndicatorBuilder();
            builder.setBounds(0, (double) totalDays);
            builder.setCurrentValue((double) passedDays);
            builder.setTitle("Прогресс года");
            builder.setScaleVisible(true);

            Indicator indicator = builder.build();
            indicator.show(indicatorContainer);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
