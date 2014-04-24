package org.shizhz.exception;

public class UnRecognizedDirectiveException extends DirectiveException {

    private static final long serialVersionUID = -6348301308971280925L;

    public UnRecognizedDirectiveException(String message) {
        super(message);
    }
}
