package com.johnmartin.coaching.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.johnmartin.coaching.constants.api.ApiConstants;
import com.johnmartin.coaching.dto.response.common.ApiErrorResponse;
import com.johnmartin.coaching.dto.response.common.Result;

public class ApiResponseUtils {

    private ApiResponseUtils() {
    }

    public static ResponseEntity<Result<ApiErrorResponse>> createInternalServerErrorResponse(String message) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(Result.failure(new ApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                                                       ApiConstants.HttpError.INTERNAL_SERVER_ERROR,
                                                                       message)));
    }

    public static ResponseEntity<Result<ApiErrorResponse>> createNotFoundErrorResponse(String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(Result.failure(new ApiErrorResponse(HttpStatus.NOT_FOUND.value(),
                                                                       ApiConstants.HttpError.NOT_FOUND,
                                                                       message)));
    }

    public static ResponseEntity<Result<ApiErrorResponse>> createUnauthorizedErrorResponse(String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                             .body(Result.failure(new ApiErrorResponse(HttpStatus.UNAUTHORIZED.value(),
                                                                       ApiConstants.HttpError.UNAUTHORIZED,
                                                                       message)));
    }

    public static ResponseEntity<Result<ApiErrorResponse>> createBadRequestErrorResponse(String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(Result.failure(new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(),
                                                                       ApiConstants.HttpError.BAD_REQUEST,
                                                                       message)));
    }

    public static ResponseEntity<Result<ApiErrorResponse>> createConflictErrorResponse(String message) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(Result.failure(new ApiErrorResponse(HttpStatus.CONFLICT.value(),
                                                                       ApiConstants.HttpError.CONFLICT,
                                                                       message)));
    }

    public static ResponseEntity<Result<ApiErrorResponse>> createForbiddenErrorResponse(String message) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(Result.failure(new ApiErrorResponse(HttpStatus.FORBIDDEN.value(),
                                                                       ApiConstants.HttpError.FORBIDDEN,
                                                                       message)));
    }
}
