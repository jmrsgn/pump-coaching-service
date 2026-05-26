package com.johnmartin.coaching.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.johnmartin.coaching.constants.domain.UserConstants;
import com.johnmartin.coaching.constants.error.SystemErrorConstants;

import lombok.Getter;

@Getter
public enum Gender {
    MALE(UserConstants.MALE), FEMALE(UserConstants.FEMALE);

    private final String value;

    Gender(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Gender fromValue(String value) {
        for (Gender gender : values()) {
            if (gender.value.equalsIgnoreCase(value)) {
                return gender;
            }
        }

        throw new IllegalArgumentException(SystemErrorConstants.INVALID_GENDER + ": " + value);
    }
}
