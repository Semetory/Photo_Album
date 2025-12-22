package com.example.iterator;

import com.example.iterator.model.ImageWithEmotions;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;
import java.io.File;
import com.example.iterator.emotions.*;
import com.example.iterator.emotions.factory.EmotionFactory;
import com.example.iterator.emotions.EmotionManager;
import com.example.iterator.model.*;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

public class HelloController {
    @FXML private ImageView imageView;
    @FXML private Label statusLabel;
    @FXML private ComboBox<String> formatCombo;
    @FXML private Pane emotionPane;

    private ImageAggregate slides;
    private ImageIterator iterator;
    private Timeline timeline;
    private EmotionFactory emotionFactory;
    private EmotionManager emotionManager;
    private int slideDelay = 2;

    @FXML
    public void initialize() {
        formatCombo.getItems().addAll("Все изображения", "PNG", "JPEG", "GIF", "BMP");
        formatCombo.getSelectionModel().selectFirst();

        emotionFactory = EmotionFactory.getFactory("advanced");
        emotionManager = EmotionManager.getInstance();

        //обработчик кликов для добавления эмоций
        imageView.setOnMouseClicked(event -> {
            if (iterator != null) {
                showEmotionMenu(event.getX(), event.getY());
            }
        });
    }

    @FXML
    private void chooseDirectory(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Выберите папку с изображениями");
        File directory = chooser.showDialog(imageView.getScene().getWindow());

        if (directory != null && directory.exists()) {
            String filter = formatCombo.getValue();
            slides = new EnhancedImageAggregate(directory.getAbsolutePath(), filter);
            ((EnhancedImageAggregate) slides).loadImages(); //Загружаем изображения

            if (slides.size() > 0) {
                iterator = slides.getIterator();
                showNext();
                statusLabel.setText("Загружено: " + slides.size() + " изображений");
            } else {
                statusLabel.setText("В выбранной папке нет изображений");
            }
        }
    }

    @FXML
    private void startSlideShow(ActionEvent event) {
        if (iterator == null || slides.size() == 0) {
            statusLabel.setText("Сначала выберите папку с изображениями!");
            return;
        }

        stopSlideShow(null);
        timeline = new Timeline(new KeyFrame(Duration.seconds(slideDelay), e -> showNext()));
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

    @FXML
    private void nextImage(ActionEvent event) {
        showNext();
    }

    @FXML
    private void prevImage(ActionEvent event) {
        showPrev();
    }

    private void showNext() {
        if (iterator != null && iterator.hasNext()) {
            ImageWithEmotions image = iterator.next();
            loadImageWithEmotions(image);
        }
    }

    private void showPrev() {
        if (iterator != null) {
            ImageWithEmotions image = iterator.preview();
            loadImageWithEmotions(image);
        }
    }

    private void loadImageWithEmotions(ImageWithEmotions image) {
        if (image != null) {

            javafx.scene.image.Image fxImage = new javafx.scene.image.Image(
                    image.getImageFile().toURI().toString());
            imageView.setImage(fxImage);

            emotionPane.getChildren().clear();

            for (Emotion emotion : image.getEmotions()) {
                displayEmotion(emotion);
            }

            statusLabel.setText(image.getFileName() + " - " + iterator.getProgress() +
                    " (эмоций: " + image.getEmotions().size() + ")");
        }
    }

    private void displayEmotion(Emotion emotion) {
        double x = emotion.getPositionX() * imageView.getFitWidth();
        double y = emotion.getPositionY() * imageView.getFitHeight();

        Label emotionLabel = new Label(emotion.getDisplayText());
        emotionLabel.setStyle("-fx-background-color: rgba(255,255,255,0.8); " +
                "-fx-background-radius: 15; -fx-padding: 5 10; " +
                "-fx-font-size: 16px;");
        emotionLabel.setLayoutX(x - 20);
        emotionLabel.setLayoutY(y - 15);

        emotionLabel.setOnMouseClicked(event -> {
            if (event.isControlDown()) {
                iterator.removeEmotionFromCurrent(emotion.getId());
                emotionPane.getChildren().remove(emotionLabel);
            }
        });

        emotionPane.getChildren().add(emotionLabel);
    }

    private void showEmotionMenu(double x, double y) {
        ContextMenu emotionMenu = new ContextMenu();

        MenuItem happyItem = new MenuItem("😊 Счастливый");
        happyItem.setOnAction(e -> addEmotion(x, y, "smiley", SmileyEmotion.SmileyType.HAPPY));

        MenuItem sadItem = new MenuItem("😢 Грустный");
        sadItem.setOnAction(e -> addEmotion(x, y, "smiley", SmileyEmotion.SmileyType.SAD));

        MenuItem loveItem = new MenuItem("❤️ Любовь");
        loveItem.setOnAction(e -> addEmotion(x, y, "smiley", SmileyEmotion.SmileyType.LOVE));

        MenuItem textItem = new MenuItem("💬 Текст");
        textItem.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog("Круто!");
            dialog.setTitle("Добавить текстовую эмоцию");
            dialog.setHeaderText("Введите текст эмоции:");
            dialog.showAndWait().ifPresent(text -> {
                addEmotion(x, y, "text", text);
            });
        });

        emotionMenu.getItems().addAll(happyItem, sadItem, loveItem, textItem);
        emotionMenu.show(imageView.getScene().getWindow(),
                imageView.localToScreen(x, y).getX(),
                imageView.localToScreen(x, y).getY());
    }

    private void addEmotion(double x, double y, String type, Object... params) {
        if (iterator != null) {
            double normX = x / imageView.getFitWidth();
            double normY = y / imageView.getFitHeight();

            Emotion emotion = emotionFactory.createEmotion(
                    "user1", type, params[0], normX, normY
            );

            emotionManager.saveEmotion(iterator.getCurrentFile(), emotion);
            iterator.addEmotionToCurrent(emotion);
            displayEmotion(emotion);

            statusLabel.setText("Эмоция добавлена!");
        }
    }
}