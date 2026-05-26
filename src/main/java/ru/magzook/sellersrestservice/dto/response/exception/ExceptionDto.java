package ru.magzook.sellersrestservice.dto.response.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ExceptionDto(String message, LocalDateTime timestamp, List<String> details) {
}
