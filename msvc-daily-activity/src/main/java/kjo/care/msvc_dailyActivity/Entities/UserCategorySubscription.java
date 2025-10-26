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
        name = "user_category_subscriptions",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_category",
                columnNames = {"user_id", "category_id"}
        ),
        indexes = {
                @Index(name = "idx_subscriptions_user", columnList = "user_id"),
                @Index(name = "idx_subscriptions_category", columnList = "category_id")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCategorySubscription {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @Column(name = "user_id", nullable = false, length = 255)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "subscribed_at", nullable = false, updatable = false)
    private LocalDateTime subscribedAt;

    @PrePersist
    protected void onCreate() {
        this.subscribedAt = LocalDateTime.now();
    }
}