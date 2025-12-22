package com.example.iterator.model;

import com.example.iterator.emotions.Emotion;
import java.util.List;
package com.example.iterator.model;

import com.example.iterator.emotions.Emotion;
import java.util.List;
import java.util.Map;

/**
 * Специализированный интерфейс агрегата для работы с изображениями.
 */
public interface ImageAggregate extends Aggregate {

    /**
     * Получить итератор для изображений.
     * @return ImageIterator
     */
    @Override
    ImageIterator getIterator();

    /**
     * Получить изображение по индексу.
     * @param index индекс изображения
     * @return ImageWithEmotions объект
     */
    ImageWithEmotions getImage(int index);

    /**
     * Найти изображение по хэшу.
     * @param hash хэш изображения
     * @return ImageWithEmotions или null если не найдено
     */
    ImageWithEmotions findByHash(String hash);

    /**
     * Найти изображение по имени файла.
     * @param fileName имя файла
     * @return список найденных изображений
     */
    List<ImageWithEmotions> findByName(String fileName);

    /**
     * Получить все изображения с эмоциями определенного типа.
     * @param emotionType тип эмоции
     * @return список изображений
     */
    List<ImageWithEmotions> getImagesWithEmotionType(String emotionType);

    /**
     * Добавить эмоцию к изображению по индексу.
     * @param index индекс изображения
     * @param emotion эмоция для добавления
     */
    void addEmotion(int index, Emotion emotion);

    /**
     * Добавить эмоцию к изображению по хэшу.
     * @param hash хэш изображения
     * @param emotion эмоция для добавления
     * @return true если эмоция была добавлена
     */
    boolean addEmotionByHash(String hash, Emotion emotion);

    /**
     * Удалить эмоцию с изображения.
     * @param imageHash хэш изображения
     * @param emotionId ID эмоции
     * @return true если эмоция была удалена
     */
    boolean removeEmotion(String imageHash, String emotionId);

    /**
     * Получить статистику по коллекции.
     * @return Map со статистикой
     */
    Map<String, Object> getStatistics();

    /**
     * Получить изображения, отсортированные по количеству эмоций.
     * @return отсортированный список изображений
     */
    List<ImageWithEmotions> getImagesSortedByEmotionCount();

    /**
     * Получить изображения, отсортированные по дате изменения.
     * @return отсортированный список изображений
     */
    List<ImageWithEmotions> getImagesSortedByDate();

    /**
     * Получить общее количество изображений.
     * @return количество изображений
     */
    int size();

    /**
     * Проверить, пуста ли коллекция.
     * @return true если коллекция пуста
     */
    boolean isEmpty();

    /**
     * Обновить путь к файлу при перемещении.
     * @param oldFile старый файл
     * @param newFile новый файл
     * @return true если обновление успешно
     */
    boolean updateImageFile(java.io.File oldFile, java.io.File newFile);

    /**
     * Очистить все эмоции со всех изображений.
     */
    void clearAllEmotions();

    /**
     * Экспортировать все данные.
     * @return список данных всех изображений
     */
    List<Map<String, Object>> exportAllData();
}