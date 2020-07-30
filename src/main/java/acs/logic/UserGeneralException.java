package acs.logic;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class UserGeneralException extends RuntimeException {
    private static final long serialVersionUID = -5426648053109042109L;

    public UserGeneralException() {
        super();
    }

    public UserGeneralException(
        String message,
        Throwable cause,
        boolean enableSuppression,
        boolean writableStackTrace
    ) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public UserGeneralException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserGeneralException(String message) {
        super(message);
    }

    public UserGeneralException(Throwable cause) {
        super(cause);
    }
}
