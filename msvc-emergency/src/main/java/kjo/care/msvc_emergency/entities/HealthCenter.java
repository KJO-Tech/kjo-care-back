package kjo.care.msvc_emergency.entities;

import jakarta.persistence.*;
import kjo.care.msvc_emergency.enums.StatusHealth;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "health_center")
public class HealthCenter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "phone")
    private String phone;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusHealth status;

    @Column(name = "createdDate")
    private LocalDate createdDate = LocalDate.now();

    @Column(name = "modifiedDate")
    private LocalDate modifiedDate = LocalDate.now();
}
