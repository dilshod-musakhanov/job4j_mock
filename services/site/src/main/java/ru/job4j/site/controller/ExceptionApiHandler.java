package ru.job4j.site.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.site.domain.ErrorMessage;
import ru.job4j.site.exception.IdNotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionApiHandler {

    @ExceptionHandler(IdNotFoundException.class)
    public ResponseEntity<ErrorMessage> idNotFoundException(IdNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorMessage> handleHttpClientErrorException(HttpClientErrorException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage("The requested resource could not be found"));
    }

    @ExceptionHandler({NullPointerException.class, })
    public ResponseEntity<ErrorMessage> handleNullPointerException(NullPointerException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage("Some of fields empty"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessage> handleIllegalArgumentException(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage("Some of fields empty"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<Map<String, String>> errors = e.getFieldErrors().stream()
                .map(f -> Map.of(
                        f.getField(),
                        String.format("%s. Actual value: %s", f.getDefaultMessage(), f.getRejectedValue())
                ))
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(new ErrorMessage(errors.toString()));
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorMessage> sqlException(SQLException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage("Error when saving or retrieving data"));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorMessage> responseStatusException(ResponseStatusException exception, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(request.getRequestURI() + " not found"));
    }
}