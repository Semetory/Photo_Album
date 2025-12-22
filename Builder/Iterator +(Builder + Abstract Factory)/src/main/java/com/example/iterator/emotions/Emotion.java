package com.example.iterator.emotions;

import com.example.iterator.CustomEmotion;
import com.example.iterator.TextEmotion;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SmileyEmotion.class, name = "smiley"),
        @JsonSubTypes.Type(value = TextEmotion.class, name = "text"),
        @JsonSubTypes.Type(value = CustomEmotion.class, name = "custom")
})
public abstract class Emotion {
    protected String id;
    protected String userId;
    protected long timestamp;
    protected double positionX; // позиция на изображении (0-1)
    protected double positionY;

    public Emotion() {}

    public Emotion(String id, String userId) {
        this.id = id;
        this.userId = userId;
        this.timestamp = System.currentTimeMillis();
    }

    public abstract String getType();
    public abstract String getDisplayText();

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public double getPositionX() { return positionX; }
    public void setPositionX(double positionX) { this.positionX = positionX; }
    public double getPositionY() { return positionY; }
    public void setPositionY(double positionY) { this.positionY = positionY; }
}
