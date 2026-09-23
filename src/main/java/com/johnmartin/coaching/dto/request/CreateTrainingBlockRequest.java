package com.johnmartin.coaching.dto.request;

import com.johnmartin.coaching.constants.error.ValidationErrorConstants;
import jakarta.validation.constraints.*;

public record CreateTrainingBlockRequest(@NotBlank(message = ValidationErrorConstants.TRAINING_BLOCK_NAME_IS_REQUIRED) String trainingBlockName,
                                         @NotNull(message = ValidationErrorConstants.NUMBER_OF_WEEKS_CANNOT_BE_NULL) @Min(value = 1, message = ValidationErrorConstants.NUMBER_OF_WEEKS_MUST_BE_AT_LEAST_1) Integer numberOfWeeks,
                                         @NotNull(message = ValidationErrorConstants.TRAINING_DAYS_CANNOT_BE_NULL) @Min(value = 1, message = ValidationErrorConstants.TRAINING_DAYS_MUST_BE_AT_LEAST_1) Integer trainingDays,
                                         @NotBlank(message = ValidationErrorConstants.TRAINING_SPLIT_CANNOT_BE_BLANK) @Size(max = 100, message = ValidationErrorConstants.TRAINING_SPLIT_MUST_BE_AT_MOST_100_CHARACTERS) String trainingSplit,
                                         @NotNull(message = ValidationErrorConstants.ESTIMATED_MACROS_CANNOT_BE_NULL) @PositiveOrZero(message = ValidationErrorConstants.ESTIMATED_MACROS_MUST_BE_ZERO_OR_GREATER) Integer estimatedMacros,
                                         @NotNull(message = ValidationErrorConstants.TARGET_PROTEIN_CANNOT_BE_NULL) @PositiveOrZero(message = ValidationErrorConstants.TARGET_PROTEIN_MUST_BE_ZERO_OR_GREATER) Integer targetProteinInGrams,
                                         @NotNull(message = ValidationErrorConstants.TARGET_CARBS_CANNOT_BE_NULL) @PositiveOrZero(message = ValidationErrorConstants.TARGET_CARBS_MUST_BE_ZERO_OR_GREATER) Integer targetCarbsInGrams,
                                         @NotNull(message = ValidationErrorConstants.TARGET_FAT_CANNOT_BE_NULL) @PositiveOrZero(message = ValidationErrorConstants.TARGET_FAT_MUST_BE_ZERO_OR_GREATER) Integer targetFatInGrams,
                                         @NotNull(message = ValidationErrorConstants.REQUIRED_DAILY_STEPS_CANNOT_BE_NULL) @Positive(message = ValidationErrorConstants.REQUIRED_DAILY_STEPS_MUST_BE_GREATER_THAN_ZERO) Integer requiredDailySteps,
                                         @Size(max = 2000, message = ValidationErrorConstants.OTHER_NOTES_MUST_BE_AT_MOST_2000_CHARACTERS) String otherNotes) {
}
