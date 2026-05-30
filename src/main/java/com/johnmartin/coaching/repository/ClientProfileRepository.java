package com.johnmartin.coaching.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.johnmartin.coaching.entity.ClientProfileEntity;

@Repository
public interface ClientProfileRepository extends JpaRepository<ClientProfileEntity, UUID> {

    boolean existsByUserId(UUID userId);

    void deleteByUserId(UUID userId);

    @Query("""
            SELECT cp
            FROM ClientProfileEntity cp
            JOIN CoachClientRelationshipEntity ccr
                ON cp.userId = ccr.clientId
            WHERE ccr.coachId = :coachId
            """)
    Page<ClientProfileEntity> findByCoachId(UUID coachId, Pageable pageable);
}
