package com.johnmartin.coaching.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.johnmartin.coaching.constants.domain.UserConstants;
import com.johnmartin.coaching.constants.error.SystemErrorConstants;

import lombok.Getter;

@Getter
public enum CoachingStatus {
    ACTIVE(UserConstants.ACTIVE), INACTIVE(UserConstants.INACTIVE);

    private final String value;

    CoachingStatus(String value) {
        this.value = value;
    }

    @JsonCreator
    public static CoachingStatus fromCode(String value) {
        for (CoachingStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException(SystemErrorConstants.INVALID_COACHING_STATUS + ": " + value);
    }
}
