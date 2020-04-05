package br.com.desafio.b2w.starWarsApi.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author Leonardo Rocha
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ValidationErrorDetails extends ErrorDetails {
    private String title;
	private String timestamp;
    private String field;
    private String fieldMessage;
    private String detail;
    private String developerMessage;
    private int status;
}
