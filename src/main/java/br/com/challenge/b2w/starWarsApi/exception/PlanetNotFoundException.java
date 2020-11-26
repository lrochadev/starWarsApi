package br.com.challenge.b2w.starWarsApi.exception;

/**
 * @author Leonardo Rocha
 */
public class PlanetNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PlanetNotFoundException(String msg) {
        super(msg);
    }
}
