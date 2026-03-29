package ru.practicum.ewm.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.ewm.dto.common.ApiError;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(final NotFoundException e) {
        log.error("404 Not Found: {}", e.getMessage(), e);
        return ApiError.builder()
                .status(HttpStatus.NOT_FOUND)
                .reason("The required object was not found.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(final ConflictException e) {
        log.error("409 Conflict: {}", e.getMessage(), e);
        return ApiError.builder()
                .status(HttpStatus.CONFLICT)
                .reason("For the requested operation the conditions are not met.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(final BadRequestException e) {
        log.error("400 Bad Request: {}", e.getMessage(), e);
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .reason("Incorrectly made request.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValid(final MethodArgumentNotValidException e) {
        log.error("400 Bad Request: {}", e.getMessage(), e);

        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.toList());

        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .reason("Incorrectly made request.")
                .message("Validation failed")
                .errors(errors)
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleConstraintViolation(final ConstraintViolationException e) {
        log.error("400 Bad Request: {}", e.getMessage(), e);

        List<String> errors = e.getConstraintViolations().stream()
                .map(violation -> String.format("Field: %s. Error: %s. Value: %s",
                        violation.getPropertyPath(),
                        violation.getMessage(),
                        violation.getInvalidValue()))
                .collect(Collectors.toList());

        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .reason("Incorrectly made request.")
                .message("Validation failed")
                .errors(errors)
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentTypeMismatch(final MethodArgumentTypeMismatchException e) {
        log.error("400 Bad Request: {}", e.getMessage(), e);

        String message = String.format("Failed to convert value of type '%s' to required type '%s'",
                e.getValue() != null ? e.getValue().getClass().getSimpleName() : "null",
                e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown");

        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .reason("Incorrectly made request.")
                .message(message)
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingServletRequestParameter(final MissingServletRequestParameterException e) {
        log.error("400 Bad Request: {}", e.getMessage(), e);

        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .reason("Incorrectly made request.")
                .message(String.format("Required request parameter '%s' is not present", e.getParameterName()))
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleHttpMessageNotReadable(final HttpMessageNotReadableException e) {
        log.error("400 Bad Request: {}", e.getMessage(), e);

        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .reason("Incorrectly made request.")
                .message("Malformed JSON request")
                .errors(List.of(e.getMessage()))
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolation(final DataIntegrityViolationException e) {
        log.error("409 Conflict: {}", e.getMessage(), e);

        return ApiError.builder()
                .status(HttpStatus.CONFLICT)
                .reason("Integrity constraint has been violated.")
                .message(e.getMostSpecificCause().getMessage())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleInternalServerError(final Exception e) {
        log.error("500 Internal Server Error: {}", e.getMessage(), e);

        return ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .reason("Internal server error.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }

    private String formatFieldError(FieldError fieldError) {
        return String.format("Field: %s. Error: %s. Value: %s",
                fieldError.getField(),
                fieldError.getDefaultMessage(),
                fieldError.getRejectedValue());
    }
}
