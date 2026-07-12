package com.johnmartin.coaching.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.johnmartin.coaching.constants.entities.CoachProfileConstants;
import com.johnmartin.coaching.enums.ActivityLevel;
import com.johnmartin.coaching.enums.FitnessGoal;
import com.johnmartin.coaching.enums.Gender;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = CoachProfileConstants.TABLE_NAME)
@Getter
@Setter
public class ClientProfileEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @JoinColumn(nullable = false, unique = true, name = CoachProfileConstants.COLUMN_USER_ID)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = CoachProfileConstants.COLUMN_GENDER)
    private Gender gender;

    @Column(nullable = false, name = CoachProfileConstants.COLUMN_AGE)
    private Integer age;

    @Column(nullable = false, name = CoachProfileConstants.COLUMN_HEIGHT_CM)
    private Double heightCm;

    @Column(nullable = false, name = CoachProfileConstants.COLUMN_CURRENT_WEIGHT)
    private Double currentWeight;

    @Column(nullable = false, name = CoachProfileConstants.COLUMN_GOAL_WEIGHT)
    private Double goalWeight;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = CoachProfileConstants.COLUMN_ACTIVITY_LEVEL)
    private ActivityLevel activityLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = CoachProfileConstants.COLUMN_FITNESS_GOAL)
    private FitnessGoal fitnessGoal;

    @CreationTimestamp
    @Column(name = CoachProfileConstants.COLUMN_CREATED_AT, updatable = false)
    private Instant createdAt;

    @CreationTimestamp
    @Column(name = CoachProfileConstants.COLUMN_UPDATED_AT)
    private Instant updatedAt;
}
