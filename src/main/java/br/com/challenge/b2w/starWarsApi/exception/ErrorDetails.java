package br.com.challenge.b2w.starWarsApi.exception;

import lombok.*;

/**
 * @author Leonardo Rocha
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDetails {
    private int status;
    private String detail;
    private String developerMessage;
}
