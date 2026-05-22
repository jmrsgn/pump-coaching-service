package com.johnmartin.coaching.service;

import org.springframework.stereotype.Service;

import com.johnmartin.coaching.constants.error.AuthErrorConstants;
import com.johnmartin.coaching.dto.AuthUser;
import com.johnmartin.coaching.exceptions.UnauthorizedException;
import com.johnmartin.coaching.security.AuthContext;
import com.johnmartin.coaching.utilities.LoggerUtility;

@Service
public class AuthService {

    private static final Class<AuthService> clazz = AuthService.class;

    /**
     * Get auth user from AuthContext
     *
     * @return AuthUser
     */
    public AuthUser getAuthUser() {
        LoggerUtility.d(clazz, "Execute method: [getAuthUser]");
        AuthUser authUser = AuthContext.get();
        if (authUser == null) {
            LoggerUtility.d(clazz, "Auth user is null, will throw unauthorized exception");
            throw new UnauthorizedException(AuthErrorConstants.USER_IS_NOT_AUTHENTICATED);
        }
        return authUser;
    }
}
