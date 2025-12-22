package com.example.iterator;

import com.example.iterator.emotions.Emotion;

public class TextEmotion extends Emotion {
    private String text;
    private String color;

    public TextEmotion() {}

    public TextEmotion(String id, String userId, String text) {
        super(id, userId);
        this.text = text;
        this.color = "#FF5722";
    }

    @Override
    public String getType() {
        return "text";
    }

    @Override
    public String getDisplayText() {
        return text;
    }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}
