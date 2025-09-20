package kjo.care.msvc_emergency.repositories;

import kjo.care.msvc_emergency.entities.EmergencyResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface EmergencyRepository extends JpaRepository<EmergencyResource, UUID> {

    @Modifying
    @Transactional
    @Query("UPDATE EmergencyResource e SET e.accessCount = e.accessCount + 1 WHERE e.id = :id")
    void incrementAccessCount(@Param("id") UUID id);

    @Query("SELECT COUNT(e) FROM EmergencyResource e")
    int countAllEmergencies();

    @Query("SELECT COUNT(e) FROM EmergencyResource e WHERE e.status = 'ACTIVE'")
    int countActiveEmergencies();

    @Query(value = "SELECT SUM(ARRAY_LENGTH(contacts, 1)) FROM emergency_resources", nativeQuery = true)
    int sumAllContacts();

    @Query(value = "SELECT SUM(ARRAY_LENGTH(links, 1)) FROM emergency_resources", nativeQuery = true)
    int sumAllLinks();

    @Query("SELECT SUM(e.accessCount) FROM EmergencyResource e")
    int sumTotalAccesses();
}
