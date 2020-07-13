package br.com.desafio.b2w.starWarsApi.exception;

/**
 * @author Leonardo Rocha
 */
public class PlanetaInexistenteException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PlanetaInexistenteException(String msg) {
        super(msg);
    }
}
