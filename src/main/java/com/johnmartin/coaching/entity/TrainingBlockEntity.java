package com.johnmartin.coaching.entity;

import java.time.Instant;
import java.util.UUID;

import com.johnmartin.coaching.constants.entities.TrainingBlockConstants;
import org.hibernate.annotations.CreationTimestamp;

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
@Table(name = TrainingBlockConstants.TABLE_NAME)
public class TrainingBlockEntity {

    @Id
    private UUID id;

    @Column(name = TrainingBlockConstants.COLUMN_COACH_ID, nullable = false)
    private UUID coachId;

    @Column(name = TrainingBlockConstants.COLUMN_CLIENT_ID, nullable = false)
    private UUID clientId;

    @Enumerated(EnumType.STRING)
    @Column(name = TrainingBlockConstants.COLUMN_STATUS, nullable = false)
    private TrainingBlockStatus status;

    @Column(name = TrainingBlockConstants.COLUMN_NO_OF_WEEKS, nullable = false)
    private Integer numberOfWeeks;

    @Column(name = TrainingBlockConstants.COLUMN_TRAINING_BLOCK_NAME, nullable = false, columnDefinition = "text")
    private String trainingBlockName;

    @Column(name = TrainingBlockConstants.COLUMN_TRAINING_DAYS, nullable = false)
    private Integer trainingDays;

    @Column(name = TrainingBlockConstants.COLUMN_TRAINING_SPLIT, nullable = false, length = 100)
    private String trainingSplit;

    @Column(name = TrainingBlockConstants.COLUMN_ESTIMATED_MACROS, nullable = false)
    private Integer estimatedMacros;

    @Column(name = TrainingBlockConstants.COLUMN_TARGET_PROTEIN_IN_GRAMS, nullable = false)
    private Integer targetProteinInGrams;

    @Column(name = TrainingBlockConstants.COLUMN_TARGET_CARBS_IN_GRAMS, nullable = false)
    private Integer targetCarbsInGrams;

    @Column(name = TrainingBlockConstants.COLUMN_TARGET_FAT_IN_GRAMS, nullable = false)
    private Integer targetFatInGrams;

    @Column(name = TrainingBlockConstants.COLUMN_REQUIRED_DAILY_STEPS, nullable = false)
    private Integer requiredDailySteps;

    @Column(name = TrainingBlockConstants.COLUMN_OTHER_NOTES, length = 2000)
    private String otherNotes;

    @CreationTimestamp
    @Column(name = TrainingBlockConstants.COLUMN_CREATED_AT, nullable = false, updatable = false)
    private Instant createdAt;

    @CreationTimestamp
    @Column(name = TrainingBlockConstants.COLUMN_UPDATED_AT, nullable = false)
    private Instant updatedAt;

    public TrainingBlockEntity() {
    }

}
