package br.com.desafio.b2w.starWarsApi.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author Leonardo Rocha
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ErrorDetails {
    private int status;
    private String detail;
    private String developerMessage;
}
