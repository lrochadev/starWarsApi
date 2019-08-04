package br.com.desafio.b2w.starWarsApi.exception;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * 
 * @author Leonardo Rocha
 *
 */
@RestControllerAdvice
public class StarWarsApiExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
		Collector<CharSequence, ?, String> joining = Collectors.joining(",");
		String fields = fieldErrors.stream().map(FieldError::getField).collect(joining);
		String fieldMessages = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(joining);

		ValidationErrorDetails vedDetails = ValidationErrorDetails.Builder.newBuilder()
				.status(HttpStatus.BAD_REQUEST.value())
				.title("Ocorreu um erro!")
				.detail("Foram enviados parametros com o formato inválido.")
				.developerMessage(ex.getClass().getName())
				.field(fields)
				.fieldMessage(fieldMessages).build();

		return new ResponseEntity<>(vedDetails, HttpStatus.BAD_REQUEST);

	}

	@ExceptionHandler(PlanetaInexistenteException.class)
	public final ResponseEntity<?> handlePlanetaInexistenteException(PlanetaInexistenteException ex) {
		return new ResponseEntity<>(getError(ex, HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(SWAPIException.class)
    public final ResponseEntity<?> handleSWAPIException(SWAPIException ex) {
        return new ResponseEntity<>(getError(ex, HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }

	private ErrorDetails getError(RuntimeException ex, HttpStatus status) {
		ErrorDetails errorDetail = new ErrorDetails();
		errorDetail.setStatus(status.value());
		errorDetail.setDetail(ex.getMessage());
		errorDetail.setDeveloperMessage(ex.getClass().getName());

		return errorDetail;
	}
}
