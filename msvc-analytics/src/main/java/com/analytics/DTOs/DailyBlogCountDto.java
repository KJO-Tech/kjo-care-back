package com.analytics.DTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyBlogCountDto {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private Long count;
}