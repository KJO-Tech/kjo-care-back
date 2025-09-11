package kjo.care.msvc_emergency.repositories;

import kjo.care.msvc_emergency.entities.HealthCenter;
import kjo.care.msvc_emergency.enums.StatusHealth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface HealthRepository extends JpaRepository<HealthCenter, Long> {
    long countByStatus(StatusHealth status);
    int countByCreatedDateBetween(LocalDate startDate, LocalDate endDate);
}
