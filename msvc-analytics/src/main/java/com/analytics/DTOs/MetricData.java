package com.analytics.DTOs;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetricData {
    private Long currentValue;
    private Double percentageChange;
}
