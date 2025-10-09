package kjo.care.msvc_emergency.repositories;

import kjo.care.msvc_emergency.entities.HealthCenter;
import kjo.care.msvc_emergency.enums.StatusHealth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface HealthRepository extends JpaRepository<HealthCenter, UUID> {
    long countByStatus(StatusHealth status);
    int countByCreatedDateBetween(LocalDate startDate, LocalDate endDate);
    Page<HealthCenter> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
