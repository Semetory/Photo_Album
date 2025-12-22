package com.example.iterator;

import com.example.iterator.model.Iterator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;

import java.io.File;

public class HelloController {
    @FXML private ImageView imageView;
    @FXML private ComboBox<String> formatCombo;
    @FXML private Label statusLabel;

    private ConcreteAggregate slides;
    private Iterator iterator;
    private Timeline timeline;
    private int slideDelay = 2;
    private int currentIndex = -1; //индекс в контроллере
    private int totalImages = 0;

    @FXML
    public void initialize() {
        formatCombo.getItems().addAll(
                "Все изображения",
                "PNG", "JPEG",
                "GIF", "BMP"
        );
        formatCombo.getSelectionModel().selectFirst();
    }

    @FXML
    private void chooseDirectory(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Выберите папку с изображениями");
        File directory = chooser.showDialog(imageView.getScene().getWindow());

        if (directory != null && directory.exists()) {
            String filter = formatCombo.getValue();
            slides = new ConcreteAggregate(directory.getAbsolutePath(), filter);
            iterator = slides.getIterator();
            totalImages = slides.getFileCount();
            currentIndex = -1;

            statusLabel.setText("Найдено изображений: " + totalImages);

            if (totalImages > 0) { showNext(); }
        }
    }

    @FXML
    private void startSlideShow(ActionEvent event) {
        if (iterator == null || totalImages == 0) {
            statusLabel.setText("Сначала выберите папку с изображениями!");
            return;
        }

        stopSlideShow(null);

        timeline = new Timeline(
                new KeyFrame(Duration.seconds(slideDelay), e -> showNext())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        statusLabel.setText("Слайд-шоу запущено");
    }

    @FXML
    private void stopSlideShow(ActionEvent event) {
        if (timeline != null) {
            timeline.stop();
            statusLabel.setText("Слайд-шоу остановлено");
        }
    }

    @FXML private void nextImage(ActionEvent event) { showNext(); }
    @FXML private void prevImage(ActionEvent event) { showPrev(); }

    private void showNext() {
        if (iterator != null && iterator.hasNext()) {
            Object obj = iterator.next();
            if (obj instanceof File) {
                File file = (File) obj;
                currentIndex++;
                if (currentIndex >= totalImages) currentIndex = 0;

                Image image = loadImage(file);
                if (image != null) {
                    imageView.setImage(image);
                    statusLabel.setText("Изображение " + (currentIndex + 1) + "/" + totalImages +
                            ": " + file.getName());
                }
            }
        }
    }

    private void showPrev() {
        if (iterator != null) {
            Object obj = iterator.preview();
            if (obj instanceof File) {
                File file = (File) obj;
                currentIndex--;
                if (currentIndex < 0) currentIndex = totalImages - 1;

                Image image = loadImage(file);
                if (image != null) {
                    imageView.setImage(image);
                    statusLabel.setText("Изображение " + (currentIndex + 1) + "/" + totalImages +
                            ": " + file.getName());
                }
            }
        }
    }

    private Image loadImage(File file) {
        try {
            if (!file.exists() || file.length() == 0) {
                return null;
            }
            return new Image(file.toURI().toString());
        } catch (Exception e) {
            return null;
        }
    }
}