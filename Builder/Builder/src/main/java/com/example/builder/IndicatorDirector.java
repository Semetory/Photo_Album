package com.example.builder;


public class IndicatorDirector {
    public Indicator constructProgressIndicator(IndicatorBuilder builder,
                                                double start,
                                                double end,
                                                double currentValue,
                                                String title) {
        builder.setBounds(start, end);
        builder.setCurrentValue(currentValue);
        builder.setTitle(title);
        builder.setScaleVisible(true);
        builder.setMarkersVisible(true);
        builder.setView(10, '▢', '▣');
        return builder.build();
    }

    public Indicator constructSimpleIndicator(IndicatorBuilder builder,
                                              double start,
                                              double end,
                                              double currentValue) {
        builder.setBounds(start, end);
        builder.setCurrentValue(currentValue);
        builder.setScaleVisible(false);
        builder.setMarkersVisible(false);
        return builder.build();
    }
}
