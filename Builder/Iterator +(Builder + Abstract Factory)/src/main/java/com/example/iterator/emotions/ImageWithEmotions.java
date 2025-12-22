package com.example.iterator.emotions;

import com.example.iterator.emotions.Emotion;
import java.io.File;
import java.util.*;

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

public class ImageWithEmotions {

    private final File imageFile;
    private final String imageHash;
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

    // Getters
    @JsonIgnore
    public File getImageFile() { return imageFile; }

    @JsonProperty("filePath")
    public String getFilePath() { return imageFile.getAbsolutePath(); }

    @JsonProperty("fileName")
    public String getFileName() { return imageFile.getName(); }

    @JsonProperty("imageHash")
    public String getImageHash() { return imageHash; }

    @JsonProperty("emotions")
    public List<Emotion> getEmotions() { return new ArrayList<>(emotions); }

    @JsonProperty("metadata")
    public Map<String, Object> getMetadata() { return new HashMap<>(metadata); }

    @JsonIgnore
    public Date getCreationDate() { return new Date(creationDate.getTime()); }

    @JsonProperty("fileSize")
    public long getFileSize() { return imageFile.length(); }

    @JsonProperty("lastModified")
    public long getLastModified() { return imageFile.lastModified(); }

    public ImageWithEmotions addEmotion(Emotion emotion) {
        Builder builder = new Builder(this.imageFile)
                .withCustomHash(this.imageHash)
                .withEmotions(this.emotions)
                .withMetadata(this.metadata);
        builder.withEmotion(emotion);
        return builder.build();
    }

    public ImageWithEmotions removeEmotion(String emotionId) {
        Builder builder = new Builder(this.imageFile)
                .withCustomHash(this.imageHash)
                .withMetadata(this.metadata);
        for (Emotion emotion : this.emotions) {
            if (!emotion.getId().equals(emotionId)) {
                builder.withEmotion(emotion);
            }
        }
        return builder.build();
    }

    public int getEmotionCountByType(String type) {
        return (int) emotions.stream()
                .filter(e -> e.getType().equalsIgnoreCase(type))
                .count();
    }

    public List<Emotion> getEmotionsByType(String type) {
        List<Emotion> result = new ArrayList<>();
        for (Emotion emotion : emotions) {
            if (emotion.getType().equalsIgnoreCase(type)) {
                result.add(emotion);
            }
        }
        return result;
    }

    // Builder
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

        public Builder withCustomHash(String hash) {
            if (hash != null && !hash.trim().isEmpty()) {
                this.imageHash = hash;
            }
            return this;
        }

        public ImageWithEmotions build() {
            if (!imageFile.exists()) {
                throw new IllegalStateException("Image file does not exist: " + imageFile.getPath());
            }

            if (!metadata.containsKey("fileSize")) {
                metadata.put("fileSize", imageFile.length());
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
}
