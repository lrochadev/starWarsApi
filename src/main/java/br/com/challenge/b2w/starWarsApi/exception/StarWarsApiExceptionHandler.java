package br.com.challenge.b2w.starWarsApi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;
import static org.springframework.http.HttpStatus.*;

/**
 * @author Leonardo Rocha
 */
@RestControllerAdvice
public class StarWarsApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {

        final List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        final String fields = String.join(",", fieldErrors.stream().map(FieldError::getField).collect(toSet()));

        final ValidationErrorDetails
                vedDetails = ValidationErrorDetails
                .validationBuilder()
                .status(BAD_REQUEST.value())
                .title("Ocorreu um erro!")
                .detail("Parameters with invalid format were sent.")
                .developerMessage(ex.getClass().getName())
                .field(fields)
                .timestamp(LocalDateTime.now().format(ofPattern("dd/MM/yyyy HH:mm:ss")))
                .fieldMessage(fieldErrors.stream().map(FieldError::getDefaultMessage).collect(joining(",")))
                .build();

        return new ResponseEntity<>(vedDetails, BAD_REQUEST);
    }

    @ExceptionHandler(PlanetNotFoundException.class)
    public final ResponseEntity<?> handlePlanetNotFoundException(PlanetNotFoundException ex) {
        return new ResponseEntity<>(getError(ex, NOT_FOUND), NOT_FOUND);
    }

    @ExceptionHandler(SWAPIException.class)
    public final ResponseEntity<?> handleSWAPIException(SWAPIException ex) {
        return new ResponseEntity<>(getError(ex, INTERNAL_SERVER_ERROR), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return new ResponseEntity<>(getError(ex, BAD_REQUEST), BAD_REQUEST);
    }

    private ErrorDetails getError(Exception ex, HttpStatus status) {
        return ErrorDetails
                .builder()
                .status(status.value())
                .detail(ex.getMessage())
                .developerMessage(ex.getClass().getName()).build();
    }
}
