package acs.logic;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class ElementNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 5583337657323523358L;

    public ElementNotFoundException() {
        super();
        // TODO Auto-generated constructor stub
    }

    public ElementNotFoundException(
        String message,
        Throwable cause,
        boolean enableSuppression,
        boolean writableStackTrace
    ) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }

    public ElementNotFoundException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public ElementNotFoundException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public ElementNotFoundException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }
}
