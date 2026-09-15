package com.johnmartin.coaching.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.johnmartin.coaching.entity.TrainingBlockEntity;
import com.johnmartin.coaching.enums.TrainingBlockStatus;

@Repository
public interface TrainingBlockRepository extends JpaRepository<TrainingBlockEntity, UUID> {

    List<TrainingBlockEntity> findByCoachIdAndClientIdInAndStatus(UUID coachId,
                                                                  List<UUID> clientIds,
                                                                  TrainingBlockStatus status);
}
