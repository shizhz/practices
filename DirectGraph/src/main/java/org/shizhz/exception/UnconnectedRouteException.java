package org.shizhz.exception;

/**
 * This class <code>UnconnectedRouteException</code> will be thrown when two
 * unconnected routes are being added.
 * 
 * @author shizhz
 * 
 */
public class UnconnectedRouteException extends RuntimeException {

    private static final long serialVersionUID = -1963149751234940433L;

    public UnconnectedRouteException(String message) {
        super(message);
    }
}
