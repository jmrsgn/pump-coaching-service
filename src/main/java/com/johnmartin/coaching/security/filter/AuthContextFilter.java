package com.johnmartin.coaching.security.filter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.johnmartin.coaching.constants.SecurityConstants;
import com.johnmartin.coaching.constants.api.ApiConstants;
import com.johnmartin.coaching.constants.error.AuthErrorConstants;
import com.johnmartin.coaching.dto.AuthUser;
import com.johnmartin.coaching.dto.response.internal.AuthUserResponse;
import com.johnmartin.coaching.exceptions.UnauthorizedException;
import com.johnmartin.coaching.mapper.UserMapper;
import com.johnmartin.coaching.security.AuthContext;
import com.johnmartin.coaching.service.internal.client.AuthServiceClient;
import com.johnmartin.coaching.utilities.LoggerUtility;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthContextFilter extends BaseFilter {

    private static final Class<AuthContextFilter> clazz = AuthContextFilter.class;

    private final AuthServiceClient authServiceClient;

    private final String internalServiceToken;

    public AuthContextFilter(AuthServiceClient authServiceClient,
                             ObjectMapper objectMapper,
                             String internalServiceToken) {
        super(objectMapper);
        this.authServiceClient = authServiceClient;
        this.internalServiceToken = internalServiceToken;
    }

    @Override
    protected void doFilterAction(HttpServletRequest request, HttpServletResponse response) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        validateAuthorizationHeader(authHeader);

        if (isInternalRequest(authHeader)) {
            authenticateInternalRequest(request);
            return;
        }

        String requestId = (String) request.getAttribute(SecurityConstants.HttpHeaders.REQUEST_ID);
        authenticateUserRequest(authHeader, requestId);
    }

    @Override
    protected void afterRequest() {
        AuthContext.clear();
    }

    @Override
    protected boolean shouldSkip(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith(ApiConstants.Path.ACTUATOR);
    }

    private boolean isInternalRequest(String authHeader) {
        LoggerUtility.d(clazz, "Execute method: [isInternalRequest]");
        return SecurityConstants.HttpHeaders.BEARER.concat(internalServiceToken).equals(authHeader);
    }

    private void authenticateInternalRequest(HttpServletRequest request) {
        LoggerUtility.d(clazz, "Execute method: [authenticateInternalRequest]");
        String userId = request.getHeader(SecurityConstants.HttpHeaders.USER_ID);

        if (StringUtils.isBlank(userId)) {
            throw new UnauthorizedException(AuthErrorConstants.MISSING_USER_ID_HEADER);
        }

        // User is already authenticated, userId is the one only needed
        AuthUser authUser = new AuthUser(userId, null, null, null, null);
        authenticate(authUser);
    }

    private void authenticateUserRequest(String authHeader, String requestId) {
        LoggerUtility.d(clazz, "Execute method: [authenticateUserRequest]");
        LoggerUtility.d(clazz, String.format("requestId: [%s]", requestId));
        AuthUserResponse response = authServiceClient.validate(authHeader, requestId);
        authenticate(UserMapper.toAuthUser(response));
    }

    private void authenticate(AuthUser authUser) {
        LoggerUtility.d(clazz, "Execute method: [authenticate]");
        AuthContext.set(authUser);
        // REQUIRED: put authUser to Spring Security Context
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(authUser,
                                                                                                     null,
                                                                                                     null);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void validateAuthorizationHeader(String authHeader) {
        LoggerUtility.d(clazz, "Execute method: [validateAuthorizationHeader]");

        if (StringUtils.isBlank(authHeader)) {
            LoggerUtility.d(clazz, "Missing authentication header.");
            throw new UnauthorizedException(AuthErrorConstants.MISSING_AUTHENTICATION_HEADER);
        }
    }
}
