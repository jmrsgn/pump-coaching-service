package com.johnmartin.coaching.constants.error;

public final class AuthErrorConstants {

    private AuthErrorConstants() {
    }

    public static final String BAD_REQUEST = "Bad request";
    public static final String USER_IS_NOT_AUTHENTICATED_OR_INVALID_TOKEN = "User is not authenticated or invalid token";
    public static final String MISSING_AUTHENTICATION_HEADER = "Missing Authentication Header";
}
