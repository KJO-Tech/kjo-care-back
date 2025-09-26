package kjo.care.msvc_emergency.entities;

import jakarta.persistence.*;
import kjo.care.msvc_emergency.enums.StatusEmergency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "emergency_resources")
public class EmergencyResource {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String userId;

    @Column(name = "name")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "resourceUrl")
    private String resourceUrl;

    @Column(name = "createdDate")
    private LocalDate createdDate = LocalDate.now();

    @Column(name = "modifiedDate")
    private LocalDate modifiedDate = LocalDate.now();

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    private String[] contacts;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    private String[] links;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusEmergency status;

    @Column(name = "accessCount")
    private int accessCount;

}
