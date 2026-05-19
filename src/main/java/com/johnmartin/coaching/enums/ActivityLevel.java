package com.johnmartin.coaching.enums;

import com.johnmartin.coaching.constants.domain.UserConstants;

import lombok.Getter;

@Getter
public enum ActivityLevel {
    SEDENTARY(UserConstants.SEDENTARY), LIGHTLY_ACTIVE(UserConstants.LIGHTLY_ACTIVE), MODERATELY_ACTIVE(
            UserConstants.MODERATELY_ACTIVE), VERY_ACTIVE(UserConstants.VERY_ACTIVE);

    private final String value;

    ActivityLevel(String value) {
        this.value = value;
    }

    public static ActivityLevel fromValue(String value) {
        for (ActivityLevel level : values()) {
            if (level.value.equalsIgnoreCase(value)) {
                return level;
            }
        }

        throw new IllegalArgumentException("Invalid activity level: " + value);
    }
}
