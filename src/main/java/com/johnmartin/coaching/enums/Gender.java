package com.johnmartin.coaching.enums;

import com.johnmartin.coaching.constants.domain.UserConstants;

import lombok.Getter;

@Getter
public enum Gender {
    MALE(UserConstants.MALE), FEMALE(UserConstants.FEMALE);

    private final String value;

    Gender(String value) {
        this.value = value;
    }

    public static Gender fromValue(String value) {
        for (Gender gender : values()) {
            if (gender.value.equalsIgnoreCase(value)) {
                return gender;
            }
        }

        throw new IllegalArgumentException("Invalid gender: " + value);
    }
}
