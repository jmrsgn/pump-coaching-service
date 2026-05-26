package com.johnmartin.coaching.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.johnmartin.coaching.constants.domain.UserConstants;
import com.johnmartin.coaching.constants.error.SystemErrorConstants;

public enum UserRole {
    COACH(UserConstants.COACH), CLIENT(UserConstants.CLIENT);

    private final String value;

    UserRole(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static UserRole fromValue(String value) {
        for (UserRole level : values()) {
            if (level.value.equalsIgnoreCase(value)) {
                return level;
            }
        }

        throw new IllegalArgumentException(SystemErrorConstants.INVALID_USER_ROLE + ": " + value);
    }
}
