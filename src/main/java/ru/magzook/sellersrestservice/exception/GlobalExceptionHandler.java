package ru.magzook.sellersrestservice.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tools.jackson.databind.exc.InvalidFormatException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Validation failures
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationFail(MethodArgumentNotValidException exception) {
        List<String> details = exception.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                details);
    }

    // Entity with id not found
    @ExceptionHandler(EntityWithIdNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleEntityNotFound(EntityWithIdNotFoundException ex) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                Collections.emptyList());
    }

    // Unreadable JSON, including bad enum values
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> handleBadJson(HttpMessageNotReadableException ex) {
        if (ex.getCause() instanceof InvalidFormatException ife) {
            Class<?> targetType = ife.getTargetType();
            if (targetType.isEnum()) {
                return buildResponse(
                        HttpStatus.BAD_REQUEST,
                        "Unreadable enum value",
                        List.of("Accepted values are: " + Arrays.toString(targetType.getEnumConstants())));
            }
        }
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Unreadable JSON request",
                List.of(ex.getMessage()));
    }

    // DB data integrity violation (not supposed to happen, otherwise should add more robust validation)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Database data integrity violation",
                List.of(ex.getMessage()));
    }

    private ResponseEntity<ExceptionResponse> buildResponse(HttpStatus status, String message, List<String> details) {
        ExceptionResponse body = new ExceptionResponse(
                message,
                LocalDateTime.now(),
                details
        );
        return ResponseEntity.status(status).body(body);
    }
}
