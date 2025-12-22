package com.example.iterator;

import com.example.iterator.emotions.Emotion;

public class CustomEmotion extends Emotion {
    private String emojiCode;
    private String description;

    public CustomEmotion() {}

    public CustomEmotion(String id, String userId, String emojiCode) {
        super(id, userId);
        this.emojiCode = emojiCode;
    }

    @Override
    public String getType() {
        return "custom";
    }

    @Override
    public String getDisplayText() {
        return emojiCode;
    }

    public String getEmojiCode() { return emojiCode; }
    public void setEmojiCode(String emojiCode) { this.emojiCode = emojiCode; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
