package ru.yandex.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    private Mono<ResponseEntity<ErrorResponse>> handleEntityNotFoundException(final EntityNotFoundException e) {
        log.error(e.getMessage(), e);
        final ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND.value());
        return Mono.just(
                ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(errorResponse)
        );
    }

    @ExceptionHandler(EntityIsEmptyException.class)
    private Mono<ResponseEntity<ErrorResponse>> handleEntityIsEmptyException(final EntityIsEmptyException e) {
        log.error(e.getMessage(), e);
        final ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.NO_CONTENT.value());
        return Mono.just(
                ResponseEntity
                        .status(HttpStatus.NO_CONTENT)
                        .body(errorResponse)
        );
    }

    @ExceptionHandler(EntityConflictException.class)
    private Mono<ResponseEntity<ErrorResponse>> handleEntityConflictException(final EntityConflictException e) {
        log.error(e.getMessage(), e);
        final ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.CONFLICT.value());
        return Mono.just(
                ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(errorResponse)
        );
    }

    @ExceptionHandler(ImportCsvException.class)
    private Mono<ResponseEntity<ErrorResponse>> handleImportCsvException(final ImportCsvException e) {
        log.error(e.getMessage(), e);
        final ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        return Mono.just(
                ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(errorResponse)
        );
    }

    @ExceptionHandler(S3ConnectionException.class)
    private Mono<ResponseEntity<ErrorResponse>> handleS3ConnectionException(final S3ConnectionException e) {
        log.error(e.getMessage(), e);
        final ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        return Mono.just(
                ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(errorResponse)
        );
    }

    @ExceptionHandler(PaymentProcessException.class)
    private Mono<ResponseEntity<ErrorResponse>> handlePaymentProcessException(final PaymentProcessException e) {
        log.error(e.getMessage(), e);
        final ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        return Mono.just(
                ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(errorResponse)
        );
    }

    @ExceptionHandler(PaymentServiceUnavailableException.class)
    private Mono<ResponseEntity<ErrorResponse>> handlePaymentServiceUnavailableException(final PaymentServiceUnavailableException e) {
        log.error(e.getMessage(), e);
        final ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE.value());
        return Mono.just(
                ResponseEntity
                        .status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(errorResponse)
        );
    }
}