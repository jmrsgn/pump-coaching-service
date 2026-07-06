package com.johnmartin.coaching.constants;

public final class SecurityConstants {

    private SecurityConstants() {
    }

    public static final class HttpHeaders {

        private HttpHeaders() {
        }

        public static final String USER_ID = "userId";
        public static final String REQUEST_ID = "requestId";
        public static final String BEARER = "Bearer ";
    }
}
