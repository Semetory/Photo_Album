package com.example.iterator.emotions;

import com.example.iterator.TextEmotion;
import com.example.iterator.emotions.factory.EmotionFactory;

public class SimpleEmotionFactory extends EmotionFactory {

    @Override
    public Emotion createEmotion(String userId, String type, Object... params) {
        String id = java.util.UUID.randomUUID().toString();

        switch (type.toLowerCase()) {
            case "smiley":
                if (params.length > 0 && params[0] instanceof SmileyEmotion.SmileyType) {
                    return new SmileyEmotion(id, userId, (SmileyEmotion.SmileyType) params[0]);
                }
                return new SmileyEmotion(id, userId, SmileyEmotion.SmileyType.HAPPY);

            case "text":
                if (params.length > 0 && params[0] instanceof String) {
                    return new TextEmotion(id, userId, (String) params[0]);
                }
                return new TextEmotion(id, userId, "Cool!");

            default:
                throw new IllegalArgumentException("Unknown emotion type: " + type);
        }
    }
}
