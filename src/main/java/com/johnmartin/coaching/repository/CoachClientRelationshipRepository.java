package com.johnmartin.coaching.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.johnmartin.coaching.entity.CoachClientRelationshipEntity;
import jakarta.persistence.LockModeType;

@Repository
public interface CoachClientRelationshipRepository extends JpaRepository<CoachClientRelationshipEntity, UUID> {

    boolean existsByCoachIdAndClientId(UUID coachId, UUID clientId);

    Optional<CoachClientRelationshipEntity> findByCoachIdAndClientId(UUID coachId, UUID clientId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT relationship FROM CoachClientRelationshipEntity relationship WHERE relationship.coachId = :coachId AND relationship.clientId = :clientId")
    Optional<CoachClientRelationshipEntity> findByCoachIdAndClientIdForUpdate(UUID coachId, UUID clientId);

    void deleteByClientId(UUID clientId);

    List<CoachClientRelationshipEntity> findByCoachId(UUID coachId);

    List<CoachClientRelationshipEntity> findByCoachIdAndClientIdIn(UUID coachId, List<String> userIds);
}
