package com.johnmartin.coaching.constants.api;

public final class ApiConstants {

    private ApiConstants() {
    }

    public static final int RETRIES_COUNT = 5;

    public static final String API_BASE_V1 = "/api/v1";
    public static final String API_BASE_V1_INTERNAL = "/api/v1/internal";

    public static final class Path {

        private Path() {
        }

        public static final String ACTUATOR = "/actuator";
        public static final String HEALTH = "/health";

        // User
        public static final String API_USERS = API_BASE_V1 + "/users";
        public static final String CREATE_USER = "/create";
    }

    public static final class Params {

        private Params() {
        }

        // add params here
    }

    public static final class HttpError {

        private HttpError() {
        }

        public static final String UNAUTHORIZED = "Unauthorized";
        public static final String NOT_FOUND = "Not found";
        public static final String BAD_REQUEST = "Bad Request";
        public static final String CONFLICT = "Conflict";
        public static final String FORBIDDEN = "Forbidden";
        public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
    }
}
