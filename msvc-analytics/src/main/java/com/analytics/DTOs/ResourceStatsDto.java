package com.analytics.DTOs;

import lombok.Data;

@Data
public class ResourceStatsDto {
    private long totalResources;
    private long activeEmergencies;
    private long totalContacts;
    private long totalLinks;
    private long totalAccesses;
}
