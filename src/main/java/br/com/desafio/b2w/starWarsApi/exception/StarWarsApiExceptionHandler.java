package br.com.desafio.b2w.starWarsApi.exception;

import br.com.desafio.b2w.starWarsApi.utils.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

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
public class StarWarsApiExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageUtil message;

    @Autowired
    public StarWarsApiExceptionHandler(MessageUtil message) {
        this.message = message;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
																  HttpStatus status,
																  WebRequest request) {

        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        String fields = String.join(",", fieldErrors.stream().map(FieldError::getField).collect(toSet()));

        ValidationErrorDetails
         vedDetails = ValidationErrorDetails
                .builder()
                .status(BAD_REQUEST.value())
                .title("Ocorreu um erro!")
                .detail(this.message.getMessage("mensagem.erro.argumento.invalido"))
                .developerMessage(ex.getClass().getName())
                .field(fields)
                .timestamp(LocalDateTime.now().format(ofPattern("dd/MM/yyyy HH:mm:ss")))
                .fieldMessage(fieldErrors.stream().map(FieldError::getDefaultMessage).collect(joining(",")))
                .build();

        return new ResponseEntity<>(vedDetails, BAD_REQUEST);

    }

    @ExceptionHandler(PlanetaInexistenteException.class)
    public final ResponseEntity<?> handlePlanetaInexistenteException(PlanetaInexistenteException ex) {
        return new ResponseEntity<>(getError(ex, NOT_FOUND), NOT_FOUND);
    }

    @ExceptionHandler(SWAPIException.class)
    public final ResponseEntity<?> handleSWAPIException(SWAPIException ex) {
        return new ResponseEntity<>(getError(ex, INTERNAL_SERVER_ERROR), INTERNAL_SERVER_ERROR);
    }

    private ErrorDetails getError(RuntimeException ex, HttpStatus status) {
        return ErrorDetails
                .builder()
                .status(status.value())
                .detail(ex.getMessage())
                .developerMessage(ex.getClass().getName()).build();
    }
}
