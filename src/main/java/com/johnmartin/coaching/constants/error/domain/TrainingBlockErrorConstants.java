package com.johnmartin.coaching.constants.error.domain;

public final class TrainingBlockErrorConstants {
    private TrainingBlockErrorConstants() {
    }

    public static final String CLIENT_NOT_FOUND = "Client not found";
    public static final String CLIENT_IS_NOT_ENROLLED_TO_COACH = "Client is not enrolled to coach";
    public static final String ACTIVE_TRAINING_BLOCK_ALREADY_EXISTS_FOR_THIS_CLIENT = "Active training block already exists for this client";
    public static final String USER_AUTHENTICATION_REQUIRED = "User authentication required";
    public static final String NO_ACTIVE_TRAINING_BLOCK_EXISTS_FOR_THIS_CLIENT = "No active training block exists for this client";
}
