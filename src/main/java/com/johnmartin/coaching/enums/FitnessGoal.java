package com.johnmartin.coaching.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.johnmartin.coaching.constants.domain.UserConstants;
import com.johnmartin.coaching.constants.error.SystemErrorConstants;

import lombok.Getter;

@Getter
public enum FitnessGoal {
    FAT_LOSS(UserConstants.FAT_LOSS), MUSCLE_GAIN(UserConstants.MUSCLE_GAIN), MAINTENANCE(
            UserConstants.MAINTENANCE), RECOMPOSITION(UserConstants.RECOMPOSITION), STRENGTH(UserConstants.STRENGTH);

    private final String value;

    FitnessGoal(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static FitnessGoal fromValue(String value) {
        for (FitnessGoal goal : values()) {
            if (goal.value.equalsIgnoreCase(value)) {
                return goal;
            }
        }

        throw new IllegalArgumentException(SystemErrorConstants.INVALID_FITNESS_GOAL + ": " + value);
    }
}
