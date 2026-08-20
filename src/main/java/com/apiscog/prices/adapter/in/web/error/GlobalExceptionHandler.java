package com.apiscog.prices.adapter.in.web.error;

import com.apiscog.prices.application.exception.PriceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.time.format.DateTimeParseException;

@RestControllerAdvice
public final class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(PriceNotFoundException.class)
    ResponseEntity<ProblemDetail> handlePriceNotFound(
            PriceNotFoundException exception,
            HttpServletRequest request
    ) {
        return problem(
                HttpStatus.NOT_FOUND,
                "Price not found",
                exception.getMessage(),
                ApiErrorCode.PRICE_NOT_FOUND,
                request
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    ResponseEntity<ProblemDetail> handleMissingParameter(
            MissingServletRequestParameterException exception,
            HttpServletRequest request
    ) {
        return problem(
                HttpStatus.BAD_REQUEST,
                "Invalid request",
                "Required request parameter '%s' is missing".formatted(exception.getParameterName()),
                ApiErrorCode.INVALID_REQUEST,
                request
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<ProblemDetail> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request
    ) {
        return problem(
                HttpStatus.BAD_REQUEST,
                "Invalid request",
                "Request parameter '%s' has an invalid value".formatted(exception.getName()),
                ApiErrorCode.INVALID_REQUEST,
                request
        );
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    ResponseEntity<ProblemDetail> handleValidation(
            HandlerMethodValidationException exception,
            HttpServletRequest request
    ) {
        return problem(
                HttpStatus.BAD_REQUEST,
                "Invalid request",
                "One or more request parameters are invalid",
                ApiErrorCode.INVALID_REQUEST,
                request
        );
    }

    @ExceptionHandler(DateTimeParseException.class)
    ResponseEntity<ProblemDetail> handleInvalidDate(
            DateTimeParseException exception,
            HttpServletRequest request
    ) {
        return problem(
                HttpStatus.BAD_REQUEST,
                "Invalid request",
                "Request parameter 'applicationDate' has an invalid value",
                ApiErrorCode.INVALID_REQUEST,
                request
        );
    }

    @ExceptionHandler(DataAccessException.class)
    ResponseEntity<ProblemDetail> handlePersistenceFailure(
            DataAccessException exception,
            HttpServletRequest request
    ) {
        LOGGER.error("Persistence failure while processing {}", request.getRequestURI(), exception);
        return internalServerError(request);
    }

    private ResponseEntity<ProblemDetail> internalServerError(HttpServletRequest request) {
        return problem(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error",
                "An unexpected error occurred",
                ApiErrorCode.INTERNAL_ERROR,
                request
        );
    }

    private ResponseEntity<ProblemDetail> problem(
            HttpStatus status,
            String title,
            String detail,
            ApiErrorCode code,
            HttpServletRequest request
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("code", code.name());
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problemDetail);
    }
}
