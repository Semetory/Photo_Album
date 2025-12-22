package com.example.iterator.model;

import com.example.iterator.emotions.Emotion;
import java.util.List;

import java.util.Map;

public interface ImageAggregate extends Aggregate {

    @Override
    ImageIterator getIterator();
    ImageWithEmotions getImage(int index);
    ImageWithEmotions findByHash(String hash);
    List<ImageWithEmotions> findByName(String fileName);
    List<ImageWithEmotions> getImagesWithEmotionType(String emotionType);

    void addEmotion(int index, Emotion emotion);

    boolean addEmotionByHash(String hash, Emotion emotion);
    boolean removeEmotion(String imageHash, String emotionId);

    Map<String, Object> getStatistics();

    List<ImageWithEmotions> getImagesSortedByEmotionCount();
    List<ImageWithEmotions> getImagesSortedByDate();

    int size();

    boolean isEmpty();
    boolean updateImageFile(java.io.File oldFile, java.io.File newFile);

    void clearAllEmotions();

    List<Map<String, Object>> exportAllData();
}