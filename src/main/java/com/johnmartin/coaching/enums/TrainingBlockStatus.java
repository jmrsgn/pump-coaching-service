package com.johnmartin.coaching.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.johnmartin.coaching.constants.domain.UserConstants;
import com.johnmartin.coaching.constants.error.SystemErrorConstants;

public enum TrainingBlockStatus {
    ACTIVE(UserConstants.ACTIVE), INACTIVE(UserConstants.INACTIVE);

    private final String value;

    TrainingBlockStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static TrainingBlockStatus fromCode(String value) {
        for (TrainingBlockStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }

        throw new IllegalArgumentException(SystemErrorConstants.INVALID_TRAINING_BLOCK_STATUS + ": " + value);
    }
}
