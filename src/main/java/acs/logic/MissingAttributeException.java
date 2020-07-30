package acs.logic;

public class MissingAttributeException extends RuntimeException {
    private static final long serialVersionUID = 5583337657323523358L;

    public MissingAttributeException() {
        super();
        // TODO Auto-generated constructor stub
    }

    public MissingAttributeException(
        String message,
        Throwable cause,
        boolean enableSuppression,
        boolean writableStackTrace
    ) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }

    public MissingAttributeException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public MissingAttributeException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public MissingAttributeException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }
}
