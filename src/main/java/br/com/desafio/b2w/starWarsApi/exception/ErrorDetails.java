package br.com.desafio.b2w.starWarsApi.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Leonardo Rocha
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDetails {
    private int status;
    private String detail;
    private String developerMessage;
}
