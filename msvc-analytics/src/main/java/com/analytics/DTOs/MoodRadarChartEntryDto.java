package com.analytics.DTOs;

public class MoodRadarChartEntryDto {
    private int moodValue;
    private long count;

    public MoodRadarChartEntryDto(int moodValue, long count) {
        this.moodValue = moodValue;
        this.count = count;
    }

    public int getMoodValue() { return moodValue; }
    public long getCount() { return count; }
}
