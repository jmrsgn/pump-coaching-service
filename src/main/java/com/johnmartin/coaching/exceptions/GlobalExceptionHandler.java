package com.johnmartin.coaching.exceptions;

import com.johnmartin.coaching.constants.api.ApiConstants;
import com.johnmartin.coaching.constants.error.SystemErrorConstants;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.johnmartin.coaching.dto.response.common.ApiErrorResponse;
import com.johnmartin.coaching.dto.response.common.Result;
import com.johnmartin.coaching.utilities.LoggerUtility;
import com.johnmartin.coaching.utils.ApiResponseUtils;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Class<GlobalExceptionHandler> clazz = GlobalExceptionHandler.class;

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Result<ApiErrorResponse>> handleBadRequestException(BadRequestException ex) {
        LoggerUtility.e(clazz, ex.getMessage(), ex);
        return ApiResponseUtils.createBadRequestErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Result<ApiErrorResponse>> handleUnauthorizedException(UnauthorizedException ex) {
        LoggerUtility.e(clazz, ex.getMessage(), ex);
        return ApiResponseUtils.createUnauthorizedErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Result<ApiErrorResponse>> handleNotFoundException(NotFoundException ex) {
        LoggerUtility.e(clazz, ex.getMessage(), ex);
        return ApiResponseUtils.createNotFoundErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Result<ApiErrorResponse>> handleConflictException(ConflictException ex) {
        LoggerUtility.e(clazz, ex.getMessage(), ex);
        return ApiResponseUtils.createConflictErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Result<ApiErrorResponse>> handleForbiddenException(ForbiddenException ex) {
        LoggerUtility.e(clazz, ex.getMessage(), ex);
        return ApiResponseUtils.createForbiddenErrorResponse(ex.getMessage());
    }

    @ExceptionHandler({ HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class })
    public ResponseEntity<Result<ApiErrorResponse>> handleMalformedRequest(Exception ex) {
        return ApiResponseUtils.createBadRequestErrorResponse("Malformed request");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<ApiErrorResponse>> handleException(Exception ex) {
        LoggerUtility.e(clazz, ex.getMessage(), ex);
        return ApiResponseUtils.createInternalServerErrorResponse(SystemErrorConstants.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle method argument not valid exception (used to validate request objects)
     * 
     * @param ex
     *            - MethodArgumentNotValidException
     * @return ResponseEntity
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<ApiErrorResponse>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        var fieldError = ex.getBindingResult().getFieldErrors().get(0);
        LoggerUtility.w(clazz, "Validation failed for field {}", fieldError.getField());
        String message = fieldError.getDefaultMessage();
        return ApiResponseUtils.createBadRequestErrorResponse(message);
    }

    /**
     * Handle constraint violated exception (used for validating path variables)
     *
     * @param ex
     *            - ConstraintViolationException
     * @return ResponseEntity
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<ApiErrorResponse>> handleConstraintViolationException(ConstraintViolationException ex) {
        LoggerUtility.e(clazz, ex.getMessage(), ex);
        String message = ex.getConstraintViolations().iterator().next().getMessage();
        return ApiResponseUtils.createBadRequestErrorResponse(message);
    }

}
