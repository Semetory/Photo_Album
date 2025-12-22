package com.example.iterator.model;

import com.example.iterator.emotions.Emotion;
import javafx.scene.image.Image;
import java.io.File;
import java.util.List;

public interface ImageIterator extends Iterator {

    //методы из базового интерфейса с конкретным типом возврата
    @Override
    ImageWithEmotions next();

    @Override
    ImageWithEmotions preview();

    ImageWithEmotions current();
    Image getCurrentAsJavaFXImage();
    File getCurrentFile();
    String getCurrentFileName();
    List<Emotion> getCurrentEmotions();

    void addEmotionToCurrent(Emotion emotion);

    boolean removeEmotionFromCurrent(String emotionId);

    int getTotalCount();
    int getCurrentIndex();

    default String getProgress() {
        return (getCurrentIndex() + 1) + " / " + getTotalCount();
    }

    boolean goTo(int index);

    void first();
    void last();

    boolean hasPreview();

    void reset();
}
