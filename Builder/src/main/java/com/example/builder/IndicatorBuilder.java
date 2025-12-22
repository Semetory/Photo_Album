package com.example.builder;

public interface IndicatorBuilder {
    void setView(int segments, char normalChar, char selectedChar);
    void setBounds(double start, double end);
    void setCurrentValue(double value);
    void setTitle(String title);
    void setScaleVisible(boolean visible);
    void setMarkersVisible(boolean visible);
    Indicator build();
}
