package com.johnmartin.coaching.dto.request;

import java.time.LocalDate;

import com.johnmartin.coaching.constants.error.ValidationErrorConstants;
import com.johnmartin.coaching.enums.ActivityLevel;
import com.johnmartin.coaching.enums.FitnessGoal;
import com.johnmartin.coaching.enums.Gender;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateClientUserRequest(@NotBlank(message = ValidationErrorConstants.USER_ID_IS_REQUIRED) String userId,
                                      @NotNull(message = ValidationErrorConstants.GENDER_IS_REQUIRED) Gender gender,
                                      @NotNull(message = ValidationErrorConstants.BIRTH_DATE_IS_REQUIRED) LocalDate birthDate,
                                      @NotNull(message = ValidationErrorConstants.HEIGHT_IS_REQUIRED) @Positive Double heightCm,
                                      @NotNull(message = ValidationErrorConstants.CURRENT_WEIGHT_IS_REQUIRED) @Positive Double currentWeight,
                                      @NotNull(message = ValidationErrorConstants.GOAL_WEIGHT_IS_REQUIRED) @Positive Double goalWeight,
                                      @NotNull(message = ValidationErrorConstants.ACTIVITY_LEVEL_IS_REQUIRED) ActivityLevel activityLevel,
                                      @NotNull(message = ValidationErrorConstants.FITNESS_GOAL_IS_REQUIRED) FitnessGoal fitnessGoal) {
}
