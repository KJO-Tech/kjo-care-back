package com.analytics.DTOs;

import java.time.LocalDate;

public class HeatmapEntryDto {
    private LocalDate date;
    private int moodValue;

    public HeatmapEntryDto(LocalDate date, int moodValue) {
        this.date = date;
        this.moodValue = moodValue;
    }

    public LocalDate getDate() { return date; }
    public int getMoodValue() { return moodValue; }
}
