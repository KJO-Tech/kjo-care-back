package kjo.care.msvc_moodTracking.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@ToString(exclude = "mood")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "mood_user")
public class MoodUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private String userId;
    private LocalDateTime recordedDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mood_id")
    private MoodEntity mood;
}
