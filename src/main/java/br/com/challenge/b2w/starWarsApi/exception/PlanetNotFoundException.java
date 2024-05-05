package br.com.challenge.b2w.starWarsApi.exception;

import java.io.Serial;

/**
 * @author Leonardo Rocha
 */
public class PlanetNotFoundException extends RuntimeException {


    @Serial
    private static final long serialVersionUID = 5649136291700553549L;

    public PlanetNotFoundException(String msg) {
        super(msg);
    }
}
