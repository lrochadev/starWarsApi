package br.com.challenge.b2w.starWarsApi.exception;

import lombok.*;

/**
 * @author Leonardo Rocha
 */
@Getter
@Setter
@Builder(builderMethodName = "validationBuilder")
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
