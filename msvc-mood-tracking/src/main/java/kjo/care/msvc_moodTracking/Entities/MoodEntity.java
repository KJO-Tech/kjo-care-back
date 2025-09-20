package kjo.care.msvc_moodTracking.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@ToString(exclude = "moodUsers")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "mood")
public class MoodEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;
//    @Enumerated(EnumType.STRING)
//    @Column(name = "state")
//    private MoodState state;
    @Column(name = "image")
    private String image;
    @Column(name = "color")
    private String color;
    @Column(name = "is_active")
    private Boolean isActive = true;
    @OneToMany(mappedBy = "mood", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MoodUser> moodUsers = new ArrayList<>();
}
