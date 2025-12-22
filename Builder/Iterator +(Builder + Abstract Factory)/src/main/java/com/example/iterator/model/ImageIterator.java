package com.example.iterator.model;

import com.example.iterator.emotions.Emotion;
import javafx.scene.image.Image;
import java.io.File;
import java.util.List;

/**
 * Специализированный интерфейс итератора для работы с изображениями.
 * Расширяет базовый интерфейс Iterator.
 */
public interface ImageIterator extends Iterator {

    // Основные методы из базового интерфейса с конкретным типом возврата
    @Override
    ImageWithEmotions next();

    @Override
    ImageWithEmotions preview();

    /**
     * Получить текущее изображение.
     * @return текущий ImageWithEmotions
     */
    ImageWithEmotions current();

    /**
     * Получить текущее изображение как JavaFX Image.
     * @return JavaFX Image объект
     */
    Image getCurrentAsJavaFXImage();

    /**
     * Получить текущий файл изображения.
     * @return File объект
     */
    File getCurrentFile();

    /**
     * Получить имя текущего файла.
     * @return имя файла
     */
    String getCurrentFileName();

    /**
     * Получить эмоции текущего изображения.
     * @return список эмоций
     */
    List<Emotion> getCurrentEmotions();

    /**
     * Добавить эмоцию к текущему изображению.
     * @param emotion эмоция для добавления
     */
    void addEmotionToCurrent(Emotion emotion);

    /**
     * Удалить эмоцию из текущего изображения.
     * @param emotionId ID эмоции для удаления
     * @return true если эмоция была удалена
     */
    boolean removeEmotionFromCurrent(String emotionId);

    /**
     * Получить количество изображений в коллекции.
     * @return общее количество изображений
     */
    int getTotalCount();

    /**
     * Получить текущий индекс.
     * @return индекс текущего элемента (0-based)
     */
    int getCurrentIndex();

    /**
     * Получить текущий прогресс.
     * @return строка в формате "X / Y"
     */
    default String getProgress() {
        return (getCurrentIndex() + 1) + " / " + getTotalCount();
    }

    /**
     * Перейти к изображению по индексу.
     * @param index индекс для перехода
     * @return true если переход успешен
     */
    boolean goTo(int index);

    /**
     * Перейти к первому изображению.
     */
    void first();

    /**
     * Перейти к последнему изображению.
     */
    void last();

    /**
     * Проверить, есть ли предыдущий элемент.
     * @return true если есть предыдущий элемент
     */
    boolean hasPreview();

    /**
     * Сбросить итератор в начальное состояние.
     */
    void reset();
}
