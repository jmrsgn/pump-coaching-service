package com.johnmartin.coaching.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.johnmartin.coaching.entity.CoachClientRelationshipEntity;

@Repository
public interface CoachClientRelationshipRepository extends JpaRepository<CoachClientRelationshipEntity, UUID> {

    boolean existsByCoachIdAndClientId(UUID coachId, UUID clientId);
}
