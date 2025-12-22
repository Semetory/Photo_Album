package com.example.builder;

public class BlochIndicatorBuilder {
    private final Indicator indicator;

    private BlochIndicatorBuilder(Builder builder) {
        this.indicator = new Indicator();
        // Инициализация индикатора на основе builder
    }

    public static class Builder {
        private double startValue = 0;
        private double endValue = 100;
        private double currentValue = 0;
        private String title = "";
        private boolean showScale = true;
        private boolean showMarkers = true;
        private IndicatorType type = IndicatorType.LINEAR;

        public Builder setStartValue(double startValue) {
            this.startValue = startValue;
            return this;
        }

        public Builder setEndValue(double endValue) {
            this.endValue = endValue;
            return this;
        }

        public Builder setCurrentValue(double currentValue) {
            this.currentValue = currentValue;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setShowScale(boolean showScale) {
            this.showScale = showScale;
            return this;
        }

        public Builder setShowMarkers(boolean showMarkers) {
            this.showMarkers = showMarkers;
            return this;
        }

        public Builder setType(IndicatorType type) {
            this.type = type;
            return this;
        }

        public BlochIndicatorBuilder build() {
            return new BlochIndicatorBuilder(this);
        }
    }

    public Indicator getIndicator() {
        return indicator;
    }
}

enum IndicatorType {
    LINEAR, CIRCULAR
}
