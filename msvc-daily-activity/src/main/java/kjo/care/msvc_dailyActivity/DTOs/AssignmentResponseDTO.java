package kjo.care.msvc_dailyActivity.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentResponseDTO {

    private UUID id;
    private String userId;
    private ExerciseDetailDTO exercise;
    private LocalDateTime assignedAt;
    private Boolean completed;
    private LocalDateTime completedAt;
}