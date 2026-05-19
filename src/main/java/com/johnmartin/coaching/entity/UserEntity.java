package com.johnmartin.coaching.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.johnmartin.coaching.constants.entities.UserEntityConstants;
import com.johnmartin.coaching.enums.ActivityLevel;
import com.johnmartin.coaching.enums.FitnessGoal;
import com.johnmartin.coaching.enums.Gender;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = UserEntityConstants.TABLE_NAME)
public class UserEntity {

    @Id
    @Column(nullable = false, updatable = false, name = UserEntityConstants.COLUMN_ID)
    private UUID id;

    @Column(nullable = false, name = UserEntityConstants.COLUMN_FIRST_NAME)
    private String firstName;

    @Column(nullable = false, name = UserEntityConstants.COLUMN_LAST_NAME)
    private String lastName;

    @Column(name = UserEntityConstants.COLUMN_PROFILE_IMAGE_URL)
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = UserEntityConstants.COLUMN_GENDER)
    private Gender gender;

    @Column(name = UserEntityConstants.COLUMN_BIRTH_DATE)
    private LocalDate birthDate;

    @Column(name = UserEntityConstants.COLUMN_HEIGHT_CM)
    private Double heightCm;

    @Column(name = UserEntityConstants.COLUMN_CURRENT_WEIGHT)
    private Double currentWeight;

    @Column(name = UserEntityConstants.COLUMN_GOAL_WEIGHT)
    private Double goalWeight;

    @Enumerated(EnumType.STRING)
    @Column(name = UserEntityConstants.COLUMN_ACTIVITY_LEVEL)
    private ActivityLevel activityLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = UserEntityConstants.COLUMN_FITNESS_GOAL)
    private FitnessGoal fitnessGoal;

    @CreationTimestamp
    @Column(nullable = false, updatable = false, name = UserEntityConstants.COLUMN_CREATED_AT)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false, name = UserEntityConstants.COLUMN_UPDATED_AT)
    private Instant updatedAt;

    @Override
    public String toString() {
        return "UserEntity{" + "id=" + id + ", firstName='" + firstName + '\'' + ", lastName='" + lastName + '\''
               + ", profileImageUrl='" + profileImageUrl + '\'' + ", gender=" + gender + ", birthDate=" + birthDate
               + ", heightCm=" + heightCm + ", currentWeight=" + currentWeight + ", goalWeight=" + goalWeight
               + ", activityLevel=" + activityLevel + ", fitnessGoal=" + fitnessGoal + ", createdAt=" + createdAt
               + ", updatedAt=" + updatedAt + '}';
    }
}
