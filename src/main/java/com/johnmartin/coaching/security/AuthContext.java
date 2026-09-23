package com.johnmartin.coaching.security;

import com.johnmartin.coaching.dto.AuthUser;

public final class AuthContext {

    private static final ThreadLocal<AuthUser> CONTEXT = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> INTERNAL_REQUEST = new ThreadLocal<>();

    private AuthContext() {
    }

    public static void set(AuthUser user, boolean internalRequest) {
        CONTEXT.set(user);
        INTERNAL_REQUEST.set(internalRequest);
    }

    public static AuthUser get() {
        return CONTEXT.get();
    }

    public static boolean isInternalRequest() {
        return Boolean.TRUE.equals(INTERNAL_REQUEST.get());
    }

    public static void clear() {
        CONTEXT.remove();
        INTERNAL_REQUEST.remove();
    }
}
