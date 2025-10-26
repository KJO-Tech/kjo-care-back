package com.analytics.DTOs;

import java.time.LocalDate;

public class MoodHeatmapEntryDto {
    private LocalDate date;
    private int moodValue;

    public MoodHeatmapEntryDto(LocalDate date, int moodValue) {
        this.date = date;
        this.moodValue = moodValue;
    }

    public LocalDate getDate() { return date; }
    public int getMoodValue() { return moodValue; }

    public void setDate(LocalDate date) { this.date = date; }
    public void setMoodValue(int moodValue) { this.moodValue = moodValue; }
}
