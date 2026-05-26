package com.johnmartin.coaching.constants.api;

public final class ExternalServiceConstants {

    private ExternalServiceConstants() {
    }

    public static final class PumpAuthService {

        private PumpAuthService() {
        }

        // public static final String BASE_URL =
        // "http://pump-auth-service:8080";

        public static final String BASE_URL = "http://localhost:8081";
        public static final String API_VALIDATE = "/api/v1/internal/auth/validate";
    }

    public static final class PumpSocialService {

        private PumpSocialService() {
        }

        public static final String API_GET_USER = "/api/v1/internal/user";
    }
}
