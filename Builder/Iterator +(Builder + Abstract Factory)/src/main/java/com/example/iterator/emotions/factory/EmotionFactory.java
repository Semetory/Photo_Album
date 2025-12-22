package com.example.iterator.emotions.factory;

import com.example.iterator.emotions.AdvancedEmotionFactory;
import com.example.iterator.emotions.Emotion;
import com.example.iterator.emotions.SimpleEmotionFactory;

public abstract class EmotionFactory {
    public abstract Emotion createEmotion(String userId, String type, Object... params);

    // Статический метод для получения фабрики по типу
    public static EmotionFactory getFactory(String factoryType) {
        switch (factoryType.toLowerCase()) {
            case "simple":
                return new SimpleEmotionFactory();
            case "advanced":
                return new AdvancedEmotionFactory();
            default:
                return new SimpleEmotionFactory();
        }
    }
}
