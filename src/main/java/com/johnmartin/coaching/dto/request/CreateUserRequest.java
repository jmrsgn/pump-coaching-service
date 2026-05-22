package com.johnmartin.coaching.dto.request;

import java.time.LocalDate;
import java.util.UUID;

import com.johnmartin.coaching.constants.error.ValidationErrorConstants;
import com.johnmartin.coaching.enums.ActivityLevel;
import com.johnmartin.coaching.enums.FitnessGoal;
import com.johnmartin.coaching.enums.Gender;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(@NotNull UUID coachId,
                                @NotBlank(message = ValidationErrorConstants.FIRST_NAME_IS_REQUIRED) String firstName,
                                @NotBlank(message = ValidationErrorConstants.LAST_NAME_IS_REQUIRED) String lastName,
                                String profileImageUrl,
                                Gender gender,
                                LocalDate birthDate,
                                Double heightCm,
                                Double currentWeight,
                                Double goalWeight,
                                ActivityLevel activityLevel,
                                FitnessGoal fitnessGoal) {
}
