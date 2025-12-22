package com.example.iterator.model;

import com.example.iterator.emotions.Emotion;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/*представляет изображение с прикрепленными эмоциями использует паттерн Builder */
public class ImageWithEmotions {

    private final File imageFile;
    private final String imageHash;  //Уникальный хэш для идентификации файла
    private final List<Emotion> emotions;
    private final Map<String, Object> metadata;
    private final Date creationDate;

    private ImageWithEmotions(Builder builder) {
        this.imageFile = builder.imageFile;
        this.imageHash = builder.imageHash;
        this.emotions = Collections.unmodifiableList(new ArrayList<>(builder.emotions));
        this.metadata = Collections.unmodifiableMap(new HashMap<>(builder.metadata));
        this.creationDate = new Date();

        if (!metadata.containsKey("createdAt")) {
            ((HashMap<String, Object>) metadata).put("createdAt", creationDate);
        }
    }

    @JsonIgnore public File getImageFile() {
        return imageFile;
    }
    @JsonProperty("filePath") public String getFilePath() {
        return imageFile.getAbsolutePath();
    }
    @JsonProperty("fileName") public String getFileName() {
        return imageFile.getName();
    }
    @JsonProperty("imageHash") public String getImageHash() {
        return imageHash;
    }
    @JsonProperty("emotions") public List<Emotion> getEmotions() { return new ArrayList<>(emotions);  } // Возвращаем копию
    @JsonProperty("metadata") public Map<String, Object> getMetadata() { return new HashMap<>(metadata); }  // Возвращаем копию
    @JsonIgnore public Date getCreationDate() { return new Date(creationDate.getTime()); }
    @JsonProperty("fileSize") public long getFileSize() { return imageFile.length(); }
    @JsonProperty("lastModified") public long getLastModified() { return imageFile.lastModified(); }

    public ImageWithEmotions addEmotion(Emotion emotion) {
        //создаем нового билдера на основе текущего объекта
        Builder builder = new Builder(this.imageFile)
                .withCustomHash(this.imageHash)
                .withEmotions(this.emotions)
                .withMetadata(this.metadata);

        //добавляем новую эмоцию
        builder.withEmotion(emotion);

        //создаем новый объект
        return builder.build();
    }

    public ImageWithEmotions removeEmotion(String emotionId) {
        Builder builder = new Builder(this.imageFile)
                .withCustomHash(this.imageHash)
                .withMetadata(this.metadata);

        //копируем все эмоции, кроме удаляемой
        for (Emotion emotion : this.emotions) {
            if (!emotion.getId().equals(emotionId)) {
                builder.withEmotion(emotion);
            }
        }

        return builder.build();
    }

    public ImageWithEmotions updateMetadata(String key, Object value) {
        Builder builder = new Builder(this.imageFile)
                .withCustomHash(this.imageHash)
                .withEmotions(this.emotions);

        //копируем все существующие метаданные
        for (Map.Entry<String, Object> entry : this.metadata.entrySet()) {
            builder.withMetadata(entry.getKey(), entry.getValue());
        }

        //добавляем/обновляем новое значение
        builder.withMetadata(key, value);

        return builder.build();
    }

    public int getEmotionCountByType(String type) {
        return (int) emotions.stream()
                .filter(e -> e.getType().equalsIgnoreCase(type))
                .count();
    }

    public Map<String, Integer> getEmotionsStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        for (Emotion emotion : emotions) {
            String type = emotion.getType();
            stats.put(type, stats.getOrDefault(type, 0) + 1);
        }
        return stats;
    }

    public Map<String, Double> getAverageEmotionPosition() {
        if (emotions.isEmpty()) {
            return Map.of("x", 0.5, "y", 0.5);
        }

        double totalX = 0;
        double totalY = 0;

        for (Emotion emotion : emotions) {
            totalX += emotion.getPositionX();
            totalY += emotion.getPositionY();
        }

        return Map.of(
                "x", totalX / emotions.size(),
                "y", totalY / emotions.size()
        );
    }

    @Override
    public String toString() {
        return String.format("ImageWithEmotions{file='%s', emotions=%d, hash='%s'}",
                imageFile.getName(), emotions.size(), imageHash.substring(0, 8));
    }

    /*Внутренний класс Builder для создания объектов ImageWithEmotions */
    public static class Builder {
        private final File imageFile;
        private String imageHash;
        private final List<Emotion> emotions = new ArrayList<>();
        private final Map<String, Object> metadata = new HashMap<>();

        public Builder(File imageFile) {
            if (imageFile == null) {
                throw new IllegalArgumentException("Image file cannot be null");
            }
            this.imageFile = imageFile;
            this.imageHash = calculateFileHash(imageFile);
            this.metadata.put("originalPath", imageFile.getAbsolutePath());
        }

        /*Конструктор для создания Builder из существующего ImageWithEmotions */
        public Builder(ImageWithEmotions source) {
            this.imageFile = source.imageFile;
            this.imageHash = source.imageHash;
            this.emotions.addAll(source.emotions);
            this.metadata.putAll(source.metadata);
        }

        public Builder withEmotion(Emotion emotion) {
            if (emotion != null) {
                this.emotions.add(emotion);
            }
            return this;
        }

        public Builder withEmotions(Collection<Emotion> emotions) {
            if (emotions != null) {
                this.emotions.addAll(emotions);
            }
            return this;
        }

        public Builder withMetadata(String key, Object value) {
            if (key != null && value != null) {
                this.metadata.put(key, value);
            }
            return this;
        }

        public Builder withMetadata(Map<String, Object> metadata) {
            if (metadata != null) {
                this.metadata.putAll(metadata);
            }
            return this;
        }

        public Builder withCustomHash(String hash) {
            if (hash != null && !hash.trim().isEmpty()) {
                this.imageHash = hash;
            }
            return this;
        }

        public ImageWithEmotions build() {
            // Валидация
            if (!imageFile.exists()) {
                throw new IllegalStateException("Image file does not exist: " + imageFile.getPath());
            }

            if (!imageFile.canRead()) {
                throw new IllegalStateException("Cannot read image file: " + imageFile.getPath());
            }

            // Автоматически добавляем информацию о файле
            if (!metadata.containsKey("fileSize")) {
                metadata.put("fileSize", imageFile.length());
            }

            if (!metadata.containsKey("lastModified")) {
                metadata.put("lastModified", imageFile.lastModified());
            }

            if (!metadata.containsKey("fileExtension")) {
                String name = imageFile.getName();
                int dotIndex = name.lastIndexOf('.');
                if (dotIndex > 0) {
                    metadata.put("fileExtension", name.substring(dotIndex + 1).toLowerCase());
                }
            }

            return new ImageWithEmotions(this);
        }


        private String calculateFileHash(File file) {
            try {
                StringBuilder hashInput = new StringBuilder();

                hashInput.append(file.getName());
                hashInput.append("_").append(file.length());
                BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                hashInput.append("_").append(attrs.creationTime().toMillis());

                try {
                    byte[] buffer = new byte[1024];
                    try (var fis = Files.newInputStream(file.toPath())) {
                        int bytesRead = fis.read(buffer);

                        if (bytesRead > 0) {
                            hashInput.append("_").append(
                                    Base64.getEncoder().encodeToString(Arrays.copyOfRange(buffer, 0, bytesRead))
                            );
                        }

                        /*if (bytesRead > 0) {
                            hashInput.append("_").append(Base64.getEncoder().encodeToString(buffer,0,bytesRead)); //!//
                        }*/
                    }
                } catch (IOException e) {
                    //если не удалось прочитать содержимое, используем только метаданные
                    hashInput.append("_").append(file.lastModified());
                }

                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] hashBytes = md.digest(hashInput.toString().getBytes());

                StringBuilder hexString = new StringBuilder();
                for (byte b : hashBytes) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) {
                        hexString.append('0');
                    }
                    hexString.append(hex);
                }

                return hexString.toString();

            } catch (NoSuchAlgorithmException | IOException e) {
                return file.getName() + "_" + file.length() + "_" + file.lastModified();
            }
        }
    }

    public static ImageWithEmotions fromFile(File file) {
        return new Builder(file).build();
    }

    public static ImageWithEmotions fromFileWithEmotions(File file, List<Emotion> emotions) {
        return new Builder(file)
                .withEmotions(emotions)
                .build();
    }

    public static ImageWithEmotions fromExistingWithNewFile(ImageWithEmotions existing, File newFile) {
        return new Builder(newFile)
                .withCustomHash(existing.getImageHash())  // Сохраняем старый хэш
                .withEmotions(existing.getEmotions())
                .withMetadata(existing.getMetadata())
                .withMetadata("previousPath", existing.getFilePath())
                .build();
    }
}
