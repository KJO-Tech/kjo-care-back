package kjo.care.msvc_dailyActivity.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionResponseDTO {

    private UUID id;
    private String userId;
    private UUID categoryId;
    private String categoryName;
    private String categoryDescription;
    private String categoryImageUrl;
    private LocalDateTime subscribedAt;
}