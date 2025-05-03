package kjo.care.msvc_emergency.repositories;

import kjo.care.msvc_emergency.entities.HealthCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthRepository extends JpaRepository<HealthCenter, Long> {
}
