package com.analytics.DTOs;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyMoodUserCountDto {
    private LocalDate date;
    private Long count;
}