package kjo.care.msvc_blog.entities;

import jakarta.persistence.*;
import kjo.care.msvc_blog.enums.ReactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "reaction")
public class Reaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "blogId", referencedColumnName = "id")
    private Blog blog;

    @Column(name = "userId")
    private String userId;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ReactionType type;

    @Column(name = "reactionDate")
    private LocalDate reactionDate = LocalDate.now();
}
