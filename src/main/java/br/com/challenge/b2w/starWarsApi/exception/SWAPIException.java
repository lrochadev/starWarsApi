package br.com.challenge.b2w.starWarsApi.exception;

import java.io.Serial;

/**
 * @author Leonardo Rocha
 */
public class SWAPIException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 4627761934879364485L;

    public SWAPIException(String msg) {
        super(msg);
    }
}
