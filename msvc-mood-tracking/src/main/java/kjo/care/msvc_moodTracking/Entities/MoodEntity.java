package kjo.care.msvc_moodTracking.Entities;

import jakarta.persistence.*;
import kjo.care.msvc_moodTracking.enums.MoodState;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString(exclude = "moodUsers")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "mood")
public class MoodEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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
