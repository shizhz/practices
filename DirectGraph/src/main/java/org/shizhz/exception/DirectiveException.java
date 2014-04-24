package org.shizhz.exception;

public class DirectiveException extends Exception {

    private static final long serialVersionUID = 1281141861422032564L;

    private final String message;

    @Override
    public String getMessage() {
        return message;
    }

    public DirectiveException(String message) {
        super(message);
        this.message = message;
    }

    public DirectiveException(Throwable e) {
        super(e);
        message = e.getMessage();
    }
}
