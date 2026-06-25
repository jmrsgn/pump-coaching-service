package com.johnmartin.coaching.constants.api;

public final class ExternalServiceConstants {

    public static final String API_BASE_V1_INTERNAL = "/api/v1/internal";

    private ExternalServiceConstants() {
    }

    public static final class PumpAuthService {

        private PumpAuthService() {
        }

        // public static final String BASE_URL =
        // "http://pump-auth-service:8080";

        public static final String BASE_URL = "http://localhost:8081";
        public static final String API_VALIDATE = API_BASE_V1_INTERNAL + "/auth/validate";
    }

    public static final class PumpSocialService {

        private PumpSocialService() {
        }

        public static final String API_USERS = API_BASE_V1_INTERNAL + "/social/users";
        public static final String API_SEARCH_USER = API_USERS + "/search";
    }
}
