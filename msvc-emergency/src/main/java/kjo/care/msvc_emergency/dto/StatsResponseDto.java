package kjo.care.msvc_emergency.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatsResponseDto {
    private int totalResources;
    private int activeEmergencies;
    private int totalContacts;
    private int totalLinks;
    private int totalAccesses;
}
