package com.johnmartin.coaching.security.custom;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.johnmartin.coaching.constants.api.ApiConstants;
import com.johnmartin.coaching.constants.error.AuthErrorConstants;
import com.johnmartin.coaching.dto.response.common.ApiErrorResponse;
import com.johnmartin.coaching.dto.response.common.Result;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter()
                .write(objectMapper.writeValueAsString(Result.failure(new ApiErrorResponse(HttpStatus.FORBIDDEN.value(),
                                                                                           ApiConstants.HttpError.BAD_REQUEST,
                                                                                           AuthErrorConstants.BAD_REQUEST))));
    }
}
