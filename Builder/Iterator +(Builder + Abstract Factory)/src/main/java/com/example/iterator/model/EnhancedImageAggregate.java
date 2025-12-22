package com.example.iterator.model;

import com.example.iterator.emotions.Emotion;
import java.io.File;
import java.io.FilenameFilter;
import java.util.*;
import javafx.scene.image.Image;
import java.util.*;

public class EnhancedImageAggregate implements ImageAggregate {

    private final List<ImageWithEmotions> images = new ArrayList<>();
    private final Map<String, ImageWithEmotions> hashMap = new HashMap<>();
    private final Map<File, ImageWithEmotions> fileMap = new HashMap<>();
    private final String sourceDirectory;
    private final String filter;
    private boolean loaded = false;

    public EnhancedImageAggregate(String directoryPath, String filter) {
        this.sourceDirectory = directoryPath;
        this.filter = filter;
    }

    public void loadImages() {
        if (loaded) return;

        File directory = new File(sourceDirectory);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("Invalid directory: " + sourceDirectory);
        }

        loadImagesRecursive(directory);
        loaded = true;
    }

    private void loadImagesRecursive(File directory) {
        FilenameFilter filenameFilter = (dir, name) -> {
            File file = new File(dir, name);
            if (name.startsWith(".")) return false;
            if (file.isDirectory()) return true;
            return matchesFilter(name, filter);
        };

        File[] files = directory.listFiles(filenameFilter);
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                loadImagesRecursive(file);
            } else {
                try {
                    ImageWithEmotions image = new ImageWithEmotions.Builder(file).build();
                    images.add(image);
                    hashMap.put(image.getImageHash(), image);
                    fileMap.put(file, image);
                } catch (Exception e) {
                    System.err.println("Error loading image: " + file.getAbsolutePath());
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
        } else if (filter.equals("PNG")) {
            return lowerName.endsWith(".png");
        } else if (filter.equals("JPEG")) {
            return lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg");
        } else if (filter.equals("GIF")) {
            return lowerName.endsWith(".gif");
        } else if (filter.equals("BMP")) {
            return lowerName.endsWith(".bmp");
        }
        return false;
    }

    //ImageAggregate интерфейс

    @Override
    public ImageIterator getIterator() {
        return new EnhancedImageIterator();
    }

    @Override
    public ImageWithEmotions getImage(int index) {
        if (index < 0 || index >= images.size()) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + images.size());
        }
        return images.get(index);
    }

    @Override
    public ImageWithEmotions findByHash(String hash) {
        return hashMap.get(hash);
    }

    @Override
    public List<ImageWithEmotions> findByName(String fileName) {
        List<ImageWithEmotions> result = new ArrayList<>();
        for (ImageWithEmotions image : images) {
            if (image.getFileName().equalsIgnoreCase(fileName)) {
                result.add(image);
            }
        }
        return result;
    }

    @Override
    public List<ImageWithEmotions> getImagesWithEmotionType(String emotionType) {
        List<ImageWithEmotions> result = new ArrayList<>();
        for (ImageWithEmotions image : images) {
            if (image.getEmotionCountByType(emotionType) > 0) {
                result.add(image);
            }
        }
        return result;
    }

    @Override
    public void addEmotion(int index, Emotion emotion) {
        ImageWithEmotions oldImage = images.get(index);
        ImageWithEmotions newImage = oldImage.addEmotion(emotion);

        images.set(index, newImage);
        hashMap.put(newImage.getImageHash(), newImage);
        fileMap.put(newImage.getImageFile(), newImage);
    }

    @Override
    public boolean addEmotionByHash(String hash, Emotion emotion) {
        ImageWithEmotions image = hashMap.get(hash);
        if (image != null) {
            ImageWithEmotions newImage = image.addEmotion(emotion);
            int index = images.indexOf(image);
            if (index != -1) {
                images.set(index, newImage);
            }
            hashMap.put(newImage.getImageHash(), newImage);
            fileMap.put(newImage.getImageFile(), newImage);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeEmotion(String imageHash, String emotionId) {
        ImageWithEmotions image = hashMap.get(imageHash);
        if (image != null) {
            ImageWithEmotions newImage = image.removeEmotion(emotionId);
            int index = images.indexOf(image);
            if (index != -1) {
                images.set(index, newImage);
            }
            hashMap.put(newImage.getImageHash(), newImage);
            fileMap.put(newImage.getImageFile(), newImage);
            return true;
        }
        return false;
    }

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalImages", images.size());

        int totalEmotions = 0;
        Map<String, Integer> emotionTypeStats = new HashMap<>();

        for (ImageWithEmotions image : images) {
            totalEmotions += image.getEmotions().size();
            for (Emotion emotion : image.getEmotions()) {
                String type = emotion.getType();
                emotionTypeStats.put(type, emotionTypeStats.getOrDefault(type, 0) + 1);
            }
        }

        stats.put("totalEmotions", totalEmotions);
        stats.put("emotionTypes", emotionTypeStats);
        return stats;
    }

    @Override
    public List<ImageWithEmotions> getImagesSortedByEmotionCount() {
        List<ImageWithEmotions> sorted = new ArrayList<>(images);
        sorted.sort((img1, img2) ->
                Integer.compare(img2.getEmotions().size(), img1.getEmotions().size()));
        return sorted;
    }

    @Override
    public List<ImageWithEmotions> getImagesSortedByDate() {
        List<ImageWithEmotions> sorted = new ArrayList<>(images);
        sorted.sort((img1, img2) ->
                Long.compare(img2.getLastModified(), img1.getLastModified()));
        return sorted;
    }

    @Override
    public int size() {
        return images.size();
    }

    @Override
    public boolean isEmpty() {
        return images.isEmpty();
    }

    @Override
    public boolean updateImageFile(File oldFile, File newFile) {
        ImageWithEmotions image = fileMap.get(oldFile);
        if (image != null) {
            ImageWithEmotions updatedImage = new ImageWithEmotions.Builder(newFile)
                    .withCustomHash(image.getImageHash())
                    .withEmotions(image.getEmotions())
                    .withMetadata(image.getMetadata())
                    .build();

            int index = images.indexOf(image);
            if (index != -1) {
                images.set(index, updatedImage);
            }

            hashMap.remove(image.getImageHash());
            fileMap.remove(oldFile);
            hashMap.put(updatedImage.getImageHash(), updatedImage);
            fileMap.put(newFile, updatedImage);

            return true;
        }
        return false;
    }

    @Override
    public void clearAllEmotions() {
        for (int i = 0; i < images.size(); i++) {
            ImageWithEmotions image = images.get(i);
            ImageWithEmotions clearedImage = new ImageWithEmotions.Builder(image.getImageFile())
                    .withMetadata(image.getMetadata())
                    .build();
            images.set(i, clearedImage);
        }
    }

    @Override
    public List<Map<String, Object>> exportAllData() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (ImageWithEmotions image : images) {
            Map<String, Object> data = new HashMap<>();
            data.put("fileName", image.getFileName());
            data.put("filePath", image.getFilePath());
            data.put("imageHash", image.getImageHash());
            data.put("emotions", image.getEmotions());
            data.put("metadata", image.getMetadata());
            result.add(data);
        }
        return result;
    }

    //внутренний класс итератора
    private class EnhancedImageIterator implements ImageIterator {
        private int currentIndex = -1;
        private int lastAccessed = -1;

        @Override
        public boolean hasNext() {
            return !images.isEmpty() && currentIndex < images.size() - 1;
        }

        @Override
        public ImageWithEmotions next() {
            if (!hasNext()) {
                currentIndex = 0;
            } else {
                currentIndex++;
            }
            lastAccessed = currentIndex;
            return images.get(currentIndex);
        }

        @Override
        public ImageWithEmotions preview() {
            if (images.isEmpty()) {
                return null;
            }

            if (currentIndex > 0) {
                currentIndex--;
            } else {
                currentIndex = images.size() - 1;
            }

            lastAccessed = currentIndex;
            return images.get(currentIndex);
        }

        public ImageWithEmotions nextImage() {
            if (!hasNext()) {
                currentIndex = 0;
            } else {
                currentIndex++;
            }
            lastAccessed = currentIndex;
            return images.get(currentIndex);
        }

        public ImageWithEmotions previewImage() {
            if (images.isEmpty()) {
                return null;
            }
            if (currentIndex > 0) {
                currentIndex--;
            } else {
                currentIndex = images.size() - 1;
            }
            lastAccessed = currentIndex;
            return images.get(currentIndex);
        }

      @Override
        public ImageWithEmotions current() {
            if (lastAccessed >= 0 && lastAccessed < images.size()) {
                return images.get(lastAccessed);
            }
            return null;
        }

        @Override
        public Image getCurrentAsJavaFXImage() {
            ImageWithEmotions currentImage = current();
            if (currentImage != null) {
                try {
                    return new Image(currentImage.getImageFile().toURI().toString());
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        }

        @Override
        public File getCurrentFile() {
            ImageWithEmotions currentImage = current();
            return currentImage != null ? currentImage.getImageFile() : null;
        }

        @Override
        public String getCurrentFileName() {
            ImageWithEmotions currentImage = current();
            return currentImage != null ? currentImage.getFileName() : "";
        }

        @Override
        public List<Emotion> getCurrentEmotions() {
            ImageWithEmotions currentImage = current();
            return currentImage != null ? currentImage.getEmotions() : new ArrayList<>();
        }

        @Override
        public void addEmotionToCurrent(Emotion emotion) {
            ImageWithEmotions currentImage = current();
            if (currentImage != null) {
                EnhancedImageAggregate.this.addEmotion(lastAccessed, emotion);
            }
        }

        @Override
        public boolean removeEmotionFromCurrent(String emotionId) {
            ImageWithEmotions currentImage = current();
            if (currentImage != null) {
                return EnhancedImageAggregate.this.removeEmotion(
                        currentImage.getImageHash(), emotionId);
            }
            return false;
        }

        @Override
        public int getTotalCount() {
            return images.size();
        }

        @Override
        public int getCurrentIndex() {
            return lastAccessed;
        }

        @Override
        public boolean goTo(int index) {
            if (index >= 0 && index < images.size()) {
                currentIndex = index - 1;
                lastAccessed = index;
                return true;
            }
            return false;
        }

        @Override
        public void first() {
            currentIndex = -1;
            lastAccessed = -1;
        }

        @Override
        public void last() {
            if (!images.isEmpty()) {
                currentIndex = images.size() - 2;
                lastAccessed = images.size() - 1;
            }
        }

        @Override
        public boolean hasPreview() {
            return !images.isEmpty();
        }

        @Override
        public void reset() {
            currentIndex = -1;
            lastAccessed = -1;
        }
    }
}
