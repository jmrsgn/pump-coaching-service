package com.johnmartin.coaching.entity;

import java.util.UUID;

import com.johnmartin.coaching.enums.TrainingBlockStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "training_blocks")
public class TrainingBlockEntity {

    @Id
    private UUID id;

    @Column(name = "coach_id", nullable = false)
    private UUID coachId;

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TrainingBlockStatus status;

    protected TrainingBlockEntity() {
    }

}
