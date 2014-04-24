package org.shizhz.exception;

/**
 * The class <code>IllegalInputFormatException</code> will be thrown when the
 * input format is wrong.
 * 
 * @author shizhz
 * 
 */
public class IllegalInputException extends Exception {

    private static final long serialVersionUID = -8219848264016324847L;

    public IllegalInputException(String message) {
        super(message);
    }
}
