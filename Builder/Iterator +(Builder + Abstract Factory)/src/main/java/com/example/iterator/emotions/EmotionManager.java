package com.example.iterator.emotions;

import com.example.iterator.emotions.Emotion;
import com.example.iterator.model.ImageWithEmotions;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.example.iterator.emotions.Emotion;
import com.example.iterator.model.ImageWithEmotions;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.util.*;

public class EmotionManager {
    private static final String EMOTIONS_FILE = "emotions_data.json";
    private static EmotionManager instance;
    private final ObjectMapper objectMapper;

    // Храним эмоции по хэшу изображения
    private final Map<String, List<Emotion>> emotionsByImageHash = new HashMap<>();

    private EmotionManager() {
        objectMapper = new ObjectMapper();
        objectMapper.enable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT);
        loadEmotions();
    }

    public static EmotionManager getInstance() {
        if (instance == null) {
            instance = new EmotionManager();
        }
        return instance;
    }

    public void saveEmotion(File imageFile, Emotion emotion) {
        // Создаем временный ImageWithEmotions для расчета хэша
        ImageWithEmotions tempImage = new ImageWithEmotions.Builder(imageFile).build();
        String imageHash = tempImage.getImageHash();

        List<Emotion> emotions = emotionsByImageHash.getOrDefault(imageHash, new ArrayList<>());
        emotions.add(emotion);
        emotionsByImageHash.put(imageHash, emotions);

        saveEmotions();
    }

    public List<Emotion> getEmotions(File imageFile) {
        ImageWithEmotions tempImage = new ImageWithEmotions.Builder(imageFile).build();
        String imageHash = tempImage.getImageHash();

        List<Emotion> emotions = emotionsByImageHash.get(imageHash);
        if (emotions != null) {
            return new ArrayList<>(emotions);
        }

        // Попробуем найти по имени файла
        for (Map.Entry<String, List<Emotion>> entry : emotionsByImageHash.entrySet()) {
            for (Emotion emotion : entry.getValue()) {
                // Если в эмоции сохранен путь к файлу
                if (emotion instanceof com.example.iterator.emotions.SmileyEmotion) {
                    // Проверка по имени файла
                    String storedFileName = ((com.example.iterator.emotions.SmileyEmotion) emotion)
                            .getMetadata().get("fileName");
                    if (storedFileName != null && storedFileName.equals(imageFile.getName())) {
                        return new ArrayList<>(entry.getValue());
                    }
                }
            }
        }

        return new ArrayList<>();
    }

    public void removeEmotion(File imageFile, String emotionId) {
        ImageWithEmotions tempImage = new ImageWithEmotions.Builder(imageFile).build();
        String imageHash = tempImage.getImageHash();

        List<Emotion> emotions = emotionsByImageHash.get(imageHash);
        if (emotions != null) {
            emotions.removeIf(e -> e.getId().equals(emotionId));
            saveEmotions();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadEmotions() {
        try {
            File file = new File(EMOTIONS_FILE);
            if (file.exists()) {
                Map<String, Object> data = objectMapper.readValue(file, Map.class);

                // Десериализация эмоций
                Map<String, List<Map<String, Object>>> savedEmotions =
                        (Map<String, List<Map<String, Object>>>) data.get("emotions");

                if (savedEmotions != null) {
                    for (Map.Entry<String, List<Map<String, Object>>> entry : savedEmotions.entrySet()) {
                        List<Emotion> emotions = new ArrayList<>();
                        for (Map<String, Object> emotionData : entry.getValue()) {
                            // Простая десериализация - в реальном приложении нужно использовать
                            // ObjectMapper с правильными настройками
                            emotions.add(createEmotionFromMap(emotionData));
                        }
                        emotionsByImageHash.put(entry.getKey(), emotions);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading emotions: " + e.getMessage());
        }
    }

    private Emotion createEmotionFromMap(Map<String, Object> data) {
        // Упрощенная десериализация
        // В реальном приложении используйте ObjectMapper с аннотациями @JsonSubTypes
        String type = (String) data.get("type");

        if ("smiley".equals(type)) {
            SmileyEmotion emotion = new SmileyEmotion();
            emotion.setId((String) data.get("id"));
            emotion.setUserId((String) data.get("userId"));
            emotion.setTimestamp((Long) data.get("timestamp"));
            emotion.setPositionX((Double) data.get("positionX"));
            emotion.setPositionY((Double) data.get("positionY"));
            emotion.setSmileyType(SmileyEmotion.SmileyType.valueOf((String) data.get("smileyType")));
            return emotion;
        }

        return null;
    }

    private void saveEmotions() {
        try {
            Map<String, Object> saveData = new HashMap<>();
            saveData.put("version", "1.0");
            saveData.put("savedAt", new Date().toString());

            // Конвертируем эмоции в формат для сериализации
            Map<String, List<Map<String, Object>>> serializableEmotions = new HashMap<>();
            for (Map.Entry<String, List<Emotion>> entry : emotionsByImageHash.entrySet()) {
                List<Map<String, Object>> emotionList = new ArrayList<>();
                for (Emotion emotion : entry.getValue()) {
                    emotionList.add(convertEmotionToMap(emotion));
                }
                serializableEmotions.put(entry.getKey(), emotionList);
            }

            saveData.put("emotions", serializableEmotions);
            objectMapper.writeValue(new File(EMOTIONS_FILE), saveData);

        } catch (IOException e) {
            System.err.println("Error saving emotions: " + e.getMessage());
        }
    }

    private Map<String, Object> convertEmotionToMap(Emotion emotion) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", emotion.getType());
        map.put("id", emotion.getId());
        map.put("userId", emotion.getUserId());
        map.put("timestamp", emotion.getTimestamp());
        map.put("positionX", emotion.getPositionX());
        map.put("positionY", emotion.getPositionY());

        if (emotion instanceof SmileyEmotion) {
            map.put("smileyType", ((SmileyEmotion) emotion).getSmileyType().name());
        }

        return map;
    }
}
