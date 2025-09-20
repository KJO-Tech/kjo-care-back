package kjo.care.msvc_moodTracking.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@ToString(exclude = "mood")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "mood_user")
public class MoodUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id")
    private String userId;
    private LocalDateTime recordedDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mood_id")
    private MoodEntity mood;
}
