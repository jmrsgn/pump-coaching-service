package com.johnmartin.coaching.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.johnmartin.coaching.entity.CoachClientRelationshipEntity;

@Repository
public interface CoachClientRelationshipRepository extends JpaRepository<CoachClientRelationshipEntity, UUID> {

    boolean existsByCoachIdAndClientId(UUID coachId, UUID clientId);

    void deleteByClientId(UUID clientId);

    List<CoachClientRelationshipEntity> findByCoachId(UUID coachId);
}
