package com.johnmartin.coaching.constants.error;

public final class ValidationErrorConstants {

    private ValidationErrorConstants() {
    }

    public static final String USER_ID_IS_REQUIRED = "User ID is required";
    public static final String GENDER_IS_REQUIRED = "Gender is required";
    public static final String AGE_IS_REQUIRED = "Age is required";
    public static final String HEIGHT_IS_REQUIRED = "Height is required";
    public static final String CURRENT_WEIGHT_IS_REQUIRED = "Current weight is required";
    public static final String GOAL_WEIGHT_IS_REQUIRED = "Goal weight is required";
    public static final String ACTIVITY_LEVEL_IS_REQUIRED = "Activity level is required";
    public static final String FITNESS_GOAL_IS_REQUIRED = "Fitness goal is required";

    // Training Block
    public static final String TRAINING_BLOCK_NAME_IS_REQUIRED = "Training block name is required";
    public static final String NUMBER_OF_WEEKS_CANNOT_BE_NULL = "Number of weeks cannot be null";
    public static final String TRAINING_DAYS_CANNOT_BE_NULL = "Training days cannot be null";
    public static final String NUMBER_OF_WEEKS_MUST_BE_AT_LEAST_1 = "Number of weeks must be at least 1";
    public static final String TRAINING_DAYS_MUST_BE_AT_LEAST_1 = "Training days must be at least 1";
    public static final String TRAINING_SPLIT_CANNOT_BE_BLANK = "Training split cannot be blank";
    public static final String TRAINING_SPLIT_MUST_BE_AT_MOST_100_CHARACTERS = "Training split must be at most 100 characters";
    public static final String ESTIMATED_MACROS_CANNOT_BE_NULL = "Estimated macros cannot be null";
    public static final String ESTIMATED_MACROS_MUST_BE_ZERO_OR_GREATER = "Estimated macros must be zero or greater";
    public static final String TARGET_PROTEIN_CANNOT_BE_NULL = "Target protein cannot be null";
    public static final String TARGET_PROTEIN_MUST_BE_ZERO_OR_GREATER = "Target protein must be zero or greater";
    public static final String TARGET_CARBS_CANNOT_BE_NULL = "Target carbs cannot be null";
    public static final String TARGET_CARBS_MUST_BE_ZERO_OR_GREATER = "Target carbs must be zero or greater";
    public static final String TARGET_FAT_CANNOT_BE_NULL = "Target fat cannot be null";
    public static final String TARGET_FAT_MUST_BE_ZERO_OR_GREATER = "Target fat must be zero or greater";
    public static final String REQUIRED_DAILY_STEPS_CANNOT_BE_NULL = "Required daily steps cannot be null";
    public static final String REQUIRED_DAILY_STEPS_MUST_BE_GREATER_THAN_ZERO = "Required daily steps must be greater than zero";
    public static final String OTHER_NOTES_MUST_BE_AT_MOST_2000_CHARACTERS = "Other notes must be at most 2000 characters";
}
