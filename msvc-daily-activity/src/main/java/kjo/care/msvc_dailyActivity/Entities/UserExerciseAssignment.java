package kjo.care.msvc_dailyActivity.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "user_exercise_assignments",
        indexes = {
                @Index(name = "idx_assignments_user", columnList = "user_id"),
                @Index(name = "idx_assignments_exercise", columnList = "exercise_id"),
                @Index(name = "idx_assignments_completed", columnList = "completed")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserExerciseAssignment {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @Column(name = "user_id", nullable = false, length = 255)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private DailyExercise exercise;

    @Column(name = "assigned_at", nullable = false, updatable = false)
    private LocalDateTime assignedAt;

    @Column(nullable = false)
    @Builder.Default
    private Boolean completed = false;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        this.assignedAt = LocalDateTime.now();
        if (this.completed == null) {
            this.completed = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (this.completed && this.completedAt == null) {
            this.completedAt = LocalDateTime.now();
        }
    }
}