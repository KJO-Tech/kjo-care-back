package com.analytics.DTOs;

public class MoodPieChartEntryDto {
    private int moodValue;
    private long count;
    private double percentage;

    public MoodPieChartEntryDto(int moodValue, long count, double percentage) {
        this.moodValue = moodValue;
        this.count = count;
        this.percentage = percentage;
    }

    public int getMoodValue() { return moodValue; }
    public long getCount() { return count; }
    public double getPercentage() { return percentage; }
}
