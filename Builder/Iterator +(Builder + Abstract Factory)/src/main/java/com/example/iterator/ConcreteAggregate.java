package com.example.iterator;

import com.example.iterator.emotions.Emotion;
import com.example.iterator.emotions.EmotionManager;
import com.example.iterator.emotions.ImageWithEmotions;
import com.example.iterator.model.Aggregate;

import java.io.File;
import java.util.*;

public class ConcreteAggregate implements Aggregate {
    private List<ImageWithEmotions> images = new ArrayList<>();
    private EmotionManager emotionManager;

    public ConcreteAggregate(String directoryPath, String filter) {
        this.emotionManager = EmotionManager.getInstance();
        loadImages(new File(directoryPath), filter);
    }

    private void loadImages(File directory, String filter) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        loadImages(file, filter);
                    } else if (matchesFilter(file.getName(), filter)) {
                        // Используем Builder для создания ImageWithEmotions
                        ImageWithEmotions image = new ImageWithEmotions.Builder(file)
                                .withEmotions(emotionManager.getEmotions(file))
                                .withMetadata("loadedAt", new Date())
                                .build();

                        images.add(image);
                    }
                }
            }
        }
    }

    private boolean matchesFilter(String fileName, String filter) {
        String lowerName = fileName.toLowerCase();
        if (filter.equals("Все изображения")) {
            return lowerName.endsWith(".png") ||
                    lowerName.endsWith(".jpg") ||
                    lowerName.endsWith(".jpeg") ||
                    lowerName.endsWith(".gif") ||
                    lowerName.endsWith(".bmp");
        }
        return lowerName.endsWith("." + filter.toLowerCase());
    }

    @Override
    public Iterator getIterator() {
        return new EnhancedImageIterator();
    }

    public void addEmotionToCurrentImage(Emotion emotion) {
        // Логика добавления эмоции к текущему изображению
    }

    private class EnhancedImageIterator implements Iterator {
        private int current = -1;

        @Override
        public boolean hasNext() {
            return !images.isEmpty() && current < images.size() - 1;
        }

        @Override
        public Object next() {
            if (images.isEmpty()) return null;

            if (hasNext()) {
                current++;
            } else {
                current = 0;
            }

            return images.get(current);
        }

        @Override
        public Object preview() {
            if (images.isEmpty()) return null;

            if (current > 0) {
                current--;
            } else {
                current = images.size() - 1;
            }

            return images.get(current);
        }

        public ImageWithEmotions getCurrentImage() {
            if (current >= 0 && current < images.size()) {
                return images.get(current);
            }
            return null;
        }
    }
}