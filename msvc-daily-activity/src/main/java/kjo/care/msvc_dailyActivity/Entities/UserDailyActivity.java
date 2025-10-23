package kjo.care.msvc_dailyActivity.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "user_daily_activities")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDailyActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @ManyToOne
    @JoinColumn(name = "daily_exercise_id", nullable = false)
    private DailyExercise dailyExercise;

    @Column(name = "assigned_date", nullable = false)
    private LocalDate assignedDate;
}
