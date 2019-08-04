package br.com.desafio.b2w.starWarsApi.exception;

/**
 * 
 * @author Leonardo Rocha
 *
 */
public class SWAPIException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public SWAPIException(String msg) {
        super(msg);
    }
}
