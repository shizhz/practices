package org.shizhz.exception;

/**
 * This class <code>NoRouteExistingException</code> will be thrown when no route
 * existing between two cities when trying to find one.
 * 
 * @author shizhz
 * 
 */
public class NoRouteExistingException extends Exception {

    private static final long serialVersionUID = -1633818010172946410L;

    public NoRouteExistingException(String message) {
        super(message);
    }
}
