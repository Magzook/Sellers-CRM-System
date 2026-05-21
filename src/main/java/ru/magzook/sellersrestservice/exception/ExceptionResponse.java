package ru.magzook.sellersrestservice.exception;

import java.time.LocalDateTime;
import java.util.List;

public class ExceptionResponse {
    private final String message;
    private final LocalDateTime timestamp;
    private final List<String> details;

    public ExceptionResponse(String message, LocalDateTime timestamp, List<String> details) {
        this.message = message;
        this.timestamp = timestamp;
        this.details = details;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public List<String> getDetails() {
        return details;
    }
}
