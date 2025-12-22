package com.example.iterator.emotions;

import com.example.iterator.CustomEmotion;
import com.example.iterator.TextEmotion;
import com.example.iterator.emotions.factory.EmotionFactory;

public class AdvancedEmotionFactory extends EmotionFactory {

    @Override
    public Emotion createEmotion(String userId, String type, Object... params) {
        String id = java.util.UUID.randomUUID().toString();

        switch (type.toLowerCase()) {
            case "smiley":
                SmileyEmotion.SmileyType smileyType = params.length > 0 && params[0] instanceof SmileyEmotion.SmileyType
                        ? (SmileyEmotion.SmileyType) params[0]
                        : SmileyEmotion.SmileyType.HAPPY;

                SmileyEmotion smiley = new SmileyEmotion(id, userId, smileyType);

                if (params.length > 1 && params[1] instanceof Double) {
                    smiley.setPositionX((Double) params[1]);
                }
                if (params.length > 2 && params[2] instanceof Double) {
                    smiley.setPositionY((Double) params[2]);
                }
                return smiley;

            case "text":
                String text = params.length > 0 && params[0] instanceof String
                        ? (String) params[0]
                        : "Awesome!";

                TextEmotion textEmotion = new TextEmotion(id, userId, text);

                if (params.length > 1 && params[1] instanceof String) {
                    textEmotion.setColor((String) params[1]);
                }
                return textEmotion;

            case "custom":
                String emojiCode = params.length > 0 && params[0] instanceof String
                        ? (String) params[0]
                        : "🎯";

                CustomEmotion custom = new CustomEmotion(id, userId, emojiCode);

                if (params.length > 1 && params[1] instanceof String) {
                    custom.setDescription((String) params[1]);
                }
                return custom;

            default:
                throw new IllegalArgumentException("Unknown emotion type: " + type);
        }
    }
}