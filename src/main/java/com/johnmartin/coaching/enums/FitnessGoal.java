package com.johnmartin.coaching.enums;

import com.johnmartin.coaching.constants.domain.UserConstants;

import lombok.Getter;

@Getter
public enum FitnessGoal {
    FAT_LOSS(UserConstants.FAT_LOSS), MUSCLE_GAIN(UserConstants.MUSCLE_GAIN), MAINTENANCE(
            UserConstants.MAINTENANCE), RECOMPOSITION(UserConstants.RECOMPOSITION), STRENGTH(UserConstants.STRENGTH);

    private final String value;

    FitnessGoal(String value) {
        this.value = value;
    }

    public static FitnessGoal fromValue(String value) {
        for (FitnessGoal goal : values()) {
            if (goal.value.equalsIgnoreCase(value)) {
                return goal;
            }
        }

        throw new IllegalArgumentException("Invalid fitness goal: " + value);
    }
}
