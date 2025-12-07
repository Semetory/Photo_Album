package com.example.iterator;

import com.example.model.ConcreteAggregate.ConcreteAggregate;
import com.example.iterator.model.Iterator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;

public class HelloController {

    @FXML
    private ImageView imageView;

    @FXML
    private Button btnStart, btnStop, btnChooseFolder;

    private ConcreteAggregate slides;
    private Iterator iter;
    private Timeline timeline;
    private int slideDuration = 1000; // время показа слайда в миллисекундах

    @FXML
    public void initialize() {
        // Инициализация слайд-шоу по умолчанию (папка resources/img)
        slides = new ConcreteAggregate("img", ".png", ".jpg");
        iter = slides.getIterator();

        // Таймлайн для автоматической смены слайдов
        timeline = new Timeline(new KeyFrame(Duration.millis(slideDuration), this::showNextSlide));
        timeline.setCycleCount(Timeline.INDEFINITE);

        // Показываем первый слайд сразу
        if (iter.hasNext(1)) {
            imageView.setImage((Image) iter.next());
        }
    }

    /** Старт слайд-шоу */
    @FXML
    private void handleStart(ActionEvent event) {
        timeline.play();
    }

    /** Остановка слайд-шоу */
    @FXML
    private void handleStop(ActionEvent event) {
        timeline.stop();
    }

    /** Выбор папки с изображениями */
    @FXML
    private void handleChooseFolder(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Выберите папку с изображениями");
        Stage stage = (Stage) imageView.getScene().getWindow();
        File selectedDir = directoryChooser.showDialog(stage);

        if (selectedDir != null && selectedDir.isDirectory()) {
            slides = new ConcreteAggregate(getRelativePath(selectedDir), ".png", ".jpg");
            iter = slides.getIterator();

            // Показываем первый слайд выбранной папки
            if (iter.hasNext(1)) {
                imageView.setImage((Image) iter.next());
            }
        }
    }

    /** Переход к следующему слайду */
    private void showNextSlide(ActionEvent event) {
        if (iter.hasNext(1)) {
            Image image = (Image) iter.next();
            imageView.setImage(image);
        }
    }

    /** Преобразование абсолютного пути к относительному для resources */
    private String getRelativePath(File folder) {
        // Предполагаем, что папка находится в src/main/resources
        String path = folder.getAbsolutePath();
        int index = path.indexOf("resources");
        if (index != -1) {
            return path.substring(index + "resources/".length()).replace("\\", "/");
        }
        return folder.getName(); // fallback
    }
}
