package com.johnmartin.coaching.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.johnmartin.coaching.entity.TrainingBlockEntity;
import com.johnmartin.coaching.enums.TrainingBlockStatus;

@Repository
public interface TrainingBlockRepository extends JpaRepository<TrainingBlockEntity, UUID> {

    boolean existsByCoachIdAndClientIdAndStatus(UUID coachId, UUID clientId, TrainingBlockStatus status);

    Optional<TrainingBlockEntity> findByCoachIdAndClientIdAndStatus(UUID coachId,
                                                                     UUID clientId,
                                                                     TrainingBlockStatus status);

    List<TrainingBlockEntity> findByCoachIdAndClientIdInAndStatus(UUID coachId,
                                                                  List<UUID> clientIds,
                                                                  TrainingBlockStatus status);
}
