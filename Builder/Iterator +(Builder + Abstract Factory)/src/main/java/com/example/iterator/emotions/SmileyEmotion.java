package com.example.iterator.emotions;

public class SmileyEmotion extends Emotion {
    private SmileyType smileyType;

    public enum SmileyType {
        HAPPY("😊"), SAD("😢"), LOVE("❤️"), WOW("😮"), ANGRY("😠"), LAUGH("😂");

        private final String emoji;

        SmileyType(String emoji) {
            this.emoji = emoji;
        }

        public String getEmoji() {
            return emoji;
        }
    }

    public SmileyEmotion() {}

    public SmileyEmotion(String id, String userId, SmileyType smileyType) {
        super(id, userId);
        this.smileyType = smileyType;
    }

    @Override
    public String getType() {
        return "smiley";
    }

    @Override
    public String getDisplayText() {
        return smileyType.getEmoji();
    }

    public SmileyType getSmileyType() { return smileyType; }
    public void setSmileyType(SmileyType smileyType) { this.smileyType = smileyType; }
}